package com.github.movins.event;

import com.github.movins.event.core.IDispatcher;

import java.util.Set;

/**
 * 作者：movinliao
 * 邮箱：
 * 说明：
 */
public interface IModule extends IDispatcher {
    void init(Object api);
    void destroy();

    void start();
    void stop();

    void back();
    void fore();

    void doEvent(String key, Object data, int priority);
    void addBlock(String key, IBlock block);
    void removeBlock(String key);

    <T> T block(Class<T> cls);
    boolean hasBlock(String key);
    boolean isInited();
    Object getApi();
    Set<String> keys();

    void log(Object tag, String format, Object... args);

    Object call(Class cls, String cmd, Object... args);
}
