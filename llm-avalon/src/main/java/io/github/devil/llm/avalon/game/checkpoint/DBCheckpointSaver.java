package io.github.devil.llm.avalon.game.checkpoint;

import io.github.devil.llm.avalon.dao.entity.CheckpointEntity;
import io.github.devil.llm.avalon.dao.repository.CheckpointEntityRepository;
import io.github.devil.llm.avalon.game.Converter;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.checkpoint.Checkpoint;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Devil
 */
@Component
public class DBCheckpointSaver extends MemorySaver {

    @Resource
    private CheckpointEntityRepository checkpointEntityRepository;

    @Override
    protected LinkedList<Checkpoint> loadedCheckpoints(RunnableConfig config, LinkedList<Checkpoint> checkpoints) throws Exception {
        List<CheckpointEntity> entities = checkpointEntityRepository.findByThreadId(config.threadId().get());
        return new LinkedList<>(Converter.toCheckpoints(entities));
    }

    @Override
    protected void insertedCheckpoint(RunnableConfig config, LinkedList<Checkpoint> checkpoints, Checkpoint checkpoint) throws Exception {
        saveCheckpoint(config.threadId().get(), checkpoint);
    }

    @Override
    protected void updatedCheckpoint(RunnableConfig config, LinkedList<Checkpoint> checkpoints, Checkpoint checkpoint) throws Exception {
        saveCheckpoint(config.threadId().get(), checkpoint);
    }

    private void saveCheckpoint(String threadId, Checkpoint checkpoint) {
        CheckpointEntity entity = Converter.toEntity(threadId, checkpoint);
        checkpointEntityRepository.saveAndFlush(entity);
    }

//    @Override
//    protected void releasedCheckpoints(RunnableConfig config, LinkedList<Checkpoint> checkpoints, Tag releaseTag) throws Exception {
//    }

}
