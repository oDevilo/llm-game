package io.github.devil.llm.avalon.game.runtime.message.host;

import io.github.devil.llm.avalon.game.runtime.message.Message;

/**
 * @author Devil
 */
public abstract class HostMessage implements Message {

    public abstract String prompt();

    @Override
    public Source source() {
        return Source.HOST;
    }
}
