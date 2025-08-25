package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.message.HostMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Devil
 */
public class StartTurnMessage extends HostMessage {

    private final MessageData data;

    private final String textTemplate = """
        当前是第{{round}}轮的第{{turn}}回合，当前队长为{{captainNumber}}号玩家，本轮所需出任务人数为{{teamNum}}。
        请队长拟定队伍，并简单说明原因
        """;

    public StartTurnMessage(String gameId, Integer round, Integer turn, MessageData data) {
        super(gameId, round, turn);
        this.data = data;
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of(
                "round", data.round,
                "turn", data.turn,
                "captainNumber", data.captainNumber,
                "teamNum", data.teamNum
            )
        ).text();
    }

    @Override
    public String type() {
        return Type.StartTurnMessage;
    }

    @Override
    public MData data() {
        return data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageData implements MData {
        private int round;
        private int turn;
        private int captainNumber;
        private int teamNum;
    }
}