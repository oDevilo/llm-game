package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.game.Engine;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.MessageHistory;
import io.github.devil.llm.avalon.game.runtime.Player;
import io.github.devil.llm.avalon.game.runtime.message.host.AskKillMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.AskSpeakMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.BeforeKillMessage;
import io.github.devil.llm.avalon.game.store.StepStore;

import java.util.List;

/**
 * @author Devil
 */
public class KillStep implements Step {

    @Override
    public StepResult execute() {
        Game game = Engine.game();
        MessageHistory messageHistory = game.getMessageHistory();
        messageHistory.add(new BeforeKillMessage());
        List<Player> reds = game.getPlayers().stream()
            .filter(p -> p.getRole().camp == CampType.RED)
            .toList();
        for (Player red : reds) {
            messageHistory.add(new AskSpeakMessage(red.getNumber()));
            red.chat();
        }
        messageHistory.add(new AskKillMessage());
        Player assassin = game.getPlayers().stream()
            .filter(p -> p.getRole() == Player.Role.ASSASSIN)
            .findFirst().get();
        Player merlin = game.getPlayers().stream()
            .filter(p -> p.getRole() == Player.Role.MERLIN)
            .findFirst().get();
        int killNumber = assassin.kill();
        if (merlin.getNumber() == killNumber) {
            game.setWinCamp(CampType.RED);
        } else {
            game.setWinCamp(CampType.BLUE);
        }
        return new Result();
    }

    @Override
    public StepStore store() {
        StepStore.KillStepStore store = new StepStore.KillStepStore();
        return store;
    }

    public static class Result implements StepResult {

        @Override
        public Step next() {
            return null;
        }
    }

}
