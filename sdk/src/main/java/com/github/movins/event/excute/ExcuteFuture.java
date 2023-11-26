package com.github.movins.event.excute;

public interface ExcuteFuture<T> {
    boolean excute(ExcuteCallback<T> callback);
}
