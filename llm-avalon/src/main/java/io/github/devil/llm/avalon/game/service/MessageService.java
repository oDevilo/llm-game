package io.github.devil.llm.avalon.game.service;

import io.github.devil.llm.avalon.dao.entity.MessageEntity;
import io.github.devil.llm.avalon.dao.repository.MessageEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
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

    public List<Message> messages(String gameId, Integer round, Integer turn) {
        List<MessageEntity> entities = messageEntityRepository.findByGameIdAndRoundAndTurn(gameId, round, turn);
        return Converter.toMessages(entities);
    }

}
