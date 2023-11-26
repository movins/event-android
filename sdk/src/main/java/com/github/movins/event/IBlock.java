package com.github.movins.event;

import com.github.movins.event.core.IDispatcher;

import java.lang.reflect.InvocationHandler;

/**
 * 作者：movinliao
 * 邮箱：
 * 说明：
 */
public interface IBlock extends InvocationHandler, IDispatcher {
    void init(Object module);
    void destroy();

    void start();
    void stop();

    void back();
    void fore();

    boolean isStoped();
    Object getModule();

    void log(Object tag, String format, Object... args);

    Object call(String cmd, Object[] args);
}
