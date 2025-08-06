package io.github.devil.llm.avalon.game.runtime;

import io.github.devil.llm.avalon.assistant.Assistant;
import io.github.devil.llm.avalon.assistant.AssistantFactory;
import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.runtime.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.PlayerChatMessage;
import io.github.devil.llm.avalon.game.runtime.message.player.VoteMessage;
import io.github.devil.llm.avalon.utils.LLMUtils;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;

import java.util.List;
import java.util.Set;

/**
 * AI玩家
 *
 * @author Devil
 */
public class AIPlayer extends Player {

    private Assistant assistant;

    public AIPlayer(String gameId, int number, Role role, MessageHistory messageHistory) {
        super(gameId, number, role, messageHistory);
    }

    @Override
    public void init(List<Player> players) {
        this.assistant = AssistantFactory.build(new AssistantFactory.Request(
            gameId, number, players, messageHistory, role
        ));
    }

    @Override
    public SpeakOrder draftTeam() {
        String json = assistant.chat(messageHistory.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
//        String json = json(assistant.chat(messageHistory.lastHostMessage().prompt()).content());
        DraftTeamMessage message = JacksonUtils.toType(json, DraftTeamMessage.class);
        addMessage(message);
        return SpeakOrder.parse(message.getSpeakOrder());
    }

    @Override
    public String chat() {
        String content = assistant.chat(messageHistory.lastHostMessage().prompt()).content();
//        String json = json(assistant.chat(messageHistory.lastHostMessage().prompt()).content());
//        PlayerChatMessage message = JacksonUtils.toType(json, PlayerChatMessage.class);
        PlayerChatMessage message = new PlayerChatMessage();
        message.setContent(content);
        addMessage(message);
        return message.getContent();
    }

    @Override
    public Set<Integer> team() {
        String json = assistant.chat(messageHistory.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
//        String json = json(assistant.chat(messageHistory.lastHostMessage().prompt()).content());
        ConfirmTeamMessage message = JacksonUtils.toType(json, ConfirmTeamMessage.class);
        addMessage(message);
        return message.getTeamNumbers();
    }

    @Override
    public boolean vote() {
        String json = assistant.chat(messageHistory.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
//        String json = json(assistant.chat(messageHistory.lastHostMessage().prompt()).content());
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
            String json = assistant.chat(messageHistory.lastHostMessage().prompt()).content();
            json = LLMUtils.llmStringToJson(json);
//            String json = json(assistant.chat(messageHistory.lastHostMessage().prompt()).content());
            message = JacksonUtils.toType(json, MissionMessage.class);
        }
        addMessage(message);
        return message.isSuccess();
    }

    @Override
    public int kill() {
        String json = assistant.chat(messageHistory.lastHostMessage().prompt()).content();
        json = LLMUtils.llmStringToJson(json);
        KillResultMessage message = JacksonUtils.toType(json, KillResultMessage.class);
        return message.getKillNumber();
    }

//    public String json(String text) {
//        // 定义正则表达式，用于匹配{}及其间的内容。
//        // 注意：这里的正则表达式是懒惰匹配，即尽可能少地匹配字符，以确保获取到的是最内层的{}对。
//        Pattern pattern = Pattern.compile("\\{(.*?)}");
//        Matcher matcher = pattern.matcher(text);
//        List<String> matches = new ArrayList<>();
//        while (matcher.find()) {
//            // matcher.group(1) 获取第一个捕获组，即{}中的内容
//            matches.add(matcher.group(1));
//        }
//        return matches.get(0);
//    }


}
