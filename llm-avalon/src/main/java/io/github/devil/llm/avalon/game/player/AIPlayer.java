package io.github.devil.llm.avalon.game.player;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.message.player.PlayerChatMessage;
import io.github.devil.llm.avalon.game.message.player.VoteMessage;
import io.github.devil.llm.avalon.game.player.assistant.AssistantFactory;
import io.github.devil.llm.avalon.game.service.MessageService;
import io.github.devil.llm.avalon.llm.Assistant;
import io.github.devil.llm.avalon.utils.LLMUtils;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;

import java.util.List;
import java.util.Set;

/**
 * @author Devil
 */
public class AIPlayer extends Player {

    private Assistant assistant;

    public AIPlayer(String gameId, int number, PlayerRole role, MessageService messageService) {
        super(gameId, number, role, messageService);
    }

    @Override
    public void init(List<Player> players) {
        this.assistant = AssistantFactory.build(new AssistantFactory.Request(
            gameId, number, players, messageService, role
        ));
    }

    @Override
    public SpeakOrder draftTeam() {
        String json = assistant.chat(messageService.lastHostMessage(gameId).prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        DraftTeamMessage.MessageData messageData = JacksonUtils.toType(json, DraftTeamMessage.MessageData.class);
        addMessage(new DraftTeamMessage(messageData));
        return SpeakOrder.parse(messageData.getSpeakOrder());
    }

    @Override
    public String chat() {
        String json = assistant.chat(messageService.lastHostMessage(gameId).prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        PlayerChatMessage.MessageData messageData = JacksonUtils.toType(json, PlayerChatMessage.MessageData.class);
        addMessage(new PlayerChatMessage(messageData));
        return messageData.getSpeak();
    }

    @Override
    public Set<Integer> team() {
        String json = assistant.chat(messageService.lastHostMessage(gameId).prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        ConfirmTeamMessage.MessageData messageData = JacksonUtils.toType(json, ConfirmTeamMessage.MessageData.class);
        addMessage(new ConfirmTeamMessage(messageData));
        return messageData.getTeamNumbers();
    }

    @Override
    public boolean vote() {
        String json = assistant.chat(messageService.lastHostMessage(gameId).prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        VoteMessage.MessageData messageData = JacksonUtils.toType(json, VoteMessage.MessageData.class);
        addMessage(new VoteMessage(messageData));
        return messageData.isAgree();
    }

    @Override
    public boolean mission() {
        MissionMessage.MessageData messageData;
        if (CampType.BLUE == role.camp) {
            messageData = new MissionMessage.MessageData();
            messageData.setSuccess(true);
        } else {
            String json = assistant.chat(messageService.lastHostMessage(gameId).prompt()).content();
            json = LLMUtils.llmStringToJson(json);
            messageData = JacksonUtils.toType(json, MissionMessage.MessageData.class);
        }
        addMessage(new MissionMessage(messageData));
        return messageData.isSuccess();
    }

    @Override
    public int kill() {
        String json = assistant.chat(messageService.lastHostMessage(gameId).prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        KillResultMessage.MessageData messageData = JacksonUtils.toType(json, KillResultMessage.MessageData.class);
        addMessage(new KillResultMessage(messageData));
        return messageData.getKillNumber();
    }
}
