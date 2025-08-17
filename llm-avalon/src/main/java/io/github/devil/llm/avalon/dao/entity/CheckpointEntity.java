package io.github.devil.llm.avalon.dao.entity;

import io.github.devil.llm.avalon.dao.TableConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Devil
 */
@Setter
@Getter
@Table(name = TableConstants.CHECKPOINT)
@Entity
@DynamicInsert
@DynamicUpdate
public class CheckpointEntity extends BaseEntity {

    @Id
    private String checkpointId;

    private String threadId;

    private String state;

    private String nodeId;

    private String nextNodeId;

    @Override
    public Object getUid() {
        return checkpointId;
    }
}
