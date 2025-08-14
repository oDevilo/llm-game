package io.github.devil.llm.avalon.game;

import io.github.devil.llm.avalon.dao.entity.MessageEntity;
import io.github.devil.llm.avalon.dao.entity.RoundEntity;
import io.github.devil.llm.avalon.dao.entity.TurnEntity;
import io.github.devil.llm.avalon.game.message.Message;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * todo
 * @author Devil
 */
public class Converter {

    // round
    public static List<RoundState.Round> toRounds(List<RoundEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(Converter::toRound).collect(Collectors.toList());
    }

    public static RoundState.Round toRound(RoundEntity entity) {
        if (entity == null) {
            return null;
        }
        return null;
    }

    // turn
    public static TurnState.Turn toTurn(TurnEntity entity) {
        if (entity == null) {
            return null;
        }
        return null;
    }

    // message
    public static MessageEntity toEntity(Message message) {
        return null;
    }

    public static List<Message> toMessages(List<MessageEntity> entities) {
        return null;
    }

    public static Message toMessage(MessageEntity entity) {
        if (entity == null) {
            return null;
        }
        // todo 得转成对应类型
        return null;
    }
}
