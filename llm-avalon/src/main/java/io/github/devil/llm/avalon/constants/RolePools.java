package io.github.devil.llm.avalon.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devil
 */
public class RolePools {

    public static final List<PlayerRole> FIVE_ROLE_POOL = new ArrayList<>();
    public static final List<PlayerRole> SIX_ROLE_POOL = new ArrayList<>();
    public static final List<PlayerRole> SEVEN_ROLE_POOL = new ArrayList<>();
    public static final List<PlayerRole> EIGHT_ROLE_POOL = new ArrayList<>();
    public static final List<PlayerRole> NIGHT_ROLE_POOL = new ArrayList<>();
    public static final List<PlayerRole> TEN_ROLE_POOL = new ArrayList<>();

    static {
        FIVE_ROLE_POOL.add(PlayerRole.MERLIN);
        FIVE_ROLE_POOL.add(PlayerRole.PERCIVAL);
        FIVE_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        FIVE_ROLE_POOL.add(PlayerRole.MORGANA);
        FIVE_ROLE_POOL.add(PlayerRole.ASSASSIN);

        SIX_ROLE_POOL.add(PlayerRole.MERLIN);
        SIX_ROLE_POOL.add(PlayerRole.PERCIVAL);
        SIX_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        SIX_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        SIX_ROLE_POOL.add(PlayerRole.MORGANA);
        SIX_ROLE_POOL.add(PlayerRole.ASSASSIN);

        SEVEN_ROLE_POOL.add(PlayerRole.MERLIN);
        SEVEN_ROLE_POOL.add(PlayerRole.PERCIVAL);
        SEVEN_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        SEVEN_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        SEVEN_ROLE_POOL.add(PlayerRole.MORGANA);
        SEVEN_ROLE_POOL.add(PlayerRole.ASSASSIN);
        SEVEN_ROLE_POOL.add(PlayerRole.OBERON);

        EIGHT_ROLE_POOL.add(PlayerRole.MERLIN);
        EIGHT_ROLE_POOL.add(PlayerRole.PERCIVAL);
        EIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        EIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        EIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        EIGHT_ROLE_POOL.add(PlayerRole.MORGANA);
        EIGHT_ROLE_POOL.add(PlayerRole.ASSASSIN);
        EIGHT_ROLE_POOL.add(PlayerRole.CLAWS);

        NIGHT_ROLE_POOL.add(PlayerRole.MERLIN);
        NIGHT_ROLE_POOL.add(PlayerRole.PERCIVAL);
        NIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        NIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        NIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        NIGHT_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        NIGHT_ROLE_POOL.add(PlayerRole.MORGANA);
        NIGHT_ROLE_POOL.add(PlayerRole.ASSASSIN);
        NIGHT_ROLE_POOL.add(PlayerRole.MORDRED);

        TEN_ROLE_POOL.add(PlayerRole.MERLIN);
        TEN_ROLE_POOL.add(PlayerRole.PERCIVAL);
        TEN_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        TEN_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        TEN_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        TEN_ROLE_POOL.add(PlayerRole.Loyal_Minister);
        TEN_ROLE_POOL.add(PlayerRole.MORGANA);
        TEN_ROLE_POOL.add(PlayerRole.ASSASSIN);
        TEN_ROLE_POOL.add(PlayerRole.MORDRED);
        TEN_ROLE_POOL.add(PlayerRole.OBERON);
    }

    public static List<PlayerRole> roles(int playerNumber) {
        return switch (playerNumber) {
            case 5 -> RolePools.FIVE_ROLE_POOL;
            case 6 -> RolePools.SIX_ROLE_POOL;
            case 7 -> RolePools.SEVEN_ROLE_POOL;
            case 8 -> RolePools.EIGHT_ROLE_POOL;
            case 9 -> RolePools.NIGHT_ROLE_POOL;
            case 10 -> RolePools.TEN_ROLE_POOL;
            default -> RolePools.FIVE_ROLE_POOL;
        };
    }
}
