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
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Devil
 */
@Setter
@Getter
@Table(name = TableConstants.PLAYER)
@Entity
@DynamicInsert
@DynamicUpdate
public class PlayerEntity extends BaseEntity {

    @EmbeddedId
    private ID id;

    private String role;
    /**
     * 推理
     */
    private String thinking;

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
        /**
         * 号码
         */
        private Integer number;
    }
}
