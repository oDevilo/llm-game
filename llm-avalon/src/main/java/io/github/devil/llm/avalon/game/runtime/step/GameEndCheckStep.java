package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.game.Engine;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.store.StepStore;

import java.util.List;

/**
 * @author Devil
 */
public class GameEndCheckStep implements Step {

    @Override
    public StepResult execute() {
        Game game = Engine.game();
        if (gameEnd()) {
            if (CampType.BLUE == game.getMissionCamp()) {
                return new EndResult();
            } else {
                game.setWinCamp(CampType.RED);
                return new RedWinResult(); // 游戏结束
            }
        } else {
            Engine.nextRound();
            return new NotEndResult();
        }
    }

    @Override
    public StepStore store() {
        StepStore.GameEndCheckStepStore store = new StepStore.GameEndCheckStepStore();
        return store;
    }

    public static class EndResult implements StepResult {

        @Override
        public Step next() {
            return new KillStep();
        }

    }

    public static class NotEndResult implements StepResult {

        @Override
        public Step next() {
            return new RoundEndCheckStep();
        }

    }

    public static class RedWinResult implements StepResult {

        @Override
        public Step next() {
            return null;
        }

    }


    /**
     * 所有回合结束
     */
    private boolean gameEnd() {
        Game game = Engine.game();
        if (game.getMissionCamp() != null) {
            return true;
        }
        List<Game.Round> historyRounds = game.getHistoryRounds();
        if (historyRounds.isEmpty()) {
            return false;
        }
        // 流局超过5次
        int missionSuccess = 0;
        int missionFail = 0;
        for (int i = historyRounds.size() - 1; i >= 0; i--) {
            Game.Round r = historyRounds.get(i);
            if (Game.Round.Result.DRAWN_OVER == r.getResult()) {
                game.setMissionCamp(CampType.RED);
                return true;
            } else if (Game.Round.Result.MISSION_COMPLETE == r.getResult()) {
                missionSuccess++;
            } else if (Game.Round.Result.MISSION_FAIL == r.getResult()) {
                missionFail++;
            }

        }
        // 三次任务成功
        if (missionSuccess >= 3) {
            game.setMissionCamp(CampType.BLUE);
            return true;
        }
        // 三次任务失败
        if (missionFail >= 3) {
            game.setMissionCamp(CampType.RED);
            return true;
        }
        return false;
    }

}
