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
public class AssassinAIPlayer extends AIPlayer {

    public AssassinAIPlayer(String gameId, int number, PlayerRole role, Map<Integer, PlayerRole> roles, AIComponent aiComponent) {
        super(gameId, number, role, roles, aiComponent);
    }

    @Override
    protected SystemMessage systemMessage(String gameId, int number, Map<Integer, PlayerRole> roles) {
        String playerNumbers = roles.entrySet().stream()
            .filter(p -> PlayerRole.MORGANA == p.getValue() || PlayerRole.ASSASSIN == p.getValue()
                || PlayerRole.MORDRED == p.getValue() || PlayerRole.OBERON == p.getValue())
            .map(p -> String.valueOf(p.getKey()))
            .collect(Collectors.joining("、"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("RULE", PromptConstants.RULE);
        variables.put("number", number);
        variables.put("playerNumbers", playerNumbers);
        String text = new PromptTemplate("""
            # 角色设定
            你现在在参与一款名为阿瓦隆的身份类游戏，你是{{number}}号玩家，并作为里面的游戏角色`刺客`参与到游戏中，请熟记游戏规则并尽最大可能保证`红方`胜利。
            
            # 你的红方队友号码
            {{playerNumbers}}
            
            # 注意
            - 不要随意暴露自己的红方身份，伪装成蓝方迷惑对手
            
            {{RULE}}
            """).apply(variables).text();
        return SystemMessage.from(text);
    }
}
