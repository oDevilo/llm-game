package io.github.devil.llm.avalon.game;

import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.Map;

/**
 * @author Devil
 */
public class RoundState extends AgentState {

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
        "data", Channels.base(Round::new)
    );

    public RoundState(Map<String, Object> initData) {
        super(initData);
    }

    public Round round() {
        Map<String, Object> data = data();
        return (Round) data.get("data");
    }

    @Getter
    @Setter
    public static class Round {
        /**
         * 第几轮
         */
        private int round;

        /**
         * 出任务的人数
         */
        private int teamNum;
        /**
         * 本轮结果
         */
        private Result result;
    }

    public enum Result {
        NOT_END,
        DRAWN_OVER, // 流局超过5次
        MISSION_COMPLETE, // 任务成功
        MISSION_FAIL, // 任务失败
        ;
    }

    @Getter
    public enum State {
        START(StateGraph.START),
        RUNNING("running"),
        END(StateGraph.END),
        ;
        private final String state;

        State(String state) {
            this.state = state;
        }
    }
}
