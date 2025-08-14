package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.dao.entity.RoundEntity;
import io.github.devil.llm.avalon.dao.repository.RoundEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
import io.github.devil.llm.avalon.game.DBCheckpointSaver;
import io.github.devil.llm.avalon.game.GameState;
import io.github.devil.llm.avalon.game.RoundState;
import io.github.devil.llm.avalon.game.TurnState;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @author Devil
 */
@Service
public class RoundService {

    @Resource
    private DBCheckpointSaver checkpointSaver;

    @Resource
    private TurnService turnService;

    @Resource
    private RoundEntityRepository roundEntityRepository;

    private CompiledGraph<RoundState> graph;

    @PostConstruct
    public void init() throws GraphStateException {
        var graph = new StateGraph<>(RoundState.SCHEMA, RoundState::new)
            .addNode(RoundState.State.RUNNING.getState(), inTurnNode())
            // 判断回合是否结束
            .addConditionalEdges(RoundState.State.START.getState(), new AsyncEdgeAction<RoundState>() {
                    @Override
                    public CompletableFuture<String> apply(RoundState state) {
                        RoundState.Round round = state.round();
                        if (roundEnd(state.round())) {
                            return completedFuture("round_end");
                        } else {
                            TurnState.Turn turn = turnService.current(round);
                            turnService.create(round, turn);
                            return completedFuture("next_turn");
                        }
                    }
                }, EdgeMappings.builder()
                    .to(RoundState.State.END.getState(), "round_end")
                    .to(RoundState.State.RUNNING.getState(), "next_turn")
                    .build()
            );

        this.graph = graph.compile(CompileConfig.builder()
            .checkpointSaver(checkpointSaver)
            .build()
        );
    }

    private AsyncNodeAction<RoundState> inTurnNode() {
        return node_async(state -> {
            RoundState.Round round = state.round();
            TurnState.Turn turn = turnService.current(round);
            RunnableConfig config = RunnableConfig.builder()
                .threadId(round.getGameId())
                .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
                .build();
            if (turn == null) {
                // 创建新的并执行
                turn = turnService.create(round, null);
                turnService.invoke(turn, config);
            } else if (TurnState.Result.NOT_END == turn.getResult()) {
                // 继续执行
                turnService.invoke(turn, config);
            } else {
                // 回合结束了
            }
            return Map.of();
        });
    }

    private boolean roundEnd(RoundState.Round round) {
        TurnState.Turn curTurn = turnService.current(round);
        if (curTurn == null) {
            return false;
        }
        switch (curTurn.getResult()) {
            case DRAWN -> {
                if (curTurn.getTurn() == 5) {
                    round.setResult(RoundState.Result.DRAWN_OVER);
                    return true;
                }
            }
            case MISSION_COMPLETE -> {
                round.setResult(RoundState.Result.MISSION_COMPLETE);
                return true;
            }
            case MISSION_FAIL -> {
                round.setResult(RoundState.Result.MISSION_FAIL);
                return true;
            }
        }
        return false;
    }

    public List<RoundState.Round> historyRounds(String gameId) {
        List<RoundEntity> entities = roundEntityRepository.findByGameId(gameId);
        return Converter.toRounds(entities);
    }

    public Optional<RoundState> invoke(RoundState.Round round, RunnableConfig config) {
        return graph.invoke(RoundState.from(round), config);
    }

    public RoundState.Round create(GameState.Game game, RoundState.Round preRound) {
        int r = (preRound == null ? 0 : preRound.getRound()) + 1;
        RoundState.Round round = new RoundState.Round();
        round.setGameId(game.getId());
        round.setRound(r);
        round.setTeamNum(teamNum(game.getPlayerNumber(), r));
        round.setPlayerNumber(game.getPlayerNumber());
        round.setCaptainOrder(game.getCaptainOrder());
        return round;
    }

    /**
     * 出任务人数
     */
    private int teamNum(int playerNumber, int round) {
        switch (playerNumber) {
            case 5: {
                switch (round) {
                    case 1, 3: {
                        return 2;
                    }
                    case 2, 4, 5: {
                        return 3;
                    }
                }
            }
            case 6: {
                switch (round) {
                    case 1: {
                        return 2;
                    }
                    case 2, 4: {
                        return 3;
                    }
                    case 3, 5: {
                        return 4;
                    }
                }
            }
            case 7: {
                switch (round) {
                    case 1: {
                        return 2;
                    }
                    case 2, 3: {
                        return 3;
                    }
                    case 4, 5: {
                        return 4;
                    }
                }
            }
            case 8, 9, 10: {
                switch (round) {
                    case 1: {
                        return 3;
                    }
                    case 2, 3: {
                        return 4;
                    }
                    case 4, 5: {
                        return 5;
                    }
                }
            }
        }
        return 0;
    }

    public RoundState.Round current(String gameId) {
        List<RoundEntity> entities = roundEntityRepository.findByGameId(gameId);
        RoundEntity entity = entities.stream().max(Comparator.comparingInt(RoundEntity::getRound)).get();
        return Converter.toRound(entity);
    }


}
