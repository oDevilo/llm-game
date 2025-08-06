package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.MessageHistory;
import io.github.devil.llm.avalon.game.runtime.step.GameInitStep;
import io.github.devil.llm.avalon.game.runtime.step.Step;
import io.github.devil.llm.avalon.game.runtime.step.StepResult;
import io.github.devil.llm.avalon.game.store.GameStore;
import io.github.devil.llm.avalon.game.store.RoundStore;
import io.github.devil.llm.avalon.game.store.StepStore;
import io.github.devil.llm.avalon.game.store.Store;
import io.github.devil.llm.avalon.game.store.TurnStore;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Devil
 */
public class Engine {

    private static final ThreadLocal<Game> GAME = new ThreadLocal<>();

    private static final ThreadLocal<Game.Round> ROUND = new ThreadLocal<>();

    private static final ThreadLocal<Game.Round.Turn> TURN = new ThreadLocal<>();

    private static final ThreadLocal<List<Step>> STEPS = new ThreadLocal<>();
//    public static ThreadLocal<List<StepResult>> STEP_RESULTS = new ThreadLocal<>();

    public static CampType start(String id, int playerNumber) {
        Game game = new Game(
            id, playerNumber, new MessageHistory(new ArrayList<>()),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0
        );
        GAME.set(game);
        loop(new GameInitStep());
        return game.getWinCamp();
    }

    private static void loop(Step lastStep) {
        Step step = lastStep;
        while (step != null) {
            step(step);
            save(); // 持久化
            StepResult result = step.execute();
//            stepResult(result);
            step = result.next();
        }
    }

    private static void save() {
        Store store = Store.store(game(), round(), turn(), STEPS.get());
        String text = JacksonUtils.toJSONString(store);
        try {
            FileUtils.writeByteArrayToFile(file(store.getGame().getId()), text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CampType load(String id) {
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file(id));
            Store store = JacksonUtils.toType(new String(bytes), Store.class);
            parseStore(store);
            loop(STEPS.get().getLast());
            return game().getWinCamp();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File file(String id) {
        return new File("avalon-" + id + ".bg");
    }

    private static void parseStore(Store store) {
        Game game = GameStore.load(store.getGame());
        GAME.set(game);
        Game.Round round = RoundStore.load(store.getRound());
        ROUND.set(round);
        Game.Round.Turn turn = TurnStore.load(store.getTurn());
        TURN.set(turn);
        List<Step> steps = StepStore.load(store.getSteps());
        STEPS.set(steps);
    }

    public static Game game() {
        return GAME.get();
    }

    public static Game.Round round() {
        return ROUND.get();
    }

    public static Game.Round.Turn turn() {
        return TURN.get();
    }

    public static void clearTurn() {
        TURN.remove();
    }

    public static void nextRound() {
        clearTurn();
        Game game = game();
        Game.Round curRound = round();
        int nextRound = 1;
        if (curRound != null) {
            nextRound = curRound.getRound() + 1;
        }
        Game.Round round = new Game.Round(
            nextRound,
            teamNum(game.getPlayerNumber(), nextRound),
            new ArrayList<>(),
            Game.Round.Result.NOT_END
        );
        ROUND.set(round);
    }

    public static void nextTurn() {
        Game.Round.Turn curTurn = turn();
        int nextTurn = 1;
        if (curTurn != null) {
            nextTurn = curTurn.getTurn() + 1;
        }
        Game.Round.Turn turn = new Game.Round.Turn(
            nextTurn,
            electCaptain(),
            new HashMap<>(),
            new HashMap<>(),
            Game.Round.Turn.Result.NOT_END
        );
        TURN.set(turn);
    }

    public static void step(Step step) {
        List<Step> steps = STEPS.get();
        if (steps == null) {
            steps = new ArrayList<>();
        }
        steps.add(step);
        STEPS.set(steps);
    }

//    public static void stepResult(StepResult result) {
//        List<StepResult> stepResults = STEP_RESULTS.get();
//        if (stepResults == null) {
//            stepResults = new ArrayList<>();
//        }
//        stepResults.add(result);
//        STEP_RESULTS.set(stepResults);
//    }

    /**
     * 选择当前队长队长
     * @return 队长的号码
     */
    private static int electCaptain() {
        Game game = game();
        int captainOrderPos = game.getCaptainOrderPos();
        Integer number = game.getCaptainOrder().get(captainOrderPos % game.getPlayerNumber());
        game.setCaptainOrderPos(captainOrderPos + 1);
        return number;
    }

    /**
     * 出任务人数
     */
    private static int teamNum(int playerNumber, int round) {
        switch (playerNumber) {
            case 5: {
                switch (round) {
                    case 1, 3: {
                        return 2;
                    }
                    case 2, 4, 5: {
                        return 3;
                    }
                }
            }
            case 6: {
                switch (round) {
                    case 1: {
                        return 2;
                    }
                    case 2, 4: {
                        return 3;
                    }
                    case 3, 5: {
                        return 4;
                    }
                }
            }
            case 7: {
                switch (round) {
                    case 1: {
                        return 2;
                    }
                    case 2, 3: {
                        return 3;
                    }
                    case 4, 5: {
                        return 4;
                    }
                }
            }
            case 8, 9, 10: {
                switch (round) {
                    case 1: {
                        return 3;
                    }
                    case 2, 3: {
                        return 4;
                    }
                    case 4, 5: {
                        return 5;
                    }
                }
            }
        }
        return 0;
    }
}
