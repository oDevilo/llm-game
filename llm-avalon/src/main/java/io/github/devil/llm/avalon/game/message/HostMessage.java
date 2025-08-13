package io.github.devil.llm.avalon.game.message;

/**
 * @author Devil
 */
public abstract class HostMessage implements Message {

    public abstract String prompt();

    @Override
    public Message.Source source() {
        return Source.HOST;
    }
}