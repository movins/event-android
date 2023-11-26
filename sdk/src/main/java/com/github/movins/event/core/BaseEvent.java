package com.github.movins.event.core;

import com.github.movins.event.data.Serializedata;

/**
 * 功能：事件数据基类
 * Created by movinliao
 */
public class BaseEvent<T extends Object> extends Serializedata {
    public String key;
    public T eventData = null;

    public BaseEvent() {}
    public BaseEvent(String key) {
        this.key = key;
    }

    public BaseEvent (String key, T data) {
        this.key = key;
        this.eventData = data;
    }

    public void then(T data) {
    }

    public void error(String msg) {}
}
