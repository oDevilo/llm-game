package io.github.devil.llm.avalon.game.store;

import io.github.devil.llm.avalon.game.runtime.Game;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
@Data
public class TurnStore {

    private int turn;

    private int captainNumber;

    private Map<Integer, Boolean> voteResult;

    private Map<Integer, Boolean> missionResult;

    private Game.Round.Turn.Result result;

    public static TurnStore store(Game.Round.Turn turn) {
        if (turn == null) {
            return null;
        }
        TurnStore store = new TurnStore();
        store.turn = turn.getTurn();
        store.captainNumber = turn.getCaptainNumber();
        store.voteResult = turn.getVoteResult();
        store.missionResult = turn.getMissionResult();
        store.result = turn.getResult();
        return store;
    }

    public static List<TurnStore> storeTurns(List<Game.Round.Turn> turns) {
        if (CollectionUtils.isEmpty(turns)) {
            return new ArrayList<>();
        }
        return turns.stream().map(TurnStore::store).collect(Collectors.toList());
    }

    public static Game.Round.Turn load(TurnStore store) {
        if (store == null) {
            return null;
        }
        return new Game.Round.Turn(
            store.turn, store.captainNumber, store.voteResult, store.missionResult, store.result
        );
    }

    public static List<Game.Round.Turn> loadTurns(List<TurnStore> stores) {
        if (CollectionUtils.isEmpty(stores)) {
            return new ArrayList<>();
        }
        return stores.stream().map(TurnStore::load).collect(Collectors.toList());
    }
}
