package io.github.devil.llm.avalon.dao.entity;

import io.github.devil.llm.avalon.dao.TableConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Devil
 */
@Setter
@Getter
@Table(name = TableConstants.TURN)
@Entity
@DynamicInsert
@DynamicUpdate
public class TurnEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameId;

    private Integer round;

    private Integer turn;

    private Integer captainNumber;

    private String unSpeakers;

    private String team;

    private String voteResult;

    private String missionResult;

    private String state;

    @Override
    public Object getUid() {
        return id;
    }
}
