package com.github.movins.event.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 功能：事件分发
 * Created by movinliao
 */
public class Dispatcher implements IDispatcher{
    private String mBaseKey;

    public Dispatcher() {
        mBaseKey = this.toString();
    }

    @Override
    public void addListener(String key, String method) {
        addListener(key, method, EventConst.NORMAL);
    }

    @Override
    public void addListener(String key, String method, int priority) {
        addListener(key, this, method, EventConst.NORMAL);
    }

    @Override
    public void addListener(String key, String method, int priority, boolean async) {
        addListener(key, this, method, priority, async);
    }

    @Override
    public void addListener(String key, Object listener, String method, int priority) {
        EventCenter.addListener(mBaseKey, key, new Notify(listener, method), priority);
    }

    @Override
    public void addListener(String key, Object listener, String method, int priority, boolean async) {
        EventCenter.addListener(mBaseKey, key, new Notify(listener, method, async), priority);
    }

    @Override
    public void removeListener(String key, String method) {
        removeListener(key, method, EventConst.NORMAL);
    }

    @Override
    public void removeListener(String key, String method, int priority) {
        removeListener(key, this, method, priority);
    }

    @Override
    public void removeListener(String key, Object listener, String method, int priority) {
        EventCenter.removeListener(mBaseKey, key, new Notify(listener, method), priority);
    }

    @Override
    public <T extends Object> void dispatch(String key, BaseEvent<T> event) {
        EventCenter.dispatch(mBaseKey, key, event);
    }

    @Override
    public <T extends Object> void doEvent(String key, T data) {
        BaseEvent<T> evt = new BaseEvent<T>(key, data);
        dispatch(key, evt);
    }

    @Override
    public void on(String key, String method) {
        addListener(key, method, EventConst.NORMAL, true);
    }

    @Override
    public void on(String key, Object listener, String method) {
        addListener(key, listener, method, EventConst.NORMAL, true);
    }

    @Override
    public void on(String key, String method, int priority) {
        addListener(key, method, priority, true);
    }

    @Override
    public void on(String key, Object listener, String method, int priority) {
        addListener(key, listener, method, priority, true);
    }

    @Override
    public void off(String key, String method) {
        removeListener(key, method, EventConst.NORMAL);
    }

    @Override
    public void off(String key, Object listener, String method) {
        removeListener(key, listener, method, EventConst.NORMAL);
    }

    @Override
    public void off(String key, String method, int priority) {
        removeListener(key, method, priority);
    }

    @Override
    public void off(String key, Object listener, String method, int priority) {
        removeListener(key, listener, method, priority);
    }

    @Override
    public void clear() {
        EventCenter.clear(mBaseKey);
    }

    @Override
    public void clear(String key, Object listener) {
        EventCenter.clear(mBaseKey, key, listener);
    }

    @Override
    public void setPaused(boolean paused) {
        EventCenter.setPause(mBaseKey, paused);
    }

    @Override
    public void addEvents(Object target, Class keyMap) {
        Class targetCls = target.getClass();
        addEvents(target, targetCls, keyMap);
    }

    @Override
    public void addEvents(Object target, Class targetCls, Class keyMap) {
        if (targetCls == null || keyMap == null) {
            return;
        }
        ArrayList<String> keys = getEventKeys(keyMap);
        if (keys.isEmpty()) {
            return;
        }
        for (Method method : targetCls.getDeclaredMethods()) {
            OnListener an = method.getAnnotation(OnListener.class);
            if (an == null) continue;
            String key = an.key();
            if (key.isEmpty()) continue;
            if (!keys.contains(key)) continue;

            addListener(key, target, method.getName(), an.priority(), an.sync());
        }
    }

    @Override
    public void removeEvents(Object target, Class keyMap) {
        Class targetCls = target.getClass();
        removeEvents(target, targetCls, keyMap);
    }

    @Override
    public void removeEvents(Object target, Class targetCls, Class keyMap) {
        if (targetCls == null || keyMap == null) {
            return;
        }
        ArrayList<String> keys = getEventKeys(keyMap);
        if (keys.isEmpty()) {
            return;
        }

        for (Method method : targetCls.getDeclaredMethods()) {
            OnListener an = method.getAnnotation(OnListener.class);
            if (an == null) continue;
            String key = an.key();
            if (key.isEmpty()) continue;
            if (!keys.contains(key)) continue;

            removeListener(key, target, method.getName(), an.priority());
        }
    }

    private ArrayList<String> getEventKeys(Class cls) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (Field field : cls.getDeclaredFields()) {
                EventKey an = field.getAnnotation(EventKey.class);
                int modifiers  = field.getModifiers();
                if((an != null) && Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    String value = field.get(cls).toString();
                    if (!value.isEmpty()) result.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
