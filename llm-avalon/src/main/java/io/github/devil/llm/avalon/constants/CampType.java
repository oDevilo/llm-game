package io.github.devil.llm.avalon.constants;

/**
 * @author Devil
 */
public enum CampType {
    UNKNOWN,
    BLUE,
    RED,
    ;

    public static CampType parse(String value) {
        for (CampType v : values()) {
            if (v.name().equals(value)) {
                return v;
            }
        }
        return UNKNOWN;
    }
}
