//package io.github.devil.llm.avalon.game.runtime;
//
//import io.github.devil.llm.avalon.game.runtime.message.Message;
//import io.github.devil.llm.avalon.game.runtime.message.host.HostMessage;
//import io.github.devil.llm.avalon.utils.json.JacksonUtils;
//
//import java.util.List;
//
///**
// * 历史消息
// * @author Devil
// */
//public class MessageHistory {
//
//    private final List<Message> messages;
//
//    public MessageHistory(List<Message> messages) {
//        this.messages = messages;
//    }
//
//    public void add(Message message) {
//        if (message instanceof HostMessage) {
//            System.out.println(message.text());
//        } else {
//            System.out.println(JacksonUtils.toJSONString(message));
//        }
//        messages.add(message);
//    }
//
//    public List<Message> messages() {
//        return messages;
//    }
//
//    public HostMessage lastHostMessage() {
//        for (int i = messages.size() - 1; i >= 0; i--) {
//            Message message = messages.get(i);
//            if (Message.Source.HOST == message.source()) {
//                return (HostMessage) message;
//            }
//        }
//        return null;
//    }
//
//}
