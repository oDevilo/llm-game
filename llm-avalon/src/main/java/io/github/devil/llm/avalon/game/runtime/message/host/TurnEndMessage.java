package io.github.devil.llm.avalon.game.runtime.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.store.MessageStore;

import java.util.Map;

/**
 * @author Devil
 */
public class TurnEndMessage extends HostMessage {

    private final int round;
    private final int turn;
    private final int captainNumber;
    private final int teamNum;
    private final Game.Round.Turn.Result result;
    private final int failNumber;

    private final String textTemplate = """
        当前是第{{round}}轮的第{{turn}}回合，本回合最终结果为{{result}}。
        """;

    // DraftTeamMessage
    private final String promptTemplate = """
        当前是第{{round}}轮的第{{turn}}回合，本回合最终结果为{{result}}。
        """;

    private final String failTextTemplate = """
        当前是第{{round}}轮的第{{turn}}回合，本回合最终结果为{{result}}，任务失败票数{{failNumber}}。
        """;

    private final String failPromptTemplate = """
        当前是第{{round}}轮的第{{turn}}回合，本回合最终结果为{{result}}，任务失败票数{{failNumber}}。
        """;

    public TurnEndMessage(int round, int turn, int captainNumber, int teamNum, Game.Round.Turn.Result result, int failNumber) {
        this.round = round;
        this.turn = turn;
        this.captainNumber = captainNumber;
        this.teamNum = teamNum;
        this.result = result;
        this.failNumber = failNumber;
    }

    public String prompt() {
        if (failNumber > 0) {
            return new PromptTemplate(failPromptTemplate).apply(
                variables()
            ).text();
        } else {
            return new PromptTemplate(promptTemplate).apply(
                variables()
            ).text();
        }
    }

    @Override
    public String text() {
        if (failNumber > 0) {
            return new PromptTemplate(failTextTemplate).apply(
                variables()
            ).text();
        } else {
            return new PromptTemplate(textTemplate).apply(
                variables()
            ).text();
        }
    }

    private Map<String, Object> variables() {
        return Map.of(
            "round", round,
            "turn", turn,
            "captainNumber", captainNumber,
            "teamNum", teamNum,
            "result", result,
            "failNumber", failNumber
        );
    }

    @Override
    public MessageStore store() {
        MessageStore.TurnEndMessageStore store = new MessageStore.TurnEndMessageStore();
        store.setRound(round);
        store.setCaptainNumber(captainNumber);
        store.setTurn(turn);
        store.setTeamNum(teamNum);
        store.setResult(result);
        store.setFailNumber(failNumber);
        return store;
    }

}
