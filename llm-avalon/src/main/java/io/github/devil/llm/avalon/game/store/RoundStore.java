package io.github.devil.llm.avalon.game.store;

import io.github.devil.llm.avalon.game.runtime.Game;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
@Data
public class RoundStore {

    private int round;

    private int teamNum;

    private List<TurnStore> historyTurns;

    private Game.Round.Result result;

    public static Game.Round load(RoundStore store) {
        if (store == null) {
            return null;
        }
        return new Game.Round(store.round, store.teamNum, TurnStore.loadTurns(store.historyTurns), store.result);
    }

    public static RoundStore store(Game.Round round) {
        if (round == null) {
            return null;
        }
        RoundStore store = new RoundStore();
        store.round = round.getRound();
        store.teamNum = round.getTeamNum();
        store.historyTurns = TurnStore.storeTurns(round.getHistoryTurns());
        store.result = round.getResult();
        return store;
    }

    public static List<RoundStore> storeRounds(List<Game.Round> rounds) {
        if (CollectionUtils.isEmpty(rounds)) {
            return new ArrayList<>();
        }
        return rounds.stream().map(RoundStore::store).collect(Collectors.toList());
    }

    public static List<Game.Round> loadRounds(List<RoundStore> stores) {
        if (CollectionUtils.isEmpty(stores)) {
            return new ArrayList<>();
        }
        return stores.stream().map(RoundStore::load).collect(Collectors.toList());
    }
}
