package io.github.devil.llm.avalon.game.player;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.message.player.PlayerChatMessage;
import io.github.devil.llm.avalon.game.message.player.VoteMessage;
import io.github.devil.llm.avalon.game.player.assistant.AssistantFactory;
import io.github.devil.llm.avalon.game.service.MessageService;
import io.github.devil.llm.avalon.llm.Assistant;
import io.github.devil.llm.avalon.utils.LLMUtils;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
@Slf4j
public class AIPlayer extends Player {

    private static final String CONFIRM_TEAM_PROMPT_TEMPLATE = """
        请队长进行总结发言，并请确认最终车队人员
        
        # 返回格式
        {
            "content": "发言内容"
            "teamNumbers": [1, 2, 3]
        }
        """;

    private static final String KILL_PROMPT_TEMPLATE = """
        请红方刺客选择刺杀对象。
        
        # 返回格式
        {
            "killNumber": "想要刺杀的玩家号码"
        }
        
        # 返回示例
        {
            "killNumber": 1
        }
        """;

    private static final String SPEAK_PROMPT_TEMPLATE = """
        下面请{{number}}号玩家发言
        
        # 返回格式
        {
            "thinking": "思考过程",
            "speak": "发言内容",
        }
        """;

    private final String VOTE_PROMPT_TEMPLATE = """
        当前队伍由{{numbers}}号玩家组成，请结合你的身份对当前车队进行投票
        
        # 返回格式
        {
            "agree": "是否同意，可以返回`true`或者`false`"
        }
        
        # 返回示例
        {
            "agree": false
        }
        """;

    private final String DRAFT_TEAM_PROMPT_TEMPLATE = """
        你是本回合队长，请拟定队伍，并简单说明原因
        
        # 当前轮次
        {{round}}
        
        # 当前轮次回合
        {{turn}}
        
        # 当前队长
        {{captainNumber}}号玩家
        
        # 本轮所需出任务人数
        {{teamNum}}
        
        # 返回格式
        {
          "content": "拟定的队伍以及原因",
          "speakOrder": "发言顺序，从顺时针`CLOCKWISE`和逆时针`COUNTERCLOCKWISE`中选取一个"
        }
        """;

    private static final String MISSION_PROMPT_TEMPLATE = """
        作为红方成员，你可以根据自己场上的局势来决定让本次任务执行成功或者失败
        
        # 返回格式
        {
            "success": "是否使任务成功，成功返回`true` 希望任务失败返回`false`"
        }
        
        # 返回示例
        {
            "success": false
        }
        """;


    private Assistant assistant;

    public AIPlayer(String gameId, int number, PlayerRole role, MessageService messageService) {
        super(gameId, number, role, messageService);
    }

    @Override
    public void init(List<Player> players) {
        this.assistant = AssistantFactory.build(new AssistantFactory.Request(
            gameId, number, players, messageService, role
        ));
    }

    @Override
    public SpeakOrder draftTeam(int round, int turn, int captainNumber, int teamNum) {
        return execute(() -> {
            String json = assistant.chat(new PromptTemplate(DRAFT_TEAM_PROMPT_TEMPLATE).apply(
                Map.of(
                    "round", round,
                    "turn", turn,
                    "captainNumber", captainNumber,
                    "teamNum", teamNum
                )
            ).text()).content();
            json = LLMUtils.llmStringToJson(json);
            DraftTeamMessage.MessageData messageData = JacksonUtils.toType(json, DraftTeamMessage.MessageData.class);
            addMessage(new DraftTeamMessage(gameId, messageData));
            return SpeakOrder.parse(messageData.getSpeakOrder());
        });
    }

    @Override
    public void speak(int number) {
        execute(() -> {
            String json = assistant.chat(new PromptTemplate(SPEAK_PROMPT_TEMPLATE).apply(
                Map.of(
                    "number", number
                )
            ).text()).content();
            json = LLMUtils.llmStringToJson(json);
            PlayerChatMessage.MessageData messageData = JacksonUtils.toType(json, PlayerChatMessage.MessageData.class);
            addMessage(new PlayerChatMessage(gameId, messageData));
        });
    }

    @Override
    public Set<Integer> confirmTeam() {
        return execute(() -> {
            String json = assistant.chat(new PromptTemplate(CONFIRM_TEAM_PROMPT_TEMPLATE).apply(
                Map.of(
                )
            ).text()).content();
            json = LLMUtils.llmStringToJson(json);
            ConfirmTeamMessage.MessageData messageData = JacksonUtils.toType(json, ConfirmTeamMessage.MessageData.class);
            addMessage(new ConfirmTeamMessage(gameId, messageData));
            return messageData.getTeamNumbers();
        });
    }

    @Override
    public boolean vote(Set<Integer> team) {
        return execute(() -> {
            String json = assistant.chat(new PromptTemplate(VOTE_PROMPT_TEMPLATE).apply(
                Map.of(
                    "numbers", team.stream().map(String::valueOf).collect(Collectors.joining("、"))
                )
            ).text()).content();
            json = LLMUtils.llmStringToJson(json);
            VoteMessage.MessageData messageData = JacksonUtils.toType(json, VoteMessage.MessageData.class);
            addMessage(new VoteMessage(gameId, messageData));
            return messageData.isAgree();
        });
    }

    @Override
    public boolean mission() {
        return execute(() -> {
            MissionMessage.MessageData messageData;
            if (CampType.BLUE == role.camp) {
                messageData = new MissionMessage.MessageData();
                messageData.setSuccess(true);
            } else {
                String json = assistant.chat(new PromptTemplate(MISSION_PROMPT_TEMPLATE).apply(
                    Map.of()
                ).text()).content();
                json = LLMUtils.llmStringToJson(json);
                messageData = JacksonUtils.toType(json, MissionMessage.MessageData.class);
            }
            addMessage(new MissionMessage(gameId, messageData));
            return messageData.isSuccess();
        });
    }

    @Override
    public int kill() {
        return execute(() -> {
            String json = assistant.chat(new PromptTemplate(KILL_PROMPT_TEMPLATE).apply(
                Map.of()
            ).text()).content();
            json = LLMUtils.llmStringToJson(json);
            KillResultMessage.MessageData messageData = JacksonUtils.toType(json, KillResultMessage.MessageData.class);
            addMessage(new KillResultMessage(gameId, messageData));
            return messageData.getKillNumber();
        });
    }

    private <T> T execute(Callable<T> task) {
        try {
            Thread.sleep(500); // 防止回答过快
        } catch (InterruptedException e) {
        }
        try {
            return task.call();
        } catch (Exception e) {
            log.error("execute fail", e);
            throw new RuntimeException(e);
        }
    }

    private void execute(Runnable runnable) {
        try {
            Thread.sleep(500); // 防止回答过快
        } catch (InterruptedException e) {
        }
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("execute fail", e);
            throw new RuntimeException(e);
        }
    }

}
