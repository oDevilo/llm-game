package io.github.devil.llm.test;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devil.llm.avalon.game.runtime.message.Message;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;

import java.util.Map;

/**
 * @author Devil
 */
public class Test {

    public static void main(String[] args) {
        String json = """
            {
              "content": "我建议让2号和4号玩家出任务。2号玩家看起来发言比较中立，而4号玩家在讨论中显得较为沉默，我们需要观察他们的行为。",
              "speakOrder": "CLOCKWISE"
            }
            """;
        Map<String, Object> type = JacksonUtils.toType(json, new TypeReference<>() {
        });
        type.put("type", "DraftTeamMessage");
        type.put("number", "1");
        json = JacksonUtils.toJSONString(type);
        Message message = JacksonUtils.toType(json, Message.class);
        System.out.println(11);
    }
}
