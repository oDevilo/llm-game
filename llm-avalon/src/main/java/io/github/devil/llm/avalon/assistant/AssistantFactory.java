package io.github.devil.llm.avalon.assistant;

import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import io.github.devil.llm.avalon.game.runtime.MessageHistory;
import io.github.devil.llm.avalon.game.runtime.Player;
import io.github.devil.llm.avalon.llm.Common;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class AssistantFactory {

    @AllArgsConstructor
    public static class Request {
        private String gameId;
        private int number;
        private List<Player> players;
        private MessageHistory messageHistory;
        private Player.Role type;
    }

    public static Assistant build(Request request) {
        switch (request.type) {
            case MERLIN -> {
                return merlin(request);
            }
            case PERCIVAL -> {
                return percival(request);
            }
            case Loyal_Minister -> {
                return loyalMinister(request);
            }
            case MORGANA -> {
                return morgana(request);
            }
            case ASSASSIN -> {
                return assassin(request);
            }
            case MORDRED -> {
                return mordred(request);
            }
            case OBERON -> {
                return oberon(request);
            }
            case CLAWS -> {
                return claws(request);
            }
        }
        return null;
    }

    private static final String RULE = """
        # 角色设定
        你现在在参与一款名为阿瓦隆的身份类游戏，你作为里面的游戏角色参与到游戏中，请熟记下面游戏规则，并结合你的游戏角色在对应环节进行回答或者投票等操作。
        请注意：不要随意暴露自己的真实身份，并在发言中让对方阵营信任你并让自己队友可以更好发现你
        
        # 游戏规则
        '''
        游戏分为红、蓝两个阵营
        
        ## 胜利条件
        蓝方：赢得三局任务的胜利，并隐藏真正的梅林
        红方：让三局任务失败，或者找到真正的梅林并刺杀成功
        
        ## 角色介绍
        蓝方阵营：
        - 梅林：蓝方的核心角色，他能看到除了 莫德雷德之外的所有坏人，他需要在游戏中隐藏自己， 给派西维尔透露信息。
        - 派西维尔：蓝方的领队人，他能看到莫甘娜和梅林，但不知道谁是真正的梅林，他需要确定真正的梅林，然后带领蓝方赢得游戏
        - 忠臣：蓝方成员，他们只能看到自己的身份牌，需要跟随派西维尔的领导让任务成功，并且隐藏真正的梅林身份。
        
        红方阵营：
        - 莫甘娜：红方的领头人，能看见除奥伯伦之外的红方其他队员，同时能被梅林看见和派西维尔看见，但派西维尔并不知道她的真正身份。可以装作成梅林来迷惑派西维尔。
        - 刺客：红方的精英，他能被梅林看见，他的任务是寻找真正的梅林，并在蓝方赢得三局游戏后刺杀梅林，刺杀成功则红方胜利。
        - 莫德雷德：红方的隐藏角色，梅林晚上看不到他，所以他隐藏在平民里，混进队伍让任务失败。
        - 奥伯伦：红方角色，但是他看不到他的队友，他的队友也看不到他，他能被梅林看见。
        - 爪牙：红方角色，他能被梅林看见。
        
        ## 游戏流程
        1. 队长组队
        每个回合系统指派一名玩家为队长，队长根据该回合队伍人数组队，可以选自己。
        
        2. 线下讨论
        由队长指定由顺时针或逆时针方向，玩家进行一次发言展开讨论，表明自己对该次组队的看法或提供其它线索。
        
        3. 玩家投票
        讨论结束后，每个玩家投票表决是否同意这个队伍执行任务。超过半数玩家同意则组队成功，否则组队失败该局流局，由下一个队长重新组队。
        如果累计流局5次，红方直接胜出。
        
        4. 执行任务
        在队伍中的玩家选择“任务成功”或者“任务失败”，蓝方玩家只能选择任务成功，红方玩家可自行选择。
        
        任务成功：全部为成功票，否则为任务失败。（特例：7、8、9、10人局，第四轮需要有两个失败票才算任务失败）
        
        5. 刺杀梅林
        如果蓝方率先获得三个任务成功，则进入刺杀梅林环节。
        在刺杀梅林之前，红方玩家可以发言讨论决定要刺杀谁，如果梅林被刺杀则红方胜利，梅林没有被刺杀则蓝方胜利。
        
        ## 游戏配置
        人数对应阵营角色
        |人数|蓝方阵营|红方阵营|
        |---|---|---|
        | 5 | 梅林、派西维尔、忠臣 | 莫甘娜、刺客 |
        | 6 | 梅林、派西维尔、忠臣、忠臣 | 莫甘娜、刺客 |
        | 7 | 梅林、派西维尔、忠臣、忠臣 | 莫甘娜、刺客、奥伯伦 |
        | 8 | 梅林、派西维尔、忠臣、忠臣、忠臣 | 莫甘娜、刺客、爪牙 |
        | 9 | 梅林、派西维尔、忠臣、忠臣、忠臣、忠臣 | 莫甘娜、刺客、莫德雷德 |
        | 10 | 梅林、派西维尔、忠臣、忠臣、忠臣、忠臣 | 莫甘娜、刺客、莫德雷德、奥伯伦 |
        
        人数对应每轮出任务人数
        |人数|第一轮|第二轮|第三轮|第四轮|第五轮|
        |---|---|---|---|---|---|
        | 5 | 2 | 3 | 2 | 3 | 3 |
        | 6 | 2 | 3 | 4 | 3 | 4 |
        | 7 | 2 | 3 | 3 | 4 | 4 |
        | 8 | 3 | 4 | 4 | 5 | 5 |
        | 9 | 3 | 4 | 4 | 5 | 5 |
        | 10 | 3 | 4 | 4 | 5 | 5 |
        '''
        
        """;

    private static Assistant create(Request request, PromptTemplate systemPrompt, Map<String, Object> variables) {
        return AiServices.builder(Assistant.class)
            .chatModel(Common.chatModel())
            .chatMemory(new GameChatMemory(request.gameId, request.number, request.messageHistory))
            .systemMessageProvider(o -> {
                String text = systemPrompt.apply(variables).text();
                return text;
            })
            .build();
    }

    private static Assistant merlin(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.OBERON == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        // 参数
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`梅林`
            
            # 红方玩家号码
            {{playerNumbers}}
            """), variables);
    }

    private static Assistant percival(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.MERLIN == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`派西维尔`
            
            # 莫甘娜或梅林的号码
            {{playerNumbers}}
            """), variables);
    }

    private static Assistant loyalMinister(Request request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`忠臣`
            """), variables);
    }

    private static Assistant morgana(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`莫甘娜`
            
            # 你的红方队友号码
            {{playerNumbers}}
            """), variables);
    }

    private static Assistant assassin(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`刺客`
            
            # 你的红方队友号码
            {{playerNumbers}}
            """), variables);
    }

    private static Assistant mordred(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`莫德雷德`
            
            # 你的红方队友号码
            {{playerNumbers}}
            """), variables);
    }

    private static Assistant oberon(Request request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`奥伯伦`
            """), variables);
    }

    private static Assistant claws(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", RULE);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            {{RULE}}
            
            # 游戏角色
            你的角色是`爪牙`
            
            # 你的红方队友号码
            {{playerNumbers}}
            """), variables);
    }

}
