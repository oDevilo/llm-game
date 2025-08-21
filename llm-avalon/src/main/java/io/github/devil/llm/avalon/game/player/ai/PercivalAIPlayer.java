package io.github.devil.llm.avalon.game.player.ai;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.constants.PlayerRole;
import io.github.devil.llm.avalon.game.service.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class PercivalAIPlayer extends AIPlayer {

    public PercivalAIPlayer(String gameId, int number, PlayerRole role, MessageService messageService, Map<Integer, PlayerRole> roles) {
        super(gameId, number, role, messageService, roles);
    }

    @Override
    protected SystemMessage systemMessage(String gameId, int number, Map<Integer, PlayerRole> roles) {
        String playerNumbers = roles.entrySet().stream()
            .filter(e -> PlayerRole.MORGANA == e.getValue() || PlayerRole.MERLIN == e.getValue())
            .map(e -> String.valueOf(e.getKey()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", PromptConstants.RULE);
        variables.put("number", number);
        variables.put("playerNumbers", playerNumbers);
        String text = new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`派西维尔`参与到游戏中，请熟记游戏规则并尽最大可能保证`蓝方`胜利。
            
            # 莫甘娜或梅林的号码
            {{playerNumbers}}
            
            # 注意
            - 不要随意暴露自己看到的两个号码，因为这样红方可以推断出来梅林的号码
            - 你需要尽快确认梅林的号码，并帮助他隐藏身份
            
            {{RULE}}
            """).apply(variables).text();
        return SystemMessage.from(text);
    }
}
