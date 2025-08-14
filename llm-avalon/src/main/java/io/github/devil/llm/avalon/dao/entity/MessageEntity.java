package io.github.devil.llm.avalon.dao.entity;

import io.github.devil.llm.avalon.dao.TableConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

import static io.github.devil.llm.avalon.game.message.Message.Source;

/**
 * @author Devil
 */
@Setter
@Getter
@Table(name = TableConstants.MESSAGE)
@Entity
@DynamicInsert
@DynamicUpdate
public class MessageEntity extends BaseEntity {

    private String gameId;
    /**
     * @see Source
     */
    private String source;

    private String data;

}
