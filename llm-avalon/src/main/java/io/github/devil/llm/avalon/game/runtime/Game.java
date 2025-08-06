package io.github.devil.llm.avalon.game.runtime;

import io.github.devil.llm.avalon.constants.CampType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一局游戏的运行数据
 *
 * @author Devil
 */
@Data
public class Game {

    /**
     * 本局ID
     */
    private final String id;

    /**
     * 玩家数
     */
    private final int playerNumber;
    /**
     * 分配的玩家
     */
    private final List<Player> players;
    /**
     * 队长顺序 存的是用户号码
     */
    private final List<Integer> captainOrder;
    /**
     * 如果 0 就选 captainOrder.get(0) 为队长
     */
    private int captainOrderPos;
    /**
     * 历史回合的记录
     */
    private final List<Round> historyRounds;
    /**
     * 历史消息
     */
    private final MessageHistory messageHistory;
    /**
     * 哪边阵营先完成3次任务
     */
    private CampType missionCamp;
    /**
     * 哪边阵营最终胜利
     */
    private CampType winCamp;

    public Game(String id, int playerNumber, MessageHistory messageHistory,
                List<Player> players, List<Round> historyRounds, List<Integer> captainOrder, int captainOrderPos) {
        this.id = id;
        this.playerNumber = playerNumber;
        this.messageHistory = messageHistory;
        this.historyRounds = historyRounds;
        this.players = players;
        this.captainOrder = captainOrder;
        this.captainOrderPos = captainOrderPos;
    }


    /**
     * 第几轮，最多5轮
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Round {
        /**
         * 第几轮
         */
        private int round;

        /**
         * 出任务的人数
         */
        private int teamNum;
        /**
         * 历史回合的记录
         */
        private List<Turn> historyTurns;
        /**
         * 本轮结果
         */
        private Result result;

        public enum Result {
            NOT_END,
            DRAWN_OVER, // 流局超过5次
            MISSION_COMPLETE, // 任务成功
            MISSION_FAIL, // 任务失败
            ;
        }

        /**
         * 回合
         * 一轮可能会因为流局导致有多个回合
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Turn {

            /**
             * 第几回合
             */
            private int turn;
            /**
             * 队长
             */
            private int captainNumber;
            /**
             * 投票结果
             */
            private Map<Integer, Boolean> voteResult;
            /**
             * 任务结果
             */
            private Map<Integer, Boolean> missionResult;
            /**
             * 本回合结果
             */
            private Result result;

            public Player captain(List<Player> players) {
                return players.stream().filter(p -> p.getNumber() == captainNumber).findFirst().get();
            }

            public enum Result {
                NOT_END,
                DRAWN, // 流局
                MISSION_COMPLETE, // 任务成功
                MISSION_FAIL, // 任务失败
                ;
            }

        }

    }

}
