package com.github.movins.event.core;

/**
 * Created by movinliao
 * 邮箱：
 * 说明：
 */
public interface IDispatcher {
    void addListener(String key, String method);
    void addListener(String key, String method, int priority);
    void addListener(String key, String method, int priority, boolean async);
    void addListener(String key, Object listener, String method, int priority);
    void addListener(String key, Object listener, String method, int priority, boolean async);

    void removeListener(String key, String method);
    void removeListener(String key, String method, int priority);
    void removeListener(String key, Object listener, String method, int priority);

    <T extends Object>void dispatch(String key, BaseEvent<T> event);
    <T extends Object>void doEvent(String key, T data);

    void on(String key, String method);
    void on(String key, Object listener, String method);
    void on(String key, String method, int priority);
    void on(String key, Object listener, String method, int priority);

    void off(String key, String method);
    void off(String key, Object listener, String method);
    void off(String key, String method, int priority);
    void off(String key, Object listener, String method, int priority);

    void clear();
    void clear(String key, Object listener);

    void setPaused(boolean paused);

    void addEvents(Object target, Class keyMap);
    void addEvents(Object target, Class targetCls, Class keyMap);
    void removeEvents(Object target, Class keyMap);
    void removeEvents(Object target, Class targetCls, Class keyMap);
}
