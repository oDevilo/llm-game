package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.dao.entity.TurnEntity;
import io.github.devil.llm.avalon.dao.repository.TurnEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
import io.github.devil.llm.avalon.game.RoundState;
import io.github.devil.llm.avalon.game.TurnState;
import io.github.devil.llm.avalon.game.checkpoint.DBCheckpointSaver;
import io.github.devil.llm.avalon.game.message.host.AskCaptainSummaryMessage;
import io.github.devil.llm.avalon.game.message.host.AskVoteMessage;
import io.github.devil.llm.avalon.game.message.host.MissionStartMessage;
import io.github.devil.llm.avalon.game.message.host.StartTurnMessage;
import io.github.devil.llm.avalon.game.player.Player;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * @author Devil
 */
@Service
public class TurnService {

    @Resource
    private DBCheckpointSaver checkpointSaver;

    @Resource
    private MessageService messageService;

    @Resource
    private PlayerService playerService;

    @Resource
    private TurnEntityRepository turnEntityRepository;

    private CompiledGraph<TurnState> graph;

    @PostConstruct
    public void init() throws GraphStateException {
        AsyncEdgeAction<TurnState> speakCheckAction = new AsyncEdgeAction<>() {
            @Override
            public CompletableFuture<String> apply(TurnState state) {
                TurnState.Turn turn = state.turn();
                if (CollectionUtils.isEmpty(turn.getUnSpeakers())) {
                    return completedFuture("speak_end");
                } else {
                    return completedFuture("next_speak");
                }
            }
        };
        Map<String, String> speakCheckMapping = EdgeMappings.builder()
            .to(TurnState.State.SPEAK.getState(), "next_speak")
            .to(TurnState.State.SUMMARY.getState(), "speak_end")
            .build();


        JacksonStateSerializer<TurnState> stateSerializer = new JacksonStateSerializer<>(TurnState::new, JacksonUtils.defaultObjectMapper()) {
        };
        var graph = new StateGraph<>(TurnState.SCHEMA, stateSerializer)
            .addNode(TurnState.State.DRAFT_TEAM.getState(), draftTeamNode())
            .addNode(TurnState.State.SPEAK.getState(), speakNode())
            .addNode(TurnState.State.SUMMARY.getState(), summaryNode())
            .addNode(TurnState.State.TEAM_VOTE.getState(), teamVoteNode())
            .addNode(TurnState.State.MISSION.getState(), missionNode())
            .addNode(TurnState.State.DRAWN.getState(), stateNode(TurnState.State.DRAWN))
            .addNode(TurnState.State.MISSION_COMPLETE.getState(), stateNode(TurnState.State.MISSION_COMPLETE))
            .addNode(TurnState.State.MISSION_FAIL.getState(), stateNode(TurnState.State.MISSION_FAIL))
            // 开始
            .addEdge(StateGraph.START, TurnState.State.DRAFT_TEAM.getState())
            // 判断发言是否都结束
            .addConditionalEdges(TurnState.State.DRAFT_TEAM.getState(), speakCheckAction, speakCheckMapping)
            .addConditionalEdges(TurnState.State.SPEAK.getState(), speakCheckAction, speakCheckMapping)
            .addEdge(TurnState.State.SUMMARY.getState(), TurnState.State.TEAM_VOTE.getState())
            // 判断车队是否组件成功
            .addConditionalEdges(TurnState.State.TEAM_VOTE.getState(), new AsyncEdgeAction<TurnState>() {
                @Override
                public CompletableFuture<String> apply(TurnState state) {
                    TurnState.Turn turn = state.turn();
                    long agree = turn.getVoteResult().values().stream().filter(r -> r).count();
                    if (agree > turn.getTeamNumber() / 2) {
                        // 准备开车
                        return completedFuture("team_success");
                    } else {
                        // 流局
                        return completedFuture("drawn");
                    }
                }
            }, EdgeMappings.builder()
                .to(TurnState.State.MISSION.getState(), "team_success")
                .to(TurnState.State.DRAWN.getState(), "drawn")
                .build())
            // 判断任务是否成功
            .addConditionalEdges(TurnState.State.MISSION.getState(), new AsyncEdgeAction<TurnState>() {
                @Override
                public CompletableFuture<String> apply(TurnState state) {
                    TurnState.Turn turn = state.turn();
                    long missionFailNum = turn.getMissionResult().values().stream().filter(r -> !r).count();
                    if (missionFailNum > 0) {
                        return completedFuture("mission_complete");
                    } else {
                        return completedFuture("mission_fail");
                    }
                }
            }, EdgeMappings.builder()
                .to(TurnState.State.MISSION_COMPLETE.getState(), "mission_complete")
                .to(TurnState.State.MISSION_FAIL.getState(), "mission_fail")
                .build())
            // 结束
            .addEdge(TurnState.State.DRAWN.getState(), StateGraph.END)
            .addEdge(TurnState.State.MISSION_COMPLETE.getState(), StateGraph.END)
            .addEdge(TurnState.State.MISSION_FAIL.getState(), StateGraph.END);

        this.graph = graph.compile(CompileConfig.builder()
            .checkpointSaver(checkpointSaver)
            .build()
        );
    }

    private AsyncNodeAction<TurnState> draftTeamNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            // 队长发言，拟定队伍，决定发言顺序
            Player captain = playerService.getByIdAndNumber(turn.getGameId(), turn.getCaptainNumber());
            messageService.add(new StartTurnMessage(
                turn.getGameId(),
                new StartTurnMessage.MessageData(turn.getRound(), turn.getTurn(), turn.getCaptainNumber(), turn.getTeamNumber())
            ));
            // 确定发言顺序
            SpeakOrder speakOrder = captain.draftTeam(turn.getRound(), turn.getTurn(), turn.getCaptainNumber(), turn.getTeamNumber());
            List<Integer> speakers = speakers(turn.getGameId(), turn.getCaptainNumber(), speakOrder);
            turn.setUnSpeakers(speakers);
            turn.setState(TurnState.State.DRAFT_TEAM);
            turnEntityRepository.saveAndFlush(Converter.toEntity(turn));
            return TurnState.from(turn);
        });
    }

    private AsyncNodeAction<TurnState> speakNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            Player speaker = playerService.getByIdAndNumber(turn.getGameId(), turn.getUnSpeakers().getFirst());
            speaker.speak(speaker.getNumber());
            turn.getUnSpeakers().removeFirst();
            turn.setState(TurnState.State.SPEAK);
            turnEntityRepository.saveAndFlush(Converter.toEntity(turn));
            return TurnState.from(turn);
        });
    }

    private AsyncNodeAction<TurnState> summaryNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            messageService.add(new AskCaptainSummaryMessage(turn.getGameId()));
            Player captain = playerService.getByIdAndNumber(turn.getGameId(), turn.getCaptainNumber());
            Set<Integer> team = captain.confirmTeam();
            turn.setTeam(team);
            turn.setState(TurnState.State.SUMMARY);
            turnEntityRepository.saveAndFlush(Converter.toEntity(turn));
            return TurnState.from(turn);
        });
    }

    private AsyncNodeAction<TurnState> teamVoteNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            List<Player> players = playerService.getById(turn.getGameId());
            messageService.add(new AskVoteMessage(turn.getGameId(), new AskVoteMessage.MessageData(turn.getTeam())));
            for (Player player : players) {
                boolean vote = player.vote(turn.getTeam());
                turn.getVoteResult().put(player.getNumber(), vote);
            }
            turn.setState(TurnState.State.TEAM_VOTE);
            turnEntityRepository.saveAndFlush(Converter.toEntity(turn));
            return TurnState.from(turn);
        });
    }

    private AsyncNodeAction<TurnState> missionNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            messageService.add(new MissionStartMessage(turn.getGameId()));
            List<Player> players = playerService.getById(turn.getGameId());
            Set<Player> teamPlayers = players.stream()
                .filter(p -> turn.getTeam().contains(p.getNumber()))
                .collect(Collectors.toSet());
            for (Player teamPlayer : teamPlayers) {
                boolean mission = teamPlayer.mission();
                turn.getMissionResult().put(teamPlayer.getNumber(), mission);
            }
            turn.setState(TurnState.State.MISSION);
            turnEntityRepository.saveAndFlush(Converter.toEntity(turn));
            return TurnState.from(turn);
        });
    }

    private AsyncNodeAction<TurnState> stateNode(TurnState.State s) {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            turn.setState(s);
            turnEntityRepository.saveAndFlush(Converter.toEntity(turn));
            return TurnState.from(turn);
        });
    }

    private List<Integer> speakers(String gameId, Integer captain, SpeakOrder speakOrder) {
        int p = -1;
        List<Player> players = playerService.getById(gameId);
        for (int i = 0; i < players.size(); i++) {
            if (Objects.equals(captain, players.get(i).getNumber())) {
                p = i;
                break;
            }
        }
        List<Integer> speakers = new ArrayList<>();
        if (SpeakOrder.CLOCKWISE == speakOrder) {
            p = p + 1;
            while (speakers.size() < players.size() - 1) {
                if (p == players.size()) {
                    p = 0;
                }
                speakers.add(players.get(p).getNumber());
                p++;
            }
        } else {
            p = p - 1;
            while (speakers.size() < players.size() - 1) {
                if (p == 0) {
                    p = players.size() - 1;
                }
                speakers.add(players.get(p).getNumber());
                p--;
            }
        }
        return speakers;
    }

    public Optional<TurnState> invoke(TurnState.Turn turn, RunnableConfig config) {
        return graph.invoke(TurnState.from(turn), config);
    }

    public TurnState.Turn create(RoundState.Round round, TurnState.Turn preTurn) {
        // 获取历史turn，判断是第几个
        List<TurnEntity> entities = turnEntityRepository.findByGameId(round.getGameId());
        int pos = (entities == null ? Collections.emptyList() : entities).size();
        int t = (preTurn == null ? 0 : preTurn.getTurn()) + 1;
        TurnState.Turn turn = new TurnState.Turn();
        turn.setGameId(round.getGameId());
        turn.setRound(round.getRound());
        turn.setTurn(t);
        turn.setCaptainNumber(electCaptain(round, pos));
        turn.setTeamNumber(round.getTeamNum());

        TurnEntity entity = Converter.toEntity(turn);
        turnEntityRepository.saveAndFlush(entity);
        turn.setId(entity.getId());
        return turn;
    }

    /**
     * 选择当前队长队长
     * @return 队长的号码
     */
    private static int electCaptain(RoundState.Round round, int pos) {
        return round.getCaptainOrder().get(pos % round.getPlayerNumber());
    }

    public TurnState.Turn current(RoundState.Round round) {
        List<TurnEntity> entities = turnEntityRepository.findByGameIdAndRound(round.getGameId(), round.getRound());
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        TurnEntity entity = entities.stream().max(Comparator.comparingInt(TurnEntity::getTurn)).get();
        return Converter.toTurn(round, entity);
    }

}
