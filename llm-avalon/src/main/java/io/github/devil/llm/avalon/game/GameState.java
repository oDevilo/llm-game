package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Devil
 */
public class GameState extends AgentState {

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
        "data", Channels.base(Game::new),
        "type", Channels.base(() -> "game")
    );

    public GameState(Map<String, Object> initData) {
        super(initData);
    }

    public Game game() {
        return game(data());
    }

    public static Game game(Map<String, Object> data) {
        String json = JacksonUtils.toJSONString(data.get("data"));
        return JacksonUtils.toType(json, Game.class);
    }

    public static Map<String, Object> from(GameState.Game game) {
        return Map.of(
            "data", game
        );
    }

    @Getter
    @Setter
    public static class Game implements Serializable {
        /**
         * 本局ID
         */
        private String id;

        /**
         * 玩家数
         */
        private int playerNumber;
        /**
         * 玩家对应角色
         */
        private Map<Integer, PlayerRole> playerRoles;
        /**
         * 队长顺序 存的是用户号码
         */
        private List<Integer> captainOrder;
        /**
         * 哪边阵营先完成3次任务
         */
        private CampType missionCamp = CampType.UNKNOWN;

        private State state = State.MISSION_STEP;
    }

    @Getter
    public enum State {
        MISSION_STEP("mission_step"),
        KILL_MERLIN("kill_merlin"),
        RED_WIN("red_win"),
        BLUE_WIN("blue_win"),
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
