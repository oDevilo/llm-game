package io.github.devil.llm.avalon.game.message;

/**
 * @author Devil
 */
public interface Message {

    String text();

    Source source();

    enum Source {
        HOST, // 主持人
        PLAYER, // 玩家
        ;
    }

    interface Type {
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