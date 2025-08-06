package io.github.devil.llm.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Devil
 */
public class TestJson {

    public static void main(String[] args) {
        String json = """
            {
              "content": "我建议让3号（我自己）和5号加入任务队伍。因为作为红方核心，我和5号玩家能够更好地把控任务节奏，确保任务的失败。同时，5号玩家在之前的讨论中表现得非常冷静，是一个可靠的队友。",
              "speakOrder": "CLOCKWISE"
            }
            """;
//        String json = "这是一个包含{一些}需要{提取的内容}的例子";
        Pattern pattern = Pattern.compile("\\{[\\s\\S]*?}");
        Matcher matcher = pattern.matcher(json);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            // matcher.group(1) 获取第一个捕获组，即{}中的内容
            matches.add(matcher.group(0));
        }
        System.out.println(matches.size());
    }
}
