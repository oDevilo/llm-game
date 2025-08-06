package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.Engine;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.MessageHistory;
import io.github.devil.llm.avalon.game.runtime.Player;
import io.github.devil.llm.avalon.game.runtime.message.host.AskSpeakMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.StartTurnMessage;
import io.github.devil.llm.avalon.game.store.StepStore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 */
public class TurnStartStep implements Step {

    /**
     * 代码先写这里后面再拆分
     */
    @Override
    public StepResult execute() {
        Game game = Engine.game();
        Game.Round round = Engine.round();
        Game.Round.Turn turn = Engine.turn();
        MessageHistory messageHistory = game.getMessageHistory();
        Player captain = turn.captain(game.getPlayers());
        // 队长发言，拟定队伍，决定发言顺序
        messageHistory.add(new StartTurnMessage(round.getRound(), turn.getTurn(), captain.getNumber(), round.getTeamNum()));
        SpeakOrder speakOrder = captain.draftTeam();
        // 其它人发言
        List<Player> speakers = speakers(speakOrder);
        for (Player speaker : speakers) {
            messageHistory.add(new AskSpeakMessage(speaker.getNumber()));
            speaker.chat();
        }
        return new Result();
    }

    @Override
    public StepStore store() {
        StepStore.TurnStartStepStore store = new StepStore.TurnStartStepStore();
        return store;
    }

    public static class Result implements StepResult {

        @Override
        public Step next() {
            return new TurnVoteStep();
        }
    }

    private List<Player> speakers(SpeakOrder speakOrder) {
        Game game = Engine.game();
        Game.Round.Turn turn = Engine.turn();
        int p = -1;
        List<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (turn.getCaptainNumber() == players.get(i).getNumber()) {
                p = i;
                break;
            }
        }
        List<Player> speakers = new ArrayList<>();
        if (SpeakOrder.CLOCKWISE == speakOrder) {
            p = p + 1;
            while (speakers.size() < players.size() - 1) {
                if (p == players.size()) {
                    p = 0;
                }
                speakers.add(players.get(p));
                p++;
            }
        } else {
            p = p - 1;
            while (speakers.size() < players.size() - 1) {
                if (p == 0) {
                    p = players.size() - 1;
                }
                speakers.add(players.get(p));
                p--;
            }
        }
        return speakers;
    }
}
