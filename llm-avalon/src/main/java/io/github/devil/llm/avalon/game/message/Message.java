package io.github.devil.llm.avalon.game.message;

import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public abstract class Message {

    protected String gameId;

    public Message(String gameId) {
        this.gameId = gameId;
    }

    public abstract String text();

    public abstract Source source();

    public abstract String type();

    public abstract MData data();

    public interface MData {
    }

    public enum Source {
        HOST, // 主持人
        PLAYER, // 玩家
        ;
    }

    public interface Type {
        String AskCaptainSummaryMessage = "AskCaptainSummaryMessage";
        String AskKillMessage = "AskKillMessage";
        String AskSpeakMessage = "AskSpeakMessage";
        String AskVoteMessage = "AskVoteMessage";
        String BeforeKillMessage = "BeforeKillMessage";
        String StartTurnMessage = "StartTurnMessage";
        String TurnEndMessage = "TurnEndMessage";

        String ConfirmTeamMessage = "ConfirmTeamMessage";
        String DraftTeamMessage = "DraftTeamMessage";
        String KillResultMessage = "KillResultMessage";
        String MissionMessage = "MissionMessage";
        String PlayerChatMessage = "PlayerChatMessage";
        String VoteMessage = "VoteMessage";
    }
}