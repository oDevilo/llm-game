package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.game.store.StepStore;

/**
 * @author Devil
 */
public interface Step {

    StepResult execute();

    StepStore store();

}
