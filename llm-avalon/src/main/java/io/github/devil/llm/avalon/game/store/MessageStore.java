package io.github.devil.llm.avalon.game.store;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.message.Message;
import io.github.devil.llm.avalon.game.runtime.message.host.AskCaptainSummaryMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.AskKillMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.AskSpeakMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.AskVoteMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.BeforeKillMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.StartTurnMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.TurnEndMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.PlayerChatMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.VoteMessage;
import io.github.devil.llm.avalon.utils.json.JacksonTypeIdResolver;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonTypeIdResolver(JacksonTypeIdResolver.class)
public abstract class MessageStore {

    private String type;

    public abstract String getType();

    public abstract Message message();

    public interface Type {
        String AskCaptainSummaryMessage = "AskCaptainSummaryMessage";
        String AskSpeakMessage = "AskSpeakMessage";
        String AskVoteMessage = "AskVoteMessage";
        String StartTurnMessage = "StartTurnMessage";
        String BeforeKillMessage = "BeforeKillMessage";
        String AskKillMessage = "AskKillMessage";

        String ConfirmTeamMessage = "ConfirmTeamMessage";
        String DraftTeamMessage = "DraftTeamMessage";
        String MissionMessage = "MissionMessage";
        String PlayerChatMessage = "PlayerChatMessage";
        String VoteMessage = "VoteMessage";
        String KillResultMessage = "KillResultMessage";
    }

    public static List<Message> load(List<MessageStore> stores) {
        if (CollectionUtils.isEmpty(stores)) {
            return new ArrayList<>();
        }
        return stores.stream().map(MessageStore::message).collect(Collectors.toList());
    }

    public static List<MessageStore> store(List<Message> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return new ArrayList<>();
        }
        return messages.stream().map(Message::store).collect(Collectors.toList());
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.AskCaptainSummaryMessage)
    public static class AskCaptainSummaryMessageStore extends MessageStore {

        @Override
        public String getType() {
            return Type.AskCaptainSummaryMessage;
        }

        @Override
        public Message message() {
            AskCaptainSummaryMessage message = new AskCaptainSummaryMessage();
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.AskSpeakMessage)
    public static class AskSpeakMessageStore extends MessageStore {

        private int number;

        @Override
        public String getType() {
            return Type.AskSpeakMessage;
        }

        @Override
        public Message message() {
            AskSpeakMessage message = new AskSpeakMessage(number);
            return message;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.AskVoteMessage)
    public static class AskVoteMessageStore extends MessageStore {

        private Set<Integer> team;

        @Override
        public String getType() {
            return Type.AskVoteMessage;
        }

        @Override
        public Message message() {
            AskVoteMessage message = new AskVoteMessage(team);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.StartTurnMessage)
    public static class StartTurnMessageStore extends MessageStore {

        private int round;
        private int turn;
        private int captainNumber;
        private int teamNum;

        @Override
        public String getType() {
            return Type.StartTurnMessage;
        }

        @Override
        public Message message() {
            StartTurnMessage message = new StartTurnMessage(round, turn, captainNumber, teamNum);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.BeforeKillMessage)
    public static class BeforeKillMessageStore extends MessageStore {

        @Override
        public String getType() {
            return Type.BeforeKillMessage;
        }

        @Override
        public Message message() {
            BeforeKillMessage message = new BeforeKillMessage();
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.AskKillMessage)
    public static class AskKillMessageStore extends MessageStore {

        @Override
        public String getType() {
            return Type.AskKillMessage;
        }

        @Override
        public Message message() {
            AskKillMessage message = new AskKillMessage();
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.StartTurnMessage)
    public static class TurnEndMessageStore extends MessageStore {

        private int round;
        private int turn;
        private int captainNumber;
        private int teamNum;
        private Game.Round.Turn.Result result;
        private int failNumber;

        @Override
        public String getType() {
            return Type.StartTurnMessage;
        }

        @Override
        public Message message() {
            TurnEndMessage message = new TurnEndMessage(round, turn, captainNumber, teamNum, result, failNumber);
            return message;
        }
    }


    // ========== 下面是玩家消息

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.ConfirmTeamMessage)
    public static class ConfirmTeamMessageStore extends MessageStore {

        private int number;

        private String content;

        private Set<Integer> teamNumbers;

        @Override
        public String getType() {
            return Type.ConfirmTeamMessage;
        }

        @Override
        public Message message() {
            ConfirmTeamMessage message = new ConfirmTeamMessage();
            message.setContent(content);
            message.setTeamNumbers(teamNumbers);
            message.setNumber(number);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.DraftTeamMessage)
    public static class DraftTeamMessageStore extends MessageStore {

        private int number;

        private String content;

        private String speakOrder;

        @Override
        public String getType() {
            return Type.DraftTeamMessage;
        }

        @Override
        public Message message() {
            DraftTeamMessage message = new DraftTeamMessage();
            message.setContent(content);
            message.setSpeakOrder(speakOrder);
            message.setNumber(number);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.MissionMessage)
    public static class MissionMessageStore extends MessageStore {

        private int number;

        private boolean success;

        @Override
        public String getType() {
            return Type.MissionMessage;
        }

        @Override
        public Message message() {
            MissionMessage message = new MissionMessage();
            message.setSuccess(success);
            message.setNumber(number);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.PlayerChatMessage)
    public static class PlayerChatMessageStore extends MessageStore {

        private int number;

        private String thinking;

        private String speak;

        @Override
        public String getType() {
            return Type.PlayerChatMessage;
        }

        @Override
        public Message message() {
            PlayerChatMessage message = new PlayerChatMessage();
            message.setSpeak(speak);
            message.setThinking(thinking);
            message.setNumber(number);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.VoteMessage)
    public static class VoteMessageStore extends MessageStore {

        private int number;

        private boolean agree;

        @Override
        public String getType() {
            return Type.VoteMessage;
        }

        @Override
        public Message message() {
            VoteMessage message = new VoteMessage();
            message.setAgree(agree);
            message.setNumber(number);
            return message;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.KillResultMessage)
    public static class KillResultMessageStore extends MessageStore {

        private int number;

        private int killNumber;

        @Override
        public String getType() {
            return Type.KillResultMessage;
        }

        @Override
        public Message message() {
            KillResultMessage message = new KillResultMessage();
            message.setNumber(number);
            message.setKillNumber(killNumber);
            return message;
        }
    }

}
