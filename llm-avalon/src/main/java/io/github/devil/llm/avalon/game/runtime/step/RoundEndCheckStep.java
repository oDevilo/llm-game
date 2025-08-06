package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.game.Engine;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.store.StepStore;

/**
 * @author Devil
 */
public class RoundEndCheckStep implements Step {

    @Override
    public StepResult execute() {
        if (roundEnd()) {
            Game game = Engine.game();
            Game.Round round = Engine.round();
            game.getHistoryRounds().add(round);
            return new RoundEndResult();
        } else {
            Engine.nextTurn();
            return new RoundNotEndResult();
        }
    }

    @Override
    public StepStore store() {
        StepStore.RoundEndCheckStepStore store = new StepStore.RoundEndCheckStepStore();
        return store;
    }

    public static class RoundEndResult implements StepResult {

        @Override
        public Step next() {
            return new GameEndCheckStep();
        }
    }

    public static class RoundNotEndResult implements StepResult {

        @Override
        public Step next() {
            return new TurnStartStep();
        }
    }

    private boolean roundEnd() {
        Game.Round.Turn turn = Engine.turn();
        if (turn == null) {
            return false;
        }
        Game.Round round = Engine.round();
        switch (turn.getResult()) {
            case DRAWN -> {
                if (turn.getTurn() == 5) {
                    round.setResult(Game.Round.Result.DRAWN_OVER);
                    return true;
                }
            }
            case MISSION_COMPLETE -> {
                round.setResult(Game.Round.Result.MISSION_COMPLETE);
                return true;
            }
            case MISSION_FAIL -> {
                round.setResult(Game.Round.Result.MISSION_FAIL);
                return true;
            }
        }
        return false;
    }

}
