package io.github.devil.llm.avalon.assistant;

import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import io.github.devil.llm.avalon.constants.CommonConstants;
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

    private static Assistant create(Request request, PromptTemplate systemPrompt, Map<String, Object> variables) {
        return AiServices.builder(Assistant.class)
            .chatModel(Common.chatModel())
            .chatMemory(new GameChatMemory(request.gameId, request.number, request.messageHistory))
            .systemMessageProvider(o -> {
                String text = systemPrompt.apply(variables).text();
//                System.out.println(text);
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
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`梅林`参与到游戏中，请熟记游戏规则并尽最大可能保证`蓝方`胜利。
            
            # 红方玩家号码
            {{playerNumbers}}
            
            # 注意
            - 不要随意暴露自己的真实身份
            - 你可以伪装成其它蓝方身份来隐藏自己
        
            {{RULE}}
            """), variables);
    }

    private static Assistant percival(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.MERLIN == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`派西维尔`参与到游戏中，请熟记游戏规则并尽最大可能保证`蓝方`胜利。
            
            # 莫甘娜或梅林的号码
            {{playerNumbers}}
            
            # 注意
            - 不要随意暴露自己看到的两个号码，因为这样红方可以推断出来梅林的号码
            - 你需要尽快确认梅林的号码，并帮助他隐藏身份
            
            {{RULE}}
            """), variables);
    }

    private static Assistant loyalMinister(Request request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`忠臣`参与到游戏中，请熟记游戏规则并尽最大可能保证`蓝方`胜利。
            
            {{RULE}}
            """), variables);
    }

    private static Assistant morgana(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`莫甘娜`参与到游戏中，请熟记游戏规则并尽最大可能保证`红方`胜利。
            
            # 你的红方队友号码
            {{playerNumbers}}
            
            # 注意
            - 由于`派西维尔`可以看到你和`梅林`，所以你要伪装，让他更相信你是`梅林`
            
            {{RULE}}
            """), variables);
    }

    private static Assistant assassin(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`刺客`参与到游戏中，请熟记游戏规则并尽最大可能保证`红方`胜利。
            
            # 你的红方队友号码
            {{playerNumbers}}
            
            # 注意
            - 由于`派西维尔`可以看到你和`梅林`，所以你要伪装，让他更相信你是`梅林`
            
            {{RULE}}
            """), variables);
    }

    private static Assistant mordred(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`莫德雷德`参与到游戏中，请熟记游戏规则并尽最大可能保证`红方`胜利。
            
            # 你的红方队友号码
            {{playerNumbers}}
            
            # 注意
            - `梅林`看不到你，所以你可以更容易得伪装成蓝方成员，争取到更多上车出任务的可能，使任务更容易失败
            
            {{RULE}}
            """), variables);
    }

    private static Assistant oberon(Request request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`奥伯伦`参与到游戏中，请熟记游戏规则并尽最大可能保证`红方`胜利。
            
            # 你的红方队友号码
            {{playerNumbers}}
            """), variables);
    }

    private static Assistant claws(Request request) {
        String playerNumbers = request.players.stream()
            .filter(p -> Player.Role.MORGANA == p.getRole() || Player.Role.ASSASSIN == p.getRole()
                || Player.Role.MORDRED == p.getRole() || Player.Role.CLAWS == p.getRole())
            .map(p -> String.valueOf(p.getNumber()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", CommonConstants.RULE);
        variables.put("number", request.number);
        variables.put("playerNumbers", playerNumbers);
        return create(request, new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`爪牙`参与到游戏中，请熟记游戏规则并尽最大可能保证`红方`胜利。
            
            # 你的红方队友号码
            {{playerNumbers}}
            
            {{RULE}}
            """), variables);
    }

}
