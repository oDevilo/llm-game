/**
 * node_async 的state应该是每次重新拷贝出来，返回 Map 是需要覆盖的字段，返回空map不会替换内容
 * addConditionalEdges 中的逻辑最好只处理条件判断不处理持久化相关
 * Edge 执行后触发 checkpoint
 *
 * @author Devil
 */
package io.github.devil.llm.avalon.game;