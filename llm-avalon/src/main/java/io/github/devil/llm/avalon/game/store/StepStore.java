package io.github.devil.llm.avalon.game.store;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import io.github.devil.llm.avalon.game.runtime.step.GameEndCheckStep;
import io.github.devil.llm.avalon.game.runtime.step.GameInitStep;
import io.github.devil.llm.avalon.game.runtime.step.KillStep;
import io.github.devil.llm.avalon.game.runtime.step.RoundEndCheckStep;
import io.github.devil.llm.avalon.game.runtime.step.Step;
import io.github.devil.llm.avalon.game.runtime.step.TurnStartStep;
import io.github.devil.llm.avalon.game.runtime.step.TurnVoteStep;
import io.github.devil.llm.avalon.utils.json.JacksonTypeIdResolver;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonTypeIdResolver(JacksonTypeIdResolver.class)
public abstract class StepStore {

    private String type;

    public abstract String getType();

    public abstract Step step();

    public interface Type {
        String GameEndCheckStep = "GameEndCheckStep";
        String GameInitStep = "GameInitStep";
        String KillStep = "KillStep";
        String RoundEndCheckStep = "RoundEndCheckStep";
        String TurnStartStep = "TurnStartStep";
        String TurnVoteStep = "TurnVoteStep";
    }

    public static List<StepStore> store(List<Step> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            return new ArrayList<>();
        }
        return steps.stream().map(Step::store).collect(Collectors.toList());
    }

    public static List<Step> load(List<StepStore> stores) {
        if (CollectionUtils.isEmpty(stores)) {
            return new ArrayList<>();
        }
        return stores.stream().map(StepStore::step).collect(Collectors.toList());
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.GameEndCheckStep)
    public static class GameEndCheckStepStore extends StepStore {

        @Override
        public String getType() {
            return Type.GameEndCheckStep;
        }

        @Override
        public Step step() {
            GameEndCheckStep step = new GameEndCheckStep();
            return step;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.GameInitStep)
    public static class GameInitStepStore extends StepStore {

        @Override
        public String getType() {
            return Type.GameInitStep;
        }

        @Override
        public Step step() {
            GameInitStep step = new GameInitStep();
            return step;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.KillStep)
    public static class KillStepStore extends StepStore {

        @Override
        public String getType() {
            return Type.KillStep;
        }

        @Override
        public Step step() {
            KillStep step = new KillStep();
            return step;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.RoundEndCheckStep)
    public static class RoundEndCheckStepStore extends StepStore {

        @Override
        public String getType() {
            return Type.RoundEndCheckStep;
        }

        @Override
        public Step step() {
            RoundEndCheckStep step = new RoundEndCheckStep();
            return step;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.TurnStartStep)
    public static class TurnStartStepStore extends StepStore {

        @Override
        public String getType() {
            return Type.TurnStartStep;
        }

        @Override
        public Step step() {
            TurnStartStep step = new TurnStartStep();
            return step;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(Type.TurnVoteStep)
    public static class TurnVoteStepStore extends StepStore {

        @Override
        public String getType() {
            return Type.TurnVoteStep;
        }

        @Override
        public Step step() {
            TurnVoteStep step = new TurnVoteStep();
            return step;
        }
    }

}
