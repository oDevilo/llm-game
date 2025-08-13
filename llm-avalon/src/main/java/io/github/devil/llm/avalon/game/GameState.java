package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.RolePools;
import io.github.devil.llm.avalon.game.player.AIPlayer;
import io.github.devil.llm.avalon.game.player.Player;
import lombok.Getter;
import lombok.Setter;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        public static Game init(String id, int playerNumber, MessageService messageService) {
            // 获取角色池
            List<PlayerRole> rolePool = new ArrayList<>(RolePools.roles(playerNumber));
            // 分配角色
            List<Integer> availableNumbers = new ArrayList<>();
            List<Player> players = new ArrayList<>(); // todo 持久化？？
            Random random = new Random();
            for (int i = 0; i < playerNumber; i++) {
                int p = random.nextInt(rolePool.size());
                PlayerRole removed = rolePool.remove(p);
                int number = i + 1;
                availableNumbers.add(number);
                AIPlayer player = new AIPlayer(id, number, removed, messageService);
                players.add(player);
            }
            for (Player player : players) {
                player.init(players);
            }
            // 确定队长号码池
            List<Integer> captainOrder = new ArrayList<>();
            for (int i = 0; i < playerNumber; i++) {
                int p = random.nextInt(availableNumbers.size());
                Integer remove = availableNumbers.remove(p);
                captainOrder.add(remove);
            }
            Game game = new Game();
            game.id = id;
            game.playerNumber = playerNumber;
            game.captainOrderPos = 0;
            game.captainOrder = captainOrder;
            game.players = players;
            return game;
        }
    }

    @Getter
    public enum State {
        START(StateGraph.START),
        RUNNING("running"),
        KILL_MERLIN("kill_merlin"),
        END(StateGraph.END),
        ;
        private final String state;

        State(String state) {
            this.state = state;
        }
    }
}
