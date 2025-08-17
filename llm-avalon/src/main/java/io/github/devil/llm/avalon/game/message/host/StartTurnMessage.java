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

    // DraftTeamMessage
    private final String promptTemplate = """
        你是本回合队长，请拟定队伍，并简单说明原因

        # 当前轮次
        {{round}}

        # 当前轮次回合
        {{turn}}

        # 当前队长
        {{captainNumber}}号玩家

        # 本轮所需出任务人数
        {{teamNum}}

        # 返回格式
        {
          "content": "拟定的队伍以及原因",
          "speakOrder": "发言顺序，从顺时针`CLOCKWISE`和逆时针`COUNTERCLOCKWISE`中选取一个"
        }
        """;

    public StartTurnMessage(String gameId, MessageData data) {
        super(gameId);
        this.data = data;
    }

    public String prompt() {
        return new PromptTemplate(promptTemplate).apply(
            Map.of(
                "round", data.round,
                "turn", data.turn,
                "captainNumber", data.captainNumber,
                "teamNum", data.teamNum
            )
        ).text();
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