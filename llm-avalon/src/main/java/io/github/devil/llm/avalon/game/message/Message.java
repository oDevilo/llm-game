package io.github.devil.llm.avalon.game.message;

import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public abstract class Message {

    protected String gameId;

    protected Integer round;

    protected Integer turn;

    public Message(String gameId) {
        this.gameId = gameId;
    }

    public Message(String gameId, Integer round, Integer turn) {
        this.gameId = gameId;
        this.round = round;
        this.turn = turn;
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
        String MissionStartMessage = "MissionStartMessage";
        String AskVoteMessage = "AskVoteMessage";
        String BeforeKillMessage = "BeforeKillMessage";
        String StartTurnMessage = "StartTurnMessage";
        String TurnEndMessage = "TurnEndMessage";

        String ConfirmTeamMessage = "ConfirmTeamMessage";
        String DraftTeamMessage = "DraftTeamMessage";
        String KillResultMessage = "KillResultMessage";
        String KillSpeakMessage = "KillSpeakMessage";
        String MissionMessage = "MissionMessage";
        String SpeakMessage = "SpeakMessage";
        String VoteMessage = "VoteMessage";
    }
}