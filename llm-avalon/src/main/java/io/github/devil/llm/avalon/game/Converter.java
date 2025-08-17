package io.github.devil.llm.avalon.game;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.dao.entity.CheckpointEntity;
import io.github.devil.llm.avalon.dao.entity.GameEntity;
import io.github.devil.llm.avalon.dao.entity.MessageEntity;
import io.github.devil.llm.avalon.dao.entity.RoundEntity;
import io.github.devil.llm.avalon.dao.entity.TurnEntity;
import io.github.devil.llm.avalon.game.message.Message;
import io.github.devil.llm.avalon.game.message.host.AskCaptainSummaryMessage;
import io.github.devil.llm.avalon.game.message.host.AskKillMessage;
import io.github.devil.llm.avalon.game.message.host.AskSpeakMessage;
import io.github.devil.llm.avalon.game.message.host.AskVoteMessage;
import io.github.devil.llm.avalon.game.message.host.BeforeKillMessage;
import io.github.devil.llm.avalon.game.message.host.StartTurnMessage;
import io.github.devil.llm.avalon.game.message.host.TurnEndMessage;
import io.github.devil.llm.avalon.game.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.message.player.PlayerChatMessage;
import io.github.devil.llm.avalon.game.message.player.VoteMessage;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.bsc.langgraph4j.checkpoint.Checkpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class Converter {

    // game
    public static GameEntity toEntity(GameState.Game game) {
        if (game == null) {
            return null;
        }
        GameEntity gameEntity = new GameEntity();
        gameEntity.setGameId(game.getId());
        gameEntity.setPlayerNumber(game.getPlayerNumber());
        gameEntity.setPlayerRoles(JacksonUtils.toJSONString(game.getPlayerRoles()));
        gameEntity.setCaptainOrder(JacksonUtils.toJSONString(game.getCaptainOrder()));
        gameEntity.setState(game.getState().getState());
        gameEntity.setMissionCamp(game.getMissionCamp().name());
        return gameEntity;
    }

    public static GameState.Game toGame(GameEntity entity) {
        if (entity == null) {
            return null;
        }
        GameState.Game game = new GameState.Game();
        game.setId(entity.getGameId());
        game.setPlayerNumber(entity.getPlayerNumber());
        game.setCaptainOrder(JacksonUtils.toType(entity.getCaptainOrder(), new TypeReference<>() {
        }));
        game.setPlayerRoles(JacksonUtils.toType(entity.getPlayerRoles(), new TypeReference<>() {
        }));
        game.setState(GameState.State.parse(entity.getState()));
        game.setMissionCamp(CampType.parse(entity.getMissionCamp()));
        return game;
    }

    // round
    public static List<RoundState.Round> toRounds(GameState.Game game, List<RoundEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(e -> toRound(game, e)).collect(Collectors.toList());
    }

    public static RoundState.Round toRound(GameState.Game game, RoundEntity entity) {
        if (entity == null) {
            return null;
        }
        RoundState.Round round = new RoundState.Round();
        round.setId(entity.getId());
        round.setGameId(entity.getGameId());
        round.setRound(entity.getRound());
        round.setTeamNum(RoundState.Round.teamNum(game.getPlayerNumber(), entity.getRound()));
        round.setPlayerNumber(game.getPlayerNumber());
        round.setCaptainOrder(game.getCaptainOrder());
        round.setState(RoundState.State.parse(entity.getState()));
        return round;
    }

    public static RoundEntity toEntity(RoundState.Round round) {
        if (round == null) {
            return null;
        }
        RoundEntity roundEntity = new RoundEntity();
        roundEntity.setId(round.getId());
        roundEntity.setGameId(round.getGameId());
        roundEntity.setRound(round.getRound());
        roundEntity.setState(round.getState().getState());
        return roundEntity;
    }

    // turn
    public static TurnState.Turn toTurn(RoundState.Round round, TurnEntity entity) {
        if (entity == null) {
            return null;
        }
        TurnState.Turn turn = new TurnState.Turn();
        turn.setId(entity.getId());
        turn.setGameId(entity.getGameId());
        turn.setRound(entity.getRound());
        turn.setTurn(entity.getTurn());
        turn.setCaptainNumber(entity.getCaptainNumber());
        turn.setTeamNumber(round.getTeamNum());
        turn.setUnSpeakers(JacksonUtils.toType(entity.getUnSpeakers(), new TypeReference<>() {
        }));
        turn.setTeam(JacksonUtils.toType(entity.getTeam(), new TypeReference<>() {
        }));
        turn.setVoteResult(JacksonUtils.toType(entity.getVoteResult(), new TypeReference<>() {
        }));
        turn.setMissionResult(JacksonUtils.toType(entity.getMissionResult(), new TypeReference<>() {
        }));
        turn.setState(TurnState.State.parse(entity.getState()));
        return turn;
    }

    public static TurnEntity toEntity(TurnState.Turn turn) {
        if (turn == null) {
            return null;
        }
        TurnEntity turnEntity = new TurnEntity();
        turnEntity.setId(turn.getId());
        turnEntity.setGameId(turn.getGameId());
        turnEntity.setRound(turn.getRound());
        turnEntity.setTurn(turn.getTurn());
        turnEntity.setCaptainNumber(turn.getCaptainNumber());
        turnEntity.setUnSpeakers(JacksonUtils.toJSONString(turn.getUnSpeakers()));
        turnEntity.setTeam(JacksonUtils.toJSONString(turn.getTeam()));
        turnEntity.setVoteResult(JacksonUtils.toJSONString(turn.getVoteResult()));
        turnEntity.setMissionResult(JacksonUtils.toJSONString(turn.getMissionResult()));
        turnEntity.setState(turn.getState().getState());
        return turnEntity;
    }

    // message
    public static MessageEntity toEntity(Message message) {
        if (message == null) {
            return null;
        }
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setGameId(message.getGameId());
        messageEntity.setSource(message.source().name());
        messageEntity.setType(message.type());
        messageEntity.setData(message.data() == null ? "" : JacksonUtils.toJSONString(message.data()));
        return messageEntity;
    }

    public static List<Message> toMessages(List<MessageEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(Converter::toMessage).collect(Collectors.toList());
    }

    public static Message toMessage(MessageEntity entity) {
        if (entity == null) {
            return null;
        }
        String type = entity.getType();
        String gameId = entity.getGameId();
        String data = entity.getData();
        switch (type) {
            case Message.Type.AskCaptainSummaryMessage -> {
                return new AskCaptainSummaryMessage(gameId);
            }
            case Message.Type.AskKillMessage -> {
                return new AskKillMessage(gameId);
            }
            case Message.Type.AskSpeakMessage -> {
                return new AskSpeakMessage(gameId, JacksonUtils.toType(data, AskSpeakMessage.MessageData.class));
            }
            case Message.Type.AskVoteMessage -> {
                return new AskVoteMessage(gameId, JacksonUtils.toType(data, AskVoteMessage.MessageData.class));
            }
            case Message.Type.BeforeKillMessage -> {
                return new BeforeKillMessage(gameId);
            }
            case Message.Type.StartTurnMessage -> {
                return new StartTurnMessage(gameId, JacksonUtils.toType(data, StartTurnMessage.MessageData.class));
            }
            case Message.Type.TurnEndMessage -> {
                return new TurnEndMessage(gameId, JacksonUtils.toType(data, TurnEndMessage.MessageData.class));
            }

            case Message.Type.ConfirmTeamMessage -> {
                return new ConfirmTeamMessage(gameId, JacksonUtils.toType(data, ConfirmTeamMessage.MessageData.class));
            }
            case Message.Type.DraftTeamMessage -> {
                return new DraftTeamMessage(gameId, JacksonUtils.toType(data, DraftTeamMessage.MessageData.class));
            }
            case Message.Type.KillResultMessage -> {
                return new KillResultMessage(gameId, JacksonUtils.toType(data, KillResultMessage.MessageData.class));
            }
            case Message.Type.MissionMessage -> {
                return new MissionMessage(gameId, JacksonUtils.toType(data, MissionMessage.MessageData.class));
            }
            case Message.Type.PlayerChatMessage -> {
                return new PlayerChatMessage(gameId, JacksonUtils.toType(data, PlayerChatMessage.MessageData.class));
            }
            case Message.Type.VoteMessage -> {
                return new VoteMessage(gameId, JacksonUtils.toType(data, VoteMessage.MessageData.class));
            }
            default -> {
                return null;
            }
        }
    }

    // checkpoint
    public static CheckpointEntity toEntity(String threadId, Checkpoint checkpoint) {
        if (checkpoint == null) {
            return null;
        }
        CheckpointEntity checkpointEntity = new CheckpointEntity();
        checkpointEntity.setCheckpointId(checkpoint.getId());
        checkpointEntity.setThreadId(threadId);
        checkpointEntity.setState(JacksonUtils.toJSONString(checkpoint.getState()));
        checkpointEntity.setNodeId(checkpoint.getNodeId());
        checkpointEntity.setNextNodeId(checkpoint.getNextNodeId());
        return checkpointEntity;
    }

    public static Checkpoint toCheckpoint(CheckpointEntity entity) {
        if (entity == null) {
            return null;
        }
        return Checkpoint.builder()
            .id(entity.getCheckpointId())
            .state(JacksonUtils.toType(entity.getState(), new TypeReference<Map<String, Object>>() {
            }))
            .nodeId(entity.getNodeId())
            .nextNodeId(entity.getNextNodeId())
            .build();

    }

    public static List<Checkpoint> toCheckpoints(List<CheckpointEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(Converter::toCheckpoint).collect(Collectors.toList());
    }
}
