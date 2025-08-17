package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.TurnState;
import io.github.devil.llm.avalon.game.message.HostMessage;
import lombok.Data;

import java.util.Map;

/**
 * @author Devil
 */
public class TurnEndMessage extends HostMessage {

    private final MessageData data;

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

    public TurnEndMessage(String gameId, MessageData data) {
        super(gameId);
        this.data = data;
    }

    public String prompt() {
        if (data.failNumber > 0) {
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
        if (data.failNumber > 0) {
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
            "round", data.round,
            "turn", data.turn,
            "captainNumber", data.captainNumber,
            "teamNum", data.teamNum,
            "result", data.result,
            "failNumber", data.failNumber
        );
    }

    @Override
    public String type() {
        return Type.TurnEndMessage;
    }

    @Override
    public MData data() {
        return data;
    }

    @Data
    public static class MessageData implements MData {
        private int round;
        private int turn;
        private int captainNumber;
        private int teamNum;
        private TurnState.State result;
        private int failNumber;
    }

}