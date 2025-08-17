package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.dao.entity.RoundEntity;
import io.github.devil.llm.avalon.dao.repository.RoundEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
import io.github.devil.llm.avalon.game.GameState;
import io.github.devil.llm.avalon.game.RoundState;
import io.github.devil.llm.avalon.game.TurnState;
import io.github.devil.llm.avalon.game.checkpoint.DBCheckpointSaver;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.Comparator;
import java.util.List;
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
        JacksonStateSerializer<RoundState> stateSerializer = new JacksonStateSerializer<>(RoundState::new) {
        };
        var graph = new StateGraph<>(RoundState.SCHEMA, stateSerializer)
            .addNode(RoundState.State.RUNNING.getState(), inTurnNode())
            .addEdge(StateGraph.START, RoundState.State.RUNNING.getState())
            // 判断回合是否结束
            .addConditionalEdges(RoundState.State.RUNNING.getState(), new AsyncEdgeAction<RoundState>() {
                    @Override
                    public CompletableFuture<String> apply(RoundState state) {
                        RoundState.Round round = state.round();

                        if (RoundState.State.MISSION_FAIL == round.getState()
                            || RoundState.State.MISSION_COMPLETE == round.getState()
                            || RoundState.State.DRAWN_OVER == round.getState()) {
                            return completedFuture("to_end");
                        } else {
                            return completedFuture("next_turn");
                        }
                    }
                }, EdgeMappings.builder()
                    .to(RoundState.State.RUNNING.getState(), "next_turn")
                    .toEND("to_end")
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
            if (turn == null) {
                // 创建新的并执行
                turn = turnService.create(round, null);
                RunnableConfig config = RunnableConfig.builder()
                    .threadId(round.getGameId() + "_" + round.getRound() + "_" + turn.getTurn())
                    .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
                    .build();
                turnService.invoke(turn, config);
            } else if (TurnState.State.DRAWN != turn.getState() && TurnState.State.MISSION_COMPLETE != turn.getState()
                && TurnState.State.MISSION_FAIL != turn.getState()) {
                // 继续执行
                RunnableConfig config = RunnableConfig.builder()
                    .threadId(round.getGameId() + "_" + round.getRound() + "_" + turn.getTurn())
                    .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
                    .build();
                turnService.invoke(turn, config);
            }

            turn = turnService.current(round); // 重新获取最新状态
            switch (turn.getState()) {
                case DRAWN -> {
                    if (turn.getTurn() == 5) {
                        round.setState(RoundState.State.DRAWN_OVER);
                    } else {
                        // 下回合
                        turnService.create(round, turn);
                        round.setState(RoundState.State.RUNNING);
                    }
                }
                case MISSION_COMPLETE -> {
                    round.setState(RoundState.State.MISSION_COMPLETE);
                }
                case MISSION_FAIL -> {
                    round.setState(RoundState.State.MISSION_FAIL);
                }
            }
            roundEntityRepository.saveAndFlush(Converter.toEntity(round));
            return RoundState.from(round); // 要更新的内容，不传递不会更新
        });
    }

    public List<RoundState.Round> historyRounds(GameState.Game game) {
        List<RoundEntity> entities = roundEntityRepository.findByGameId(game.getId());
        return Converter.toRounds(game, entities);
    }

    public Optional<RoundState> invoke(RoundState.Round round, RunnableConfig config) {
        return graph.invoke(RoundState.from(round), config);
    }

    public RoundState.Round create(GameState.Game game, RoundState.Round preRound) {
        int r = (preRound == null ? 0 : preRound.getRound()) + 1;
        RoundState.Round round = new RoundState.Round();
        round.setGameId(game.getId());
        round.setRound(r);
        round.setTeamNum(RoundState.Round.teamNum(game.getPlayerNumber(), r));
        round.setPlayerNumber(game.getPlayerNumber());
        round.setCaptainOrder(game.getCaptainOrder());

        RoundEntity entity = Converter.toEntity(round);
        roundEntityRepository.saveAndFlush(entity);
        round.setId(entity.getId());
        return round;
    }

    public RoundState.Round current(GameState.Game game) {
        List<RoundEntity> entities = roundEntityRepository.findByGameId(game.getId());
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        RoundEntity entity = entities.stream().max(Comparator.comparingInt(RoundEntity::getRound)).get();
        return Converter.toRound(game, entity);
    }


}
