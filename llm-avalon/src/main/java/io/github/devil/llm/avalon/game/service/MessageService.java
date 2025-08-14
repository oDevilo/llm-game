package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.dao.entity.MessageEntity;
import io.github.devil.llm.avalon.dao.repository.MessageEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
import io.github.devil.llm.avalon.game.message.HostMessage;
import io.github.devil.llm.avalon.game.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Devil
 */
@Service
public class MessageService {

    @Resource
    private MessageEntityRepository messageEntityRepository;

    public void add(Message message) {
        messageEntityRepository.saveAndFlush(Converter.toEntity(message));
    }

    public List<Message> messages(String gameId) {
        List<MessageEntity> entities = messageEntityRepository.findByGameId(gameId);
        return Converter.toMessages(entities);
    }

    public HostMessage lastHostMessage(String gameId) {
        List<MessageEntity> entities = messageEntityRepository.findByGameId(gameId);
        MessageEntity hostEntity = null;
        for (int i = entities.size() - 1; i >= 0; i--) {
            MessageEntity entity = entities.get(i);
            if (!Message.Source.HOST.name().equalsIgnoreCase(entity.getSource())) {
                continue;
            }
            hostEntity = entity;
            break;
        }
        return (HostMessage) Converter.toMessage(hostEntity);
    }

}
