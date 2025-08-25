package io.github.devil.llm.avalon.game.player.ai;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.constants.PlayerRole;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class MerlinAIPlayer extends AIPlayer {

    public MerlinAIPlayer(String gameId, int number, PlayerRole role, Map<Integer, PlayerRole> roles, AIComponent aiComponent) {
        super(gameId, number, role, roles, aiComponent);
    }

    @Override
    protected SystemMessage systemMessage(String gameId, int number, Map<Integer, PlayerRole> roles) {
        String redNumbers = roles.entrySet().stream()
            .filter(e -> PlayerRole.MORGANA == e.getValue() || PlayerRole.ASSASSIN == e.getValue()
                || PlayerRole.OBERON == e.getValue() || PlayerRole.CLAWS == e.getValue()).map(e -> String.valueOf(e.getKey()))
            .collect(Collectors.joining("、"));
        // 参数
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", PromptConstants.RULE);
        variables.put("number", number);
        variables.put("redNumbers", redNumbers);
        String text = new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`梅林`参与到游戏中，请熟记游戏规则并尽最大可能保证`蓝方`胜利。
            
            # 红方玩家号码
            {{redNumbers}}
            
            # 注意
            - 不要随意暴露自己的真实身份
            - 你可以伪装成其它蓝方身份来隐藏自己
            
            {{RULE}}
            """).apply(variables).text();
        return SystemMessage.from(text);
    }
}
