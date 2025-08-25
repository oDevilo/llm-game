package io.github.devil.llm.avalon.game.message;

/**
 * @author Devil
 */
public abstract class HostMessage extends Message {

    public HostMessage(String gameId) {
        super(gameId);
    }

    public HostMessage(String gameId, Integer round, Integer turn) {
        super(gameId, round, turn);
    }

    @Override
    public Message.Source source() {
        return Source.HOST;
    }
}