package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.game.player.Player;
import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.List;
import java.util.Map;

/**
 * @author Devil
 */
public class GameState extends AgentState {

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
        "data", Channels.base(Game::new)
    );

    public GameState(Map<String, Object> initData) {
        super(initData);
    }

    public Game game() {
        Map<String, Object> data = data();
        return (Game) data.get("data");
    }

    public static Map<String, Object> from(GameState.Game game) {
        return Map.of(
            "data", game
        );
    }

    @Getter
    @Setter
    public static class Game {
        /**
         * 本局ID
         */
        private String id;

        /**
         * 玩家数
         */
        private int playerNumber;
        /**
         * 分配的玩家
         */
        private List<Player> players;
        /**
         * 队长顺序 存的是用户号码
         */
        private List<Integer> captainOrder;
        /**
         * 如果 0 就选 captainOrder.get(0) 为队长
         */
        private int captainOrderPos;
        /**
         * 哪边阵营先完成3次任务
         */
        private CampType missionCamp;
        /**
         * 哪边阵营最终胜利
         */
        private CampType winCamp;
    }

    @Getter
    public enum State {
        START(StateGraph.START),
        MISSION_STEP("mission_step"),
        KILL_MERLIN("kill_merlin"),
        END(StateGraph.END),
        ;
        private final String state;

        State(String state) {
            this.state = state;
        }
    }
}
