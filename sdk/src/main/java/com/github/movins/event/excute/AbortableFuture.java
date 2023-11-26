package com.github.movins.event.excute;

public interface AbortableFuture<T> extends ExcuteFuture<T> {
    boolean abort();
}
