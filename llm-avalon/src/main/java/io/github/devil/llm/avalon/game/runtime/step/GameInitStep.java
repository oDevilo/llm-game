package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.constants.RolePools;
import io.github.devil.llm.avalon.game.Engine;
import io.github.devil.llm.avalon.game.runtime.AIPlayer;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.Player;
import io.github.devil.llm.avalon.game.store.StepStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Devil
 */
public class GameInitStep implements Step {

    @Override
    public StepResult execute() {
        Game game = Engine.game();
        int playerNumber = game.getPlayerNumber();
        List<Player> players = game.getPlayers();
        // 获取角色池
        List<Player.Role> rolePool = new ArrayList<>(rolePool(playerNumber));
        // 分配角色
        Random random = new Random();
        for (int i = 0; i < playerNumber; i++) {
            int p = random.nextInt(rolePool.size());
            Player.Role removed = rolePool.remove(p);
            AIPlayer player = new AIPlayer(game.getId(), i + 1, removed, game.getMessageHistory());
            players.add(player);
        }
        for (Player player : players) {
            player.init(players);
        }
        return new Result();
    }

    @Override
    public StepStore store() {
        StepStore.GameInitStepStore store = new StepStore.GameInitStepStore();
        return store;
    }

    public static class Result implements StepResult {

        @Override
        public Step next() {
            return new GameEndCheckStep();
        }

    }

    private static List<Player.Role> rolePool(int playerNumber) {
        return switch (playerNumber) {
            case 5 -> RolePools.FIVE_ROLE_POOL;
            case 6 -> RolePools.SIX_ROLE_POOL;
            case 7 -> RolePools.SEVEN_ROLE_POOL;
            case 8 -> RolePools.EIGHT_ROLE_POOL;
            case 9 -> RolePools.NIGHT_ROLE_POOL;
            case 10 -> RolePools.TEN_ROLE_POOL;
            default -> RolePools.FIVE_ROLE_POOL;
        };
    }

}
