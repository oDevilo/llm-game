package io.github.devil.llm.avalon.game.player.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.dao.entity.AIChatEntity;
import io.github.devil.llm.avalon.dao.entity.PlayerEntity;
import io.github.devil.llm.avalon.dao.repository.AIChatEntityRepository;
import io.github.devil.llm.avalon.dao.repository.PlayerEntityRepository;
import io.github.devil.llm.avalon.game.message.Message;
import io.github.devil.llm.avalon.game.message.PlayerMessage;
import io.github.devil.llm.avalon.game.message.player.ConfirmTeamMessage;
import io.github.devil.llm.avalon.game.message.player.DraftTeamMessage;
import io.github.devil.llm.avalon.game.message.player.KillResultMessage;
import io.github.devil.llm.avalon.game.message.player.KillSpeakMessage;
import io.github.devil.llm.avalon.game.message.player.MissionMessage;
import io.github.devil.llm.avalon.game.message.player.SpeakMessage;
import io.github.devil.llm.avalon.game.message.player.VoteMessage;
import io.github.devil.llm.avalon.game.player.Player;
import io.github.devil.llm.avalon.game.service.MessageService;
import io.github.devil.llm.avalon.llm.Common;
import io.github.devil.llm.avalon.utils.LLMUtils;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
@Slf4j
public abstract class AIPlayer extends Player {

    private final SystemMessage systemMessage;

    private final ChatModel chatModel;

    private final AIComponent aiComponent;

    public AIPlayer(String gameId, int number, PlayerRole role, Map<Integer, PlayerRole> roles, AIComponent aiComponent) {
        super(gameId, number, role, aiComponent.messageService);
        this.chatModel = Common.chatModel();
        this.systemMessage = systemMessage(gameId, number, roles);
        this.aiComponent = aiComponent;
    }

    @Override
    public SpeakOrder draftTeam(int round, int turn, int captainNumber, int teamNum) {
        return execute(() -> {
            String json = chat(new PromptTemplate(draftTeamPromptTemplate()).apply(
                Map.of(
                    "round", round,
                    "turn", turn,
                    "captainNumber", captainNumber,
                    "teamNum", teamNum
                )
            ).text());
            json = LLMUtils.llmStringToJson(json);
            DraftTeamMessage.MessageData messageData = JacksonUtils.toType(json, DraftTeamMessage.MessageData.class);
            addMessage(new DraftTeamMessage(gameId, round, turn, messageData));
            return SpeakOrder.parse(messageData.getSpeakOrder());
        });
    }

    @Override
    public void speak(int round, int turn, int number) {
        execute(() -> {
            PlayerEntity playerEntity = aiComponent.playerEntityRepository.findById(new PlayerEntity.ID(gameId, number)).get();
            List<String> turnTexts = turnTexts(round, turn);
            String json = singleChat(new PromptTemplate(speakPromptTemplate()).apply(
                Map.of(
                    "historyThinking", playerEntity.getThinking(),
                    "turnMessages", String.join("\n", turnTexts)
                )
            ).text());
            json = LLMUtils.llmStringToJson(json);
            SpeakMessage.MessageData messageData = JacksonUtils.toType(json, SpeakMessage.MessageData.class);
            addMessage(new SpeakMessage(gameId, round, turn, messageData));
        });
    }

    @Override
    public Set<Integer> confirmTeam(int round, int turn) {
        return execute(() -> {
            String json = chat(new PromptTemplate(confirmTeamPromptTemplate()).apply(
                Map.of(
                )
            ).text());
            json = LLMUtils.llmStringToJson(json);
            ConfirmTeamMessage.MessageData messageData = JacksonUtils.toType(json, ConfirmTeamMessage.MessageData.class);
            addMessage(new ConfirmTeamMessage(gameId, round, turn, messageData));
            return messageData.getTeamNumbers();
        });
    }

    @Override
    public boolean vote(int round, int turn, Set<Integer> team) {
        return execute(() -> {
            String json = chat(new PromptTemplate(votePromptTemplate()).apply(
                Map.of(
                    "numbers", team.stream().map(String::valueOf).collect(Collectors.joining("、"))
                )
            ).text());
            json = LLMUtils.llmStringToJson(json);
            VoteMessage.MessageData messageData = JacksonUtils.toType(json, VoteMessage.MessageData.class);
            addMessage(new VoteMessage(gameId, round, turn, messageData));
            return messageData.isAgree();
        });
    }

    @Override
    public boolean mission(int round, int turn) {
        return execute(() -> {
            MissionMessage.MessageData messageData;
            if (CampType.BLUE == role.camp) {
                messageData = new MissionMessage.MessageData();
                messageData.setSuccess(true);
            } else {
                String json = chat(new PromptTemplate(missionPromptTemplate()).apply(
                    Map.of()
                ).text());
                json = LLMUtils.llmStringToJson(json);
                messageData = JacksonUtils.toType(json, MissionMessage.MessageData.class);
            }
            addMessage(new MissionMessage(gameId, round, turn, messageData));
            return messageData.isSuccess();
        });
    }

    @Override
    public void killSpeak(int number) {
        execute(() -> {
            String json = chat(new PromptTemplate(killSpeakPromptTemplate()).apply(
                Map.of()
            ).text());
            json = LLMUtils.llmStringToJson(json);
            KillSpeakMessage.MessageData messageData = JacksonUtils.toType(json, KillSpeakMessage.MessageData.class);
            addMessage(new KillSpeakMessage(gameId, messageData));
        });
    }

    @Override
    public int kill() {
        return execute(() -> {
            String json = chat(new PromptTemplate(killPromptTemplate()).apply(
                Map.of()
            ).text());
            json = LLMUtils.llmStringToJson(json);
            KillResultMessage.MessageData messageData = JacksonUtils.toType(json, KillResultMessage.MessageData.class);
            addMessage(new KillResultMessage(gameId, messageData));
            return messageData.getKillNumber();
        });
    }

    @Override
    public void thinking(int round, int turn) {
        execute(() -> {
            PlayerEntity playerEntity = aiComponent.playerEntityRepository.findById(new PlayerEntity.ID(gameId, number)).get();
            // 本轮消息
            List<String> turnTexts = turnTexts(round, turn);
            String thinking = singleChat(new PromptTemplate(thinkingPromptTemplate()).apply(
                Map.of(
                    "historyThinking", playerEntity.getThinking(),
                    "turnMessages", String.join("\n", turnTexts)
                )
            ).text());
            playerEntity.setThinking(thinking);
            aiComponent.playerEntityRepository.saveAndFlush(playerEntity);
        });
    }

    private List<String> turnTexts(int round, int turn) {
        List<String> texts = new ArrayList<>();
        List<Message> messages = messageService.messages(gameId, round, turn);
        for (Message message : messages) {
            if (Message.Source.HOST == message.source()) {
                texts.add(message.text());
            } else {
                PlayerMessage<?> playerMessage = (PlayerMessage<?>) message;
                if (number == playerMessage.getData().getNumber()) {
                    texts.add(message.text());
                } else {
                    // 对于部分消息要进行过滤
                    if ((playerMessage instanceof VoteMessage) || (playerMessage instanceof MissionMessage)) {
                        continue;
                    }
                    String text = playerMessage.getData().getNumber() + "号玩家：" + message.text();
                    texts.add(text);
                }
            }
        }
        return texts;
    }

    private String singleChat(String input) {
        List<String> texts = new ArrayList<>();
        // 系统消息
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        texts.add(systemMessage.text());
        // 本次输入
        messages.add(UserMessage.from(input));
        texts.add(input);
        String responseText = chatModel.chat(messages).aiMessage().text();
        // 保存记录
        AIChatEntity aiChatEntity = new AIChatEntity();
        aiChatEntity.setGameId(gameId);
        aiChatEntity.setMessages(JacksonUtils.toJSONString(texts));
        aiChatEntity.setText(responseText);
        aiComponent.aiChatEntityRepository.saveAndFlush(aiChatEntity);
        return responseText;
    }

    private String chat(String input) {
        List<String> texts = new ArrayList<>();
        // 系统消息
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        texts.add(systemMessage.text());
        // 历史消息
        List<Message> historyMessages = messageService.messages(gameId);
        for (Message message : historyMessages) {
            if (Message.Source.HOST == message.source()) {
                messages.add(UserMessage.from(message.text()));
                texts.add(message.text());
            } else {
                PlayerMessage<?> playerMessage = (PlayerMessage<?>) message;
                if (number == playerMessage.getData().getNumber()) {
                    messages.add(AiMessage.from(message.text()));
                    texts.add(message.text());
                } else {
                    // 对于部分消息要进行过滤
                    if ((playerMessage instanceof VoteMessage) || (playerMessage instanceof MissionMessage)) {
                        continue;
                    }
                    String text = playerMessage.getData().getNumber() + "号玩家：" + message.text();
                    messages.add(UserMessage.from(text));
                    texts.add(text);
                }
            }
        }
        // 本次输入
        messages.add(UserMessage.from(input));
        texts.add(input);
        String responseText = chatModel.chat(messages).aiMessage().text();
        // 保存记录
        AIChatEntity aiChatEntity = new AIChatEntity();
        aiChatEntity.setGameId(gameId);
        aiChatEntity.setMessages(JacksonUtils.toJSONString(texts));
        aiChatEntity.setText(responseText);
        aiComponent.aiChatEntityRepository.saveAndFlush(aiChatEntity);
        return responseText;
    }

    protected abstract SystemMessage systemMessage(String gameId, int number, Map<Integer, PlayerRole> roles);

    protected String draftTeamPromptTemplate() {
        return """
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
    }

    protected String speakPromptTemplate() {
        return """
            下面由你进行发言，请结合历史的推理信息和本轮的信息进行发言，并按返回格式进行返回
            
            # 历史推理信息
            {{historyThinking}}
            
            # 本轮信息
            {{turnMessages}}
            
            # 注意
            - 请不要重复其它玩家的发言，而是结合他们的发言表达自己的观点
            - 严格按照返回格式进行返回
            
            # 返回格式
            {
                "speak": "发言内容"
            }
            """;
    }

    protected String killSpeakPromptTemplate() {
        return """
            下面由你进行发言，帮助队友分析哪位玩家是梅林，并按返回格式进行返回
            
            # 返回格式
            {
                "speak": "发言内容"
            }
            """;
    }

    protected String confirmTeamPromptTemplate() {
        return """
            请队长进行总结发言，并请确认最终车队人员
            
            # 返回格式
            {
                "content": "发言内容",
                "teamNumbers": [1, 2, 3]
            }
            """;
    }

    protected String votePromptTemplate() {
        return """
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
    }

    protected String missionPromptTemplate() {
        return """
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
    }

    protected String killPromptTemplate() {
        return """
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
    }

    protected String thinkingPromptTemplate() {
        return """
            结合本轮信息和历史的推理信息，推理各个玩家的身份信息，并按返回格式进行返回
            
            # 历史推理内容
            {{historyThinking}}
            
            # 本轮信息
            {{turnMessages}}
            
            # 返回示例
            | 玩家 | 身份推测 | 推测原因 |
            |------|------------------|------|
            | 1号 | ❓红方（刺客）或蓝方（忠臣） | 本轮任务失败，身份存疑 |
            | 2号 | ✅红方（莫甘娜） | 已暴露身份，但仍在游戏中 |
            | 3号 | ✅蓝方（梅林） | 已确认身份，需继续隐藏 |
            | 4号 | ❓蓝方（忠臣）或红方（爪牙） | 本轮投失败票，身份存疑 |
            | 5号 | ✅蓝方（派西维尔） | 我自己，看到2号和3号 |
            """;
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

    @Getter
    @AllArgsConstructor
    public static class AIComponent {
        private MessageService messageService;
        private PlayerEntityRepository playerEntityRepository;
        private AIChatEntityRepository aiChatEntityRepository;
    }

}
