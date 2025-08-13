package io.github.devil.llm.avalon.game.player;

import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.MessageService;
import io.github.devil.llm.avalon.game.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.message.player.PlayerChatMessage;
import io.github.devil.llm.avalon.game.message.player.VoteMessage;
import io.github.devil.llm.avalon.game.player.assistant.AssistantFactory;
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
        String json = assistant.chat(messageService.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        DraftTeamMessage message = JacksonUtils.toType(json, DraftTeamMessage.class);
        addMessage(message);
        return SpeakOrder.parse(message.getSpeakOrder());
    }

    @Override
    public String chat() {
        String json = assistant.chat(messageService.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        PlayerChatMessage message = JacksonUtils.toType(json, PlayerChatMessage.class);
        addMessage(message);
        return message.getSpeak();
    }

    @Override
    public Set<Integer> team() {
        String json = assistant.chat(messageService.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        ConfirmTeamMessage message = JacksonUtils.toType(json, ConfirmTeamMessage.class);
        addMessage(message);
        return message.getTeamNumbers();
    }

    @Override
    public boolean vote() {
        String json = assistant.chat(messageService.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        VoteMessage message = JacksonUtils.toType(json, VoteMessage.class);
        addMessage(message);
        return message.isAgree();
    }

    @Override
    public boolean mission() {
        MissionMessage message;
        if (CampType.BLUE == role.camp) {
            message = new MissionMessage();
            message.setSuccess(true);
        } else {
            String json = assistant.chat(messageService.lastHostMessage().prompt()).content();
            json = LLMUtils.llmStringToJson(json);
            message = JacksonUtils.toType(json, MissionMessage.class);
        }
        addMessage(message);
        return message.isSuccess();
    }

    @Override
    public int kill() {
        String json = assistant.chat(messageService.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        KillResultMessage message = JacksonUtils.toType(json, KillResultMessage.class);
        return message.getKillNumber();
    }
}
