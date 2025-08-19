package io.github.devil.llm.avalon.game.player;

import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.message.PlayerMessage;
import io.github.devil.llm.avalon.game.service.MessageService;
import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * @author Devil
 */
@Getter
public abstract class Player {
    protected String gameId;
    /**
     * 号码牌
     */
    protected int number;
    /**
     * 扮演角色
     */
    protected PlayerRole role;

    protected MessageService messageService;

    public Player(String gameId, int number, PlayerRole role, MessageService messageService) {
        this.gameId = gameId;
        this.number = number;
        this.role = role;
        this.messageService = messageService;
    }

    public abstract void init(List<Player> players);

    /**
     * 拟定队伍
     * @return 发言顺序 true：顺时针 false：逆时针
     */
    public abstract SpeakOrder draftTeam(int round, int turn, int captainNumber, int teamNum);

    /**
     * 发言
     */
    public abstract void speak(int number);

    /**
     * 选出的进行任务的人员号码
     */
    public abstract Set<Integer> confirmTeam();

    /**
     * 投票出任务
     */
    public abstract boolean vote(Set<Integer> team);

    /**
     * 任务成功失败
     */
    public abstract boolean mission();

    /**
     * 刺杀
     */
    public abstract int kill();

    protected void addMessage(PlayerMessage message) {
        message.getData().setNumber(number);
        messageService.add(message);
    }
}
