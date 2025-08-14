package io.github.devil.llm.avalon.game;

import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Devil
 */
public class TurnState extends AgentState {

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
        "data", Channels.base(Turn::new)
    );

    public TurnState(Map<String, Object> initData) {
        super(initData);
    }

    public Turn turn() {
        Map<String, Object> data = data();
        return (Turn) data.get("data");
    }

    public static Map<String, Object> from(Turn turn) {
        return Map.of(
            "data", turn
        );
    }

    @Getter
    @Setter
    public static class Turn {

        private String gameId;
        /**
         * 第几轮
         */
        private int round;
        /**
         * 第几回合
         */
        private int turn;
        /**
         * 队长
         */
        private int captainNumber;
        /**
         * 车队人数
         */
        private int teamNumber;
        /**
         * 未发言的
         */
        private List<Integer> unSpeakers = new ArrayList<>();
        /**
         * 任务名单
         */
        private Set<Integer> team = new HashSet<>();
        /**
         * 投票结果
         */
        private Map<Integer, Boolean> voteResult = new HashMap<>();
        /**
         * 任务结果
         */
        private Map<Integer, Boolean> missionResult = new HashMap<>();
        /**
         * 本回合结果
         */
        private Result result = Result.NOT_END;
    }

    public enum Result {
        NOT_END,
        DRAWN, // 流局
        MISSION_COMPLETE, // 任务成功
        MISSION_FAIL, // 任务失败
        ;
    }

    @Getter
    public enum State {
        START(StateGraph.START),
        DRAFT_TEAM("draft_team"),
        SPEAK("speak"),
        SUMMARY("summary"),
        TEAM_VOTE("team_vote"),
        MISSION("mission"),
        END(StateGraph.END),
        ;
        private final String state;

        State(String state) {
            this.state = state;
        }
    }
}
