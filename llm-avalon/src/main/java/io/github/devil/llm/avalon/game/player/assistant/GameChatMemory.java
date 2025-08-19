package io.github.devil.llm.avalon.game.player.assistant;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import io.github.devil.llm.avalon.game.message.Message;
import io.github.devil.llm.avalon.game.message.PlayerMessage;
import io.github.devil.llm.avalon.game.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.message.player.VoteMessage;
import io.github.devil.llm.avalon.game.service.MessageService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 */
public class GameChatMemory implements ChatMemory {

    private final String id;

    private final String gameId;

    private final int number;

    private final MessageService messageService;

    private SystemMessage systemMessage;

    private ChatMessage inputMessage;

    public GameChatMemory(String gameId, int number, MessageService messageService) {
        this.number = number;
        this.gameId = gameId;
        this.id = gameId + "_" + number;
        this.messageService = messageService;
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void add(ChatMessage message) {
        if (message instanceof SystemMessage) {
            if (systemMessage == null) {
                systemMessage = (SystemMessage) message;
            }
        } else {
            inputMessage = message;
        }
    }

    @Override
    public List<ChatMessage> messages() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        List<Message> historyMessages = messageService.messages(gameId);
        for (Message message : historyMessages) {
            if (Message.Source.HOST == message.source()) {
                messages.add(UserMessage.from(message.text()));
            } else {
                PlayerMessage<?> playerMessage = (PlayerMessage<?>) message;
                if (number == playerMessage.getData().getNumber()) {
                    messages.add(AiMessage.from(message.text()));
                } else {
                    // 对于部分消息要进行过滤
                    if ((playerMessage instanceof VoteMessage) || (playerMessage instanceof MissionMessage)) {
                        continue;
                    }
                    messages.add(UserMessage.from(playerMessage.getData().getNumber() + "号玩家：" + message.text()));
                }
            }
        }
        // 输入
        messages.add(inputMessage);
        return messages;
    }

    @Override
    public void clear() {

    }
}
