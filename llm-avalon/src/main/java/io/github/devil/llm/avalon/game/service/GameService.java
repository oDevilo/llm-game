package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.game.DBCheckpointSaver;
import io.github.devil.llm.avalon.game.GameState;
import io.github.devil.llm.avalon.game.RoundState;
import io.github.devil.llm.avalon.game.message.host.AskKillMessage;
import io.github.devil.llm.avalon.game.message.host.AskSpeakMessage;
import io.github.devil.llm.avalon.game.message.host.BeforeKillMessage;
import io.github.devil.llm.avalon.game.player.Player;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.utils.EdgeMappings;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @author Devil
 */
@Service
public class GameService {

    @Resource
    private DBCheckpointSaver checkpointSaver;
    @Resource
    private RoundService roundService;
    @Resource
    private MessageService messageService;
    @Resource
    private PlayerService playerService;

    private CompiledGraph<GameState> graph;

    @PostConstruct
    public void init() throws GraphStateException {
        var graph = new StateGraph<>(GameState.SCHEMA, GameState::new)
            .addNode(GameState.State.MISSION_STEP.getState(), missionStepNode())
            .addNode(GameState.State.KILL_MERLIN.getState(), killMerlinNode())
            // 游戏开始
            .addEdge(GameState.State.START.getState(), GameState.State.MISSION_STEP.getState())
            // 任务阶段结束
            .addConditionalEdges(GameState.State.MISSION_STEP.getState(), new AsyncEdgeAction<GameState>() {
                    @Override
                    public CompletableFuture<String> apply(GameState state) {
                        GameState.Game game = state.game();
                        // 判断任务进度
                        // 1. 任务结束 - 蓝色胜利 - 刺杀梅林
                        // 2. 任务结束 - 红色胜利 - 游戏结束
                        // 3. 任务没结束 - 开启新的一轮
                        if (missionStepEnd(game)) {
                            if (CampType.BLUE == game.getMissionCamp()) {
                                return completedFuture("to_kill");
                            } else {
                                return completedFuture("red_win");
                            }
                        } else {
                            RoundState.Round round = roundService.current(game.getId());
                            roundService.create(game, round);
                            return completedFuture("next_round");
                        }
                    }
                }, EdgeMappings.builder()
                    .to(GameState.State.KILL_MERLIN.getState(), "to_kill")
                    .to(GameState.State.END.getState(), "red_win")
                    .to(GameState.State.MISSION_STEP.getState(), "next_round")
                    .build()
            )
            .addEdge(GameState.State.KILL_MERLIN.getState(), GameState.State.END.getState());

        this.graph = graph.compile(CompileConfig.builder()
            .checkpointSaver(checkpointSaver)
            .build()
        );

    }

    private AsyncNodeAction<GameState> missionStepNode() {
        return node_async(state -> {
            GameState.Game game = state.game();
            RoundState.Round round = roundService.current(game.getId());
            RunnableConfig config = RunnableConfig.builder()
                .threadId(game.getId())
                .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
                .build();
            if (round == null) {
                // 创建新的并执行
                round = roundService.create(game, null);
                roundService.invoke(round, config);
            } else if (RoundState.Result.NOT_END == round.getResult()) {
                // 继续执行
                roundService.invoke(round, config);
            } else {
                // round 结束了
            }
            return Map.of();
        });
    }

    private AsyncNodeAction<GameState> killMerlinNode() {
        return node_async(state -> {
            GameState.Game game = state.game();
            messageService.add(new BeforeKillMessage());
            List<Player> players = game.getPlayers();
            List<Player> reds = players.stream()
                .filter(p -> p.getRole().camp == CampType.RED)
                .toList();
            for (Player red : reds) {
                messageService.add(new AskSpeakMessage(red.getNumber()));
                red.chat();
            }
            messageService.add(new AskKillMessage());
            Player assassin = players.stream()
                .filter(p -> p.getRole() == PlayerRole.ASSASSIN)
                .findFirst().get();
            Player merlin = players.stream()
                .filter(p -> p.getRole() == PlayerRole.MERLIN)
                .findFirst().get();
            int killNumber = assassin.kill();
            if (merlin.getNumber() == killNumber) {
                game.setWinCamp(CampType.RED);
            } else {
                game.setWinCamp(CampType.BLUE);
            }
            return Map.of();
        });
    }

    /**
     * 所有任务结束
     */
    private boolean missionStepEnd(GameState.Game game) {
        if (game.getMissionCamp() != null) {
            return true;
        }
        List<RoundState.Round> historyRounds = roundService.historyRounds(game.getId());
        if (historyRounds.isEmpty()) {
            return false;
        }
        // 流局超过5次
        int missionSuccess = 0;
        int missionFail = 0;
        for (int i = historyRounds.size() - 1; i >= 0; i--) {
            RoundState.Round r = historyRounds.get(i);
            if (RoundState.Result.DRAWN_OVER == r.getResult()) {
                game.setMissionCamp(CampType.RED);
                return true;
            } else if (RoundState.Result.MISSION_COMPLETE == r.getResult()) {
                missionSuccess++;
            } else if (RoundState.Result.MISSION_FAIL == r.getResult()) {
                missionFail++;
            }

        }
        // 三次任务成功
        if (missionSuccess >= 3) {
            game.setMissionCamp(CampType.BLUE);
            return true;
        }
        // 三次任务失败
        if (missionFail >= 3) {
            game.setMissionCamp(CampType.RED);
            return true;
        }
        return false;
    }

    public Optional<GameState> invoke(GameState.Game game, RunnableConfig config) {
        return graph.invoke(GameState.from(game), config);
    }

    public GameState.Game create(int playerNumber) {
        String id = "123"; // todo
        List<Player> players = playerService.createPlayers(id, playerNumber);

        // 分配角色
        List<Integer> availableNumbers = players.stream()
            .map(Player::getNumber)
            .collect(Collectors.toList());
        // 确定队长号码池
        Random random = new Random();
        List<Integer> captainOrder = new ArrayList<>();
        for (int i = 0; i < playerNumber; i++) {
            int p = random.nextInt(availableNumbers.size());
            Integer remove = availableNumbers.remove(p);
            captainOrder.add(remove);
        }
        GameState.Game game = new GameState.Game();
        game.setId(id);
        game.setPlayerNumber(playerNumber);
        game.setCaptainOrderPos(0);
        game.setCaptainOrder(captainOrder);
        game.setPlayers(players);
        return game;
    }

}
