package io.github.devil.llm.avalon.dao.entity;

import io.github.devil.llm.avalon.dao.TableConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Devil
 */
@Setter
@Getter
@Table(name = TableConstants.GAME)
@Entity
@DynamicInsert
@DynamicUpdate
public class GameEntity extends BaseEntity {

    private Integer playerNumber;

    private String playerRoles;

    private String state;

}
