package io.github.devil.llm.avalon.game.player.ai;

import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.game.service.MessageService;

import java.util.Map;

/**
 * @author Devil
 */
public class AIPlayerFactory {

    public static AIPlayer build(String gameId, int number, PlayerRole role, MessageService messageService, Map<Integer, PlayerRole> roles) {
        switch (role) {
            case MERLIN -> {
                return new MerlinAIPlayer(gameId, number, role, messageService, roles);
            }
            case PERCIVAL -> {
                return new PercivalAIPlayer(gameId, number, role, messageService, roles);
            }
            case Loyal_Minister -> {
                return new LoyalMinisterAIPlayer(gameId, number, role, messageService, roles);
            }
            case MORGANA -> {
                return new MorganaAIPlayer(gameId, number, role, messageService, roles);
            }
            case ASSASSIN -> {
                return new AssassinAIPlayer(gameId, number, role, messageService, roles);
            }
            case MORDRED -> {
                return new MordredAIPlayer(gameId, number, role, messageService, roles);
            }
            case OBERON -> {
                return new OberonAIPlayer(gameId, number, role, messageService, roles);
            }
            case CLAWS -> {
                return new ClawsAIPlayer(gameId, number, role, messageService, roles);
            }
        }
        return null;
    }
}
