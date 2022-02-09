package jk.core.builder;

/**
 * 在使用builder解析是，进行builder解析前，进行一次回调，此时的回调，对关键信息参数ParseInfo已经进行初始化，此时可以对ParseInfo进行而额外操作
 * @author liguohui lgh1177@126.com
 */
public interface ParseBeforeAction {
    void exec(ParseBuilder builder);
}
