package io.github.devil.llm.test;

import io.github.devil.llm.avalon.game.message.player.SpeakMessage;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;

/**
 * @author Devil
 */
public class Test {

    public static void main(String[] args) {
        String json = """
            {
                "thinking": "作为刺客，我需要隐藏身份并观察派西维尔的言行，同时保护红方队友5号。3号玩家称自己是派西维尔，但我们需要怀疑他的真实性。派西维尔知道梅林和莫甘娜的身份，但他无法确定谁是梅林，所以他会试图试探任务队员的反应。选择5号可能是合理的，但我们需要进一步验证3号是否真的是派西维尔，同时保护红方的利益。",
                "speak": "我作为4号玩家支持3号和5号的组队提议。首先，3号称自己是派西维尔，而派西维尔确实需要通过试探来判断梅林的身份。如果他真是派西维尔，那他选择5号是有逻辑的。其次，5号的身份需要被观察，但我们红方可以借此机会伪装并误导派西维尔的判断。我同意这个队伍，让我们看看任务执行的结果如何。",
            }
            """;
        SpeakMessage.MessageData messageData = JacksonUtils.toType(json, SpeakMessage.MessageData.class);
        System.out.println(11);
    }
}
