package io.github.devil.llm.avalon.utils;

/**
 * @author Devil
 */
public class LLMUtils {

    /**
     * 扩符匹配算法 处理json字符串
     * 1、从第一个左花括号开始匹配，中间有坐花括号 就找第一个右花括号抵消
     * 2、如果第一个花括号都被匹配完 则直接终止处理
     * 3、如果执行到最后都没有匹配第一个左花括号 则补一个右花括号
     *
     * 需要格式化一下，兼容下面两种格式
     * 格式1："```json\\n{\\n \"intent\": \"故障提报\"\\n}\\n```"
     * 格式2："{\\n \"intent\": \"故障提报\"\\n}";
     */
    public static String llmStringToJson(String llmStr) {
        int leftBrace = 0;
        int endBrace = 0;

        int start = llmStr.indexOf("{");

        if (start == -1) {
            llmStr = "{" + llmStr;
            start = 0;
        }

        for (int i = start; i <= llmStr.length() - 1; i++) {
            char c = llmStr.charAt(i);
            if (c == '{') {
                leftBrace ++;
            }
            if (c == '}') {
                leftBrace --;
            }
            if (leftBrace == 0) {
                endBrace = i;
                break;
            }
        }

        if (llmStr.charAt(endBrace) != '}') {
            llmStr += '}';
            llmStr = llmStr.substring(start);
        } else {
            llmStr = llmStr.substring(start, endBrace + 1);
        }

        return llmStr;
    }
}
