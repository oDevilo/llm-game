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
@Table(name = TableConstants.ROUND)
@Entity
@DynamicInsert
@DynamicUpdate
public class RoundEntity extends BaseEntity {

    private String gameId;

    private Integer round;

    private String result;

}
