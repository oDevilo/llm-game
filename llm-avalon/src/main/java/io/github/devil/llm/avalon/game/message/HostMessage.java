package io.github.devil.llm.avalon.game.message;

/**
 * @author Devil
 */
public abstract class HostMessage extends Message {

    public HostMessage(String gameId) {
        super(gameId);
    }

    public abstract String prompt();

    @Override
    public Message.Source source() {
        return Source.HOST;
    }
}