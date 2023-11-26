package com.github.movins.event.excute;

public abstract class ExcuteCallback<T> implements WatchCallback<T> {
    @Override
    public void onBefore(T data) {
    }
}
