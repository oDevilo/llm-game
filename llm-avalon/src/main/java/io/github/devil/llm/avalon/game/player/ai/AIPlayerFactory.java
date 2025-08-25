package io.github.devil.llm.avalon.game.player.ai;

import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.dao.repository.AIChatEntityRepository;
import io.github.devil.llm.avalon.dao.repository.PlayerEntityRepository;
import io.github.devil.llm.avalon.game.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Devil
 */
@Component
public class AIPlayerFactory {

    @Resource
    private MessageService messageService;
    @Resource
    private PlayerEntityRepository playerEntityRepository;
    @Resource
    private AIChatEntityRepository aiChatEntityRepository;

    public AIPlayer build(String gameId, int number, PlayerRole role, Map<Integer, PlayerRole> roles) {
        AIPlayer.AIComponent aiComponent = new AIPlayer.AIComponent(messageService, playerEntityRepository, aiChatEntityRepository);
        switch (role) {
            case MERLIN -> {
                return new MerlinAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case PERCIVAL -> {
                return new PercivalAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case Loyal_Minister -> {
                return new LoyalMinisterAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case MORGANA -> {
                return new MorganaAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case ASSASSIN -> {
                return new AssassinAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case MORDRED -> {
                return new MordredAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case OBERON -> {
                return new OberonAIPlayer(gameId, number, role, roles, aiComponent);
            }
            case CLAWS -> {
                return new ClawsAIPlayer(gameId, number, role, roles, aiComponent);
            }
        }
        return null;
    }
}
