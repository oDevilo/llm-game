package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Devil
 */
public class RoundState extends AgentState {

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
        "data", Channels.base(Round::new),
        "type", Channels.base(() -> "round")
    );

    public RoundState(Map<String, Object> initData) {
        super(initData);
    }

    public static Map<String, Object> from(RoundState.Round round) {
        return Map.of(
            "data", round
        );
    }

    public Round round() {
        return round(data());
    }

    public static Round round(Map<String, Object> data) {
        String json = JacksonUtils.toJSONString(data.get("data"));
        return JacksonUtils.toType(json, Round.class);
    }

    @Getter
    @Setter
    public static class Round implements Serializable {

        private String gameId;
        /**
         * 第几轮
         */
        private int round;

        /**
         * 出任务的人数
         */
        private int teamNum;
        /**
         * 玩家数
         */
        private int playerNumber;
        /**
         * 队长顺序 存的是用户号码
         */
        private List<Integer> captainOrder;

        private State state = State.RUNNING;

        /**
         * 出任务人数
         */
        public static int teamNum(int playerNumber, int round) {
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
    }

    @Getter
    public enum State {
        RUNNING("running"),
        DRAWN_OVER("drawn_over"), // 流局超过5次
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
