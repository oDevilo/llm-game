package io.github.devil.llm.test;

import io.github.devil.llm.avalon.game.runtime.message.Message;
import io.github.devil.llm.avalon.utils.ReflectionUtils;

import java.util.Set;

/**
 * @author Devil
 */
public class Test2 {

    public static void main(String[] args) {
        Set<Class<? extends Message>> classes = ReflectionUtils.subTypesOf(Message.class);
        System.out.println(classes.size());
    }
}
