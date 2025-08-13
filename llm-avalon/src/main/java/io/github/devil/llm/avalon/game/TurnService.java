package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.message.host.AskCaptainSummaryMessage;
import io.github.devil.llm.avalon.game.message.host.AskSpeakMessage;
import io.github.devil.llm.avalon.game.message.host.AskVoteMessage;
import io.github.devil.llm.avalon.game.message.host.StartTurnMessage;
import io.github.devil.llm.avalon.game.player.Player;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private CompiledGraph<TurnState> graph;

    @PostConstruct
    public void init() throws GraphStateException {
        var graph = new StateGraph<>(TurnState.SCHEMA, TurnState::new)
            .addNode(TurnState.State.DRAFT_TEAM.getState(), draftTeamNode())
            .addNode(TurnState.State.SPEAK.getState(), speakNode())
            .addNode(TurnState.State.SUMMARY.getState(), summaryNode())
            .addNode(TurnState.State.TEAM_VOTE.getState(), teamVoteNode())
            .addNode(TurnState.State.MISSION.getState(), missionNode())
            // 开始
            .addEdge(GameState.State.START.getState(), TurnState.State.DRAFT_TEAM.getState())
            // 判断发言是否都结束
            .addConditionalEdges(TurnState.State.DRAFT_TEAM.getState(), new AsyncEdgeAction<TurnState>() {
                @Override
                public CompletableFuture<String> apply(TurnState state) {
                    TurnState.Turn turn = state.turn();
                    if (CollectionUtils.isEmpty(turn.getUnSpeakers())) {
                        return completedFuture("speak_end");
                    } else {
                        return completedFuture("next_speak");
                    }
                }
            }, EdgeMappings.builder()
                .to(TurnState.State.SPEAK.getState(), "next_speak")
                .to(TurnState.State.SUMMARY.getState(), "speak_end")
                .build())
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
                        turn.setResult(TurnState.Result.DRAWN);
                        return completedFuture("drawn");
                    }
                }
            }, EdgeMappings.builder()
                .to(TurnState.State.MISSION.getState(), "team_success")
                .to(TurnState.State.END.getState(), "drawn")
                .build())
            // 判断任务是否成功
            .addConditionalEdges(TurnState.State.TEAM_VOTE.getState(), new AsyncEdgeAction<TurnState>() {
                @Override
                public CompletableFuture<String> apply(TurnState state) {
                    TurnState.Turn turn = state.turn();
                    long missionFailNum = turn.getMissionResult().values().stream().filter(r -> !r).count();
                    if (missionFailNum > 0) {
                        turn.setResult(TurnState.Result.MISSION_FAIL);
                    } else {
                        turn.setResult(TurnState.Result.MISSION_COMPLETE);
                    }
                    return completedFuture("end");
                }
            }, EdgeMappings.builder()
                .to(TurnState.State.END.getState(), "end")
                .build());

        this.graph = graph.compile(CompileConfig.builder()
            .checkpointSaver(checkpointSaver)
            .build()
        );
    }

    private AsyncNodeAction<TurnState> draftTeamNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            // 队长发言，拟定队伍，决定发言顺序
            Player captain = turn.captain();
            messageService.add(new StartTurnMessage(turn.getRound(), turn.getTurn(), turn.getCaptainNumber(), turn.getTeamNumber()));
            // 确定发言顺序
            SpeakOrder speakOrder = captain.draftTeam();
            List<Integer> speakers = speakers(turn.getCaptainNumber(), speakOrder);
            turn.setUnSpeakers(speakers);
            return Map.of();
        });
    }

    private AsyncNodeAction<TurnState> speakNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            Player speaker = null;
            messageService.add(new AskSpeakMessage(speaker.getNumber()));
            speaker.chat();
            turn.getUnSpeakers().remove(0);
            return Map.of();
        });
    }

    private AsyncNodeAction<TurnState> summaryNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            messageService.add(new AskCaptainSummaryMessage());
            Player captain = turn.captain();
            Set<Integer> team = captain.team();
            turn.setTeam(team);
            return Map.of();
        });
    }

    private AsyncNodeAction<TurnState> teamVoteNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            List<Player> players = null; // todo
            messageService.add(new AskVoteMessage(turn.getTeam()));
            for (Player player : players) {
                boolean vote = player.vote();
                turn.getVoteResult().put(player.getNumber(), vote);
            }
            return Map.of();
        });
    }

    private AsyncNodeAction<TurnState> missionNode() {
        return node_async(state -> {
            TurnState.Turn turn = state.turn();
            List<Player> players = null; // todo
            Set<Player> teamPlayers = players.stream()
                .filter(p -> turn.getTeam().contains(p.getNumber()))
                .collect(Collectors.toSet());
            for (Player teamPlayer : teamPlayers) {
                boolean mission = teamPlayer.mission();
                turn.getMissionResult().put(teamPlayer.getNumber(), mission);
            }
            return Map.of();
        });
    }

    private List<Integer> speakers(Integer captain, SpeakOrder speakOrder) {
        int p = -1;
        List<Integer> players = null;
        for (int i = 0; i < players.size(); i++) {
            if (Objects.equals(captain, players.get(i))) {
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
                speakers.add(players.get(p));
                p++;
            }
        } else {
            p = p - 1;
            while (speakers.size() < players.size() - 1) {
                if (p == 0) {
                    p = players.size() - 1;
                }
                speakers.add(players.get(p));
                p--;
            }
        }
        return speakers;
    }

    public TurnState.Turn curentTurn() {
        return null; // todo
    }

}
