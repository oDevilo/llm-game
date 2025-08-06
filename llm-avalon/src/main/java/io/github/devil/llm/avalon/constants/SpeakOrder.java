package io.github.devil.llm.avalon.constants;

/**
 * @author Devil
 */
public enum SpeakOrder {
    CLOCKWISE, // 顺时针
    COUNTERCLOCKWISE, // 逆时针
    ;

    public static SpeakOrder parse(String v) {
        if (SpeakOrder.CLOCKWISE.name().equalsIgnoreCase(v)) {
            return SpeakOrder.CLOCKWISE;
        } else {
            return SpeakOrder.COUNTERCLOCKWISE;
        }
    }
}
