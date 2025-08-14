package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.RolePools;
import io.github.devil.llm.avalon.game.player.AIPlayer;
import io.github.devil.llm.avalon.game.player.Player;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Devil
 */
@Service
public class PlayerService {

    private static final Map<String, List<Player>> CACHE = new ConcurrentHashMap<>();

    @Resource
    private MessageService messageService;

    public List<Player> createPlayers(String gameId, int playerNumber) {
        // 获取角色池
        List<PlayerRole> rolePool = new ArrayList<>(RolePools.roles(playerNumber));
        List<Player> players = new ArrayList<>();
        Random random = new Random();
        List<Integer> availableNumbers = new ArrayList<>();
        for (int i = 0; i < playerNumber; i++) {
            int p = random.nextInt(rolePool.size());
            PlayerRole removed = rolePool.remove(p);
            int number = i + 1;
            availableNumbers.add(number);
            Player player = new AIPlayer(gameId, number, removed, messageService);
            players.add(player);
        }
        for (Player player : players) {
            player.init(players);
        }
        CACHE.put(gameId, players);
        return players;
    }

    public Player getByIdAndNumber(String gameId, int number) {
        return CACHE.get(gameId).stream().filter(p -> p.getNumber() == number).findFirst().get();
    }

    public List<Player> getById(String gameId) {
        return CACHE.get(gameId);
    }

}
