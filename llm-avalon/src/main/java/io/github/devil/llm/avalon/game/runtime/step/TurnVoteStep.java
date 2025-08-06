package io.github.devil.llm.avalon.game.runtime.step;

import io.github.devil.llm.avalon.game.Engine;
import io.github.devil.llm.avalon.game.runtime.Game;
import io.github.devil.llm.avalon.game.runtime.MessageHistory;
import io.github.devil.llm.avalon.game.runtime.Player;
import io.github.devil.llm.avalon.game.runtime.message.host.AskCaptainSummaryMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.AskVoteMessage;
import io.github.devil.llm.avalon.game.runtime.message.host.TurnEndMessage;
import io.github.devil.llm.avalon.game.store.StepStore;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class TurnVoteStep implements Step {

    /**
     * 代码先写这里后面再拆分
     */
    @Override
    public StepResult execute() {
        Game game = Engine.game();
        Game.Round round = Engine.round();
        Game.Round.Turn turn = Engine.turn();
        MessageHistory messageHistory = game.getMessageHistory();
        // 队长总结发言确认队伍
        messageHistory.add(new AskCaptainSummaryMessage());
        Player captain = turn.captain(game.getPlayers());
        Set<Integer> team = captain.team();
        // 车队投票
        vote(team);
        // 结果判定
        int missionFailNum = (int) turn.getMissionResult().values().stream().filter(r -> !r).count();
        if (missionStart()) {
            // 出任务的人对任务进行投票
            mission(team);
            if (missionFailNum > 0) {
                turn.setResult(Game.Round.Turn.Result.MISSION_FAIL);
            } else {
                turn.setResult(Game.Round.Turn.Result.MISSION_COMPLETE);
            }
        } else {
            // 流局
            turn.setResult(Game.Round.Turn.Result.DRAWN);
        }
        // 当前回合结束
        messageHistory.add(new TurnEndMessage(
            round.getRound(), turn.getTurn(), turn.getCaptainNumber(), round.getTeamNum(),
            turn.getResult(), missionFailNum
        ));
        round.getHistoryTurns().add(turn);
        return new Result();
    }

    @Override
    public StepStore store() {
        StepStore.TurnVoteStepStore store = new StepStore.TurnVoteStepStore();
        return store;
    }

    public static class Result implements StepResult {

        @Override
        public Step next() {
            return new RoundEndCheckStep();
        }
    }

    public void vote(Set<Integer> team) {
        Game game = Engine.game();
        Game.Round.Turn turn = Engine.turn();
        MessageHistory messageHistory = game.getMessageHistory();
        List<Player> players = game.getPlayers();
        Map<Integer, Boolean> voteResult = turn.getVoteResult();
        messageHistory.add(new AskVoteMessage(team));
        for (Player player : players) {
            boolean vote = player.vote();
            voteResult.put(player.getNumber(), vote);
        }
    }

    public boolean missionStart() {
        Game game = Engine.game();
        Game.Round.Turn turn = Engine.turn();
        Map<Integer, Boolean> voteResult = turn.getVoteResult();
        List<Player> players = game.getPlayers();
        long success = voteResult.values().stream().filter(v -> v).count();
        return success > players.size() / 2;
    }

    public void mission(Set<Integer> team) {
        Game game = Engine.game();
        Game.Round.Turn turn = Engine.turn();
        List<Player> players = game.getPlayers();
        Map<Integer, Boolean> missionResult = turn.getMissionResult();

        Set<Player> teamPlayers = players.stream()
            .filter(p -> team.contains(p.getNumber()))
            .collect(Collectors.toSet());
        for (Player teamPlayer : teamPlayers) {
            boolean mission = teamPlayer.mission();
            missionResult.put(teamPlayer.getNumber(), mission);
        }
    }
}
