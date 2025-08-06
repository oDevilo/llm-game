package io.github.devil.llm.avalon.constants;

import io.github.devil.llm.avalon.game.runtime.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 */
public class RolePools {

    public static final List<Player.Role> FIVE_ROLE_POOL = new ArrayList<>();
    public static final List<Player.Role> SIX_ROLE_POOL = new ArrayList<>();
    public static final List<Player.Role> SEVEN_ROLE_POOL = new ArrayList<>();
    public static final List<Player.Role> EIGHT_ROLE_POOL = new ArrayList<>();
    public static final List<Player.Role> NIGHT_ROLE_POOL = new ArrayList<>();
    public static final List<Player.Role> TEN_ROLE_POOL = new ArrayList<>();

    static {
        FIVE_ROLE_POOL.add(Player.Role.MERLIN);
        FIVE_ROLE_POOL.add(Player.Role.PERCIVAL);
        FIVE_ROLE_POOL.add(Player.Role.Loyal_Minister);
        FIVE_ROLE_POOL.add(Player.Role.MORGANA);
        FIVE_ROLE_POOL.add(Player.Role.ASSASSIN);

        SIX_ROLE_POOL.add(Player.Role.MERLIN);
        SIX_ROLE_POOL.add(Player.Role.PERCIVAL);
        SIX_ROLE_POOL.add(Player.Role.Loyal_Minister);
        SIX_ROLE_POOL.add(Player.Role.Loyal_Minister);
        SIX_ROLE_POOL.add(Player.Role.MORGANA);
        SIX_ROLE_POOL.add(Player.Role.ASSASSIN);

        SEVEN_ROLE_POOL.add(Player.Role.MERLIN);
        SEVEN_ROLE_POOL.add(Player.Role.PERCIVAL);
        SEVEN_ROLE_POOL.add(Player.Role.Loyal_Minister);
        SEVEN_ROLE_POOL.add(Player.Role.Loyal_Minister);
        SEVEN_ROLE_POOL.add(Player.Role.MORGANA);
        SEVEN_ROLE_POOL.add(Player.Role.ASSASSIN);
        SEVEN_ROLE_POOL.add(Player.Role.OBERON);

        EIGHT_ROLE_POOL.add(Player.Role.MERLIN);
        EIGHT_ROLE_POOL.add(Player.Role.PERCIVAL);
        EIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        EIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        EIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        EIGHT_ROLE_POOL.add(Player.Role.MORGANA);
        EIGHT_ROLE_POOL.add(Player.Role.ASSASSIN);
        EIGHT_ROLE_POOL.add(Player.Role.CLAWS);

        NIGHT_ROLE_POOL.add(Player.Role.MERLIN);
        NIGHT_ROLE_POOL.add(Player.Role.PERCIVAL);
        NIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        NIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        NIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        NIGHT_ROLE_POOL.add(Player.Role.Loyal_Minister);
        NIGHT_ROLE_POOL.add(Player.Role.MORGANA);
        NIGHT_ROLE_POOL.add(Player.Role.ASSASSIN);
        NIGHT_ROLE_POOL.add(Player.Role.MORDRED);

        TEN_ROLE_POOL.add(Player.Role.MERLIN);
        TEN_ROLE_POOL.add(Player.Role.PERCIVAL);
        TEN_ROLE_POOL.add(Player.Role.Loyal_Minister);
        TEN_ROLE_POOL.add(Player.Role.Loyal_Minister);
        TEN_ROLE_POOL.add(Player.Role.Loyal_Minister);
        TEN_ROLE_POOL.add(Player.Role.Loyal_Minister);
        TEN_ROLE_POOL.add(Player.Role.MORGANA);
        TEN_ROLE_POOL.add(Player.Role.ASSASSIN);
        TEN_ROLE_POOL.add(Player.Role.MORDRED);
        TEN_ROLE_POOL.add(Player.Role.OBERON);
    }
}
