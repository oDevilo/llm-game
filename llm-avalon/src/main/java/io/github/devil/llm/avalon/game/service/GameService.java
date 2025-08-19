package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.dao.entity.GameEntity;
import io.github.devil.llm.avalon.dao.repository.GameEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
import io.github.devil.llm.avalon.game.GameState;
import io.github.devil.llm.avalon.game.RoundState;
import io.github.devil.llm.avalon.game.checkpoint.DBCheckpointSaver;
import io.github.devil.llm.avalon.game.message.host.AskKillMessage;
import io.github.devil.llm.avalon.game.message.host.BeforeKillMessage;
import io.github.devil.llm.avalon.game.player.Player;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.serializer.plain_text.jackson.JacksonStateSerializer;
import org.bsc.langgraph4j.utils.EdgeMappings;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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
    @Resource
    private GameEntityRepository gameEntityRepository;

    private CompiledGraph<GameState> graph;

    @PostConstruct
    public void init() throws GraphStateException {
        JacksonStateSerializer<GameState> stateSerializer = new JacksonStateSerializer<>(GameState::new) {
        };
        var graph = new StateGraph<>(GameState.SCHEMA, stateSerializer)
            .addNode(GameState.State.MISSION_STEP.getState(), missionStepNode())
            .addNode(GameState.State.KILL_MERLIN.getState(), killMerlinNode())
            // 游戏开始
            .addEdge(StateGraph.START, GameState.State.MISSION_STEP.getState())
            // 任务阶段结束
            .addConditionalEdges(GameState.State.MISSION_STEP.getState(), new AsyncEdgeAction<GameState>() {
                    @Override
                    public CompletableFuture<String> apply(GameState state) {
                        GameState.Game game = state.game();
                        if (game.getState() == GameState.State.RED_WIN) {
                            return completedFuture("red_win");
                        } else if (game.getState() == GameState.State.KILL_MERLIN) {
                            return completedFuture("to_kill");
                        } else {
                            return completedFuture("next_round");
                        }
                    }
                }, EdgeMappings.builder()
                    .to(GameState.State.MISSION_STEP.getState(), "next_round")
                    .to(GameState.State.KILL_MERLIN.getState(), "to_kill")
                    .toEND("red_win")
                    .build()
            )
            // 结束
            .addEdge(GameState.State.KILL_MERLIN.getState(), StateGraph.END);

        this.graph = graph.compile(CompileConfig.builder()
            .checkpointSaver(checkpointSaver)
            .build()
        );

    }

    private AsyncNodeAction<GameState> missionStepNode() {
        return node_async(state -> {
            GameState.Game game = state.game();
            RoundState.Round round = roundService.current(game);
            if (round == null) {
                // 创建新的并执行
                round = roundService.create(game, null);
                RunnableConfig config = RunnableConfig.builder()
                    .threadId(game.getId() + "_" + round.getRound())
                    .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
                    .build();
                roundService.invoke(round, config);
            } else if (RoundState.State.RUNNING == round.getState()) {
                // 继续执行
                RunnableConfig config = RunnableConfig.builder()
                    .threadId(game.getId() + "_" + round.getRound())
                    .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
                    .build();
                roundService.invoke(round, config);
            }
            // 判断任务进度
            // 1. 任务结束 - 蓝色胜利 - 刺杀梅林
            // 2. 任务结束 - 红色胜利 - 游戏结束
            // 3. 任务没结束 - 开启新的一轮
            if (missionStepEnd(game)) {
                if (CampType.BLUE == game.getMissionCamp()) {
                    game.setState(GameState.State.KILL_MERLIN);
                } else {
                    game.setState(GameState.State.RED_WIN);
                }
            } else {
                roundService.create(game, round);
                game.setState(GameState.State.MISSION_STEP);
            }
            gameEntityRepository.saveAndFlush(Converter.toEntity(game));
            return GameState.from(game);
        });
    }

    private AsyncNodeAction<GameState> killMerlinNode() {
        return node_async(state -> {
            GameState.Game game = state.game();
            messageService.add(new BeforeKillMessage(game.getId()));
            List<Player> players = playerService.getById(game.getId());
            List<Player> reds = players.stream()
                .filter(p -> p.getRole().camp == CampType.RED)
                .toList();
            for (Player red : reds) {
                red.speak(red.getNumber());
            }
            messageService.add(new AskKillMessage(game.getId()));
            Player assassin = players.stream()
                .filter(p -> p.getRole() == PlayerRole.ASSASSIN)
                .findFirst().get();
            Player merlin = players.stream()
                .filter(p -> p.getRole() == PlayerRole.MERLIN)
                .findFirst().get();
            int killNumber = assassin.kill();
            if (merlin.getNumber() == killNumber) {
                game.setState(GameState.State.RED_WIN);
            } else {
                game.setState(GameState.State.BLUE_WIN);
            }
            gameEntityRepository.saveAndFlush(Converter.toEntity(game));
            return GameState.from(game);
        });
    }

    /**
     * 所有任务结束
     */
    private boolean missionStepEnd(GameState.Game game) {
        if (game.getMissionCamp() != CampType.UNKNOWN) {
            return true;
        }
        List<RoundState.Round> historyRounds = roundService.historyRounds(game);
        if (historyRounds.isEmpty()) {
            return false;
        }
        // 流局超过5次
        int missionSuccess = 0;
        int missionFail = 0;
        for (int i = historyRounds.size() - 1; i >= 0; i--) {
            RoundState.Round r = historyRounds.get(i);
            if (RoundState.State.DRAWN_OVER == r.getState()) {
                game.setMissionCamp(CampType.RED);
                return true;
            } else if (RoundState.State.MISSION_COMPLETE == r.getState()) {
                missionSuccess++;
            } else if (RoundState.State.MISSION_FAIL == r.getState()) {
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
            game.setState(GameState.State.RED_WIN);
            return true;
        }
        return false;
    }

    public Optional<GameState> invoke(GameState.Game game, RunnableConfig config) {
        return graph.invoke(GameState.from(game), config);
    }

    public GameState.Game create(int playerNumber) {
        String id = UUID.randomUUID().toString();
        // 分配角色
        List<Player> players = playerService.createPlayers(id, playerNumber);

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
        game.setCaptainOrder(captainOrder);
        game.setPlayerRoles(players.stream()
            .collect(Collectors.toMap(Player::getNumber, player -> player.getRole().value)));

        gameEntityRepository.saveAndFlush(Converter.toEntity(game));
        return game;
    }

    public GameState.Game get(String id) {
        GameEntity entity = gameEntityRepository.findById(id).get();
        return Converter.toGame(entity);
    }

}
