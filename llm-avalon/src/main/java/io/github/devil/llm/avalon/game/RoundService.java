package io.github.devil.llm.avalon.game;

import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.utils.EdgeMappings;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
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

    private CompiledGraph<RoundState> graph;

    @PostConstruct
    public void init() throws GraphStateException {
        var graph = new StateGraph<>(RoundState.SCHEMA, RoundState::new)
            .addNode(RoundState.State.RUNNING.getState(), inTurnNode())
            // 判断回合是否结束
            .addConditionalEdges(RoundState.State.START.getState(), new AsyncEdgeAction<RoundState>() {
                    @Override
                    public CompletableFuture<String> apply(RoundState state) {
                        if (roundEnd(state.round())) {
                            return completedFuture("round_end");
                        } else {
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
            TurnState.Turn turn = turnService.curentTurn();
            if (turn == null) {
                // 创建新的并执行
            } else if (TurnState.Result.NOT_END == turn.getResult()) {
                // 继续执行
            } else {
                // 交由后面判断
            }
            return Map.of();
        });
    }

    private boolean roundEnd(RoundState.Round round) {
        TurnState.Turn curTurn = turnService.curentTurn();
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

    public RoundState.Round curentRound() {
        return null; // todo
    }


}
