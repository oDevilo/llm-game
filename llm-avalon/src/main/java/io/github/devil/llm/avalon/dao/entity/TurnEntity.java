package io.github.devil.llm.avalon.dao.entity;

import io.github.devil.llm.avalon.dao.TableConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

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

    @EmbeddedId
    private ID id;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class ID implements Serializable {

        private String gameId;

        private Integer round;

        private Integer turn;
    }
}
