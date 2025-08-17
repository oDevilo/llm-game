package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.io.Serializable;
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
        "data", Channels.base(Turn::new),
        "type", Channels.base(() -> "turn")
    );

    public TurnState(Map<String, Object> initData) {
        super(initData);
    }

    public Turn turn() {
        return turn(data());
    }

    public static Turn turn(Map<String, Object> data) {
        String json = JacksonUtils.toJSONString(data.get("data"));
        return JacksonUtils.toType(json, Turn.class);
    }

    public static Map<String, Object> from(Turn turn) {
        return Map.of(
            "data", turn
        );
    }

    @Getter
    @Setter
    public static class Turn implements Serializable {

        private Long id;

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

        private State state = State.DRAFT_TEAM;
    }

    @Getter
    public enum State {
        DRAFT_TEAM("draft_team"),
        SPEAK("speak"),
        SUMMARY("summary"),
        TEAM_VOTE("team_vote"),
        MISSION("mission"),
        DRAWN("drawn"), // 流局
        MISSION_COMPLETE("mission_complete"), // 任务成功
        MISSION_FAIL("mission_fail"), // 任务失败
        ;
        private final String state;

        State(String state) {
            this.state = state;
        }

        public static State parse(String value) {
            for (State v : values()) {
                if (v.state.equals(value)) {
                    return v;
                }
            }
            return null;
        }
    }
}
