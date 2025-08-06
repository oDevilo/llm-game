package io.github.devil.llm.avalon.game.store;

import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.step.Step;
import lombok.Data;

import java.util.List;

/**
 * @author Devil
 */
@Data
public class Store {

    private GameStore game;

    private List<StepStore> steps;

//    private List<StepResult> stepResults;

    private RoundStore round;

    private TurnStore turn;

    public static Store store(Game game, Game.Round round, Game.Round.Turn turn, List<Step> steps) {
        Store store = new Store();
        store.game = GameStore.store(game);
        store.round = RoundStore.store(round);
        store.turn = TurnStore.store(turn);
        store.steps = StepStore.store(steps);
        return store;
    }
}
