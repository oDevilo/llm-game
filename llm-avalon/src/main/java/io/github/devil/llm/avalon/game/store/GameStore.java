package io.github.devil.llm.avalon.game.store;

import io.github.devil.llm.avalon.game.runtime.AIPlayer;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.MessageHistory;
import io.github.devil.llm.avalon.game.runtime.Player;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一局游戏的持久化数据保存和恢复
 *
 * @author Devil
 */
@Data
public class GameStore {
    private String id;

    /**
     * 玩家数
     */
    private int playerNumber;
    /**
     * 号码 - 角色
     */
    private Map<Integer, String> playerRoles;
    /**
     * 历史消息
     */
    private List<MessageStore> messages;
    /**
     * 历史轮次
     */
    private List<RoundStore> historyRounds;
    /**
     * 队长顺序
     */
    private List<Integer> captainOrder;

    private int captainOrderPos;

    public static GameStore store(Game game) {
        GameStore store = new GameStore();
        store.setId(game.getId());
        store.setPlayerNumber(game.getPlayerNumber());
        Map<Integer, String> playerRoles = game.getPlayers().stream()
            .collect(Collectors.toMap(Player::getNumber, player -> player.getRole().value));
        store.setPlayerRoles(playerRoles);
        store.setMessages(MessageStore.store(game.getMessageHistory().messages()));
        store.setHistoryRounds(RoundStore.storeRounds(game.getHistoryRounds()));
        store.setCaptainOrder(game.getCaptainOrder());
        store.setCaptainOrderPos(game.getCaptainOrderPos());
        return store;
    }

    public static Game load(GameStore store) {
        MessageHistory messageHistory = new MessageHistory(MessageStore.load(store.messages));
        List<Game.Round> historyRounds = RoundStore.loadRounds(store.getHistoryRounds());
        List<Player> players = store.getPlayerRoles().entrySet().stream()
            .map(entry -> new AIPlayer(store.getId(), entry.getKey(), Player.Role.parse(entry.getValue()), messageHistory))
            .collect(Collectors.toList());
        for (Player player : players) {
            player.init(players);
        }
        return new Game(store.getId(), store.getPlayerNumber(),
            messageHistory, players, historyRounds, store.captainOrder, store.captainOrderPos
        );
    }


}
