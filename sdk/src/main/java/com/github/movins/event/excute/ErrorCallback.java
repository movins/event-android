package com.github.movins.event.excute;

public abstract class ErrorCallback<T> extends ExcuteCallback<T> {
    @Override
    public void onProgress(int progress) {
    }

    @Override
    public void onSuccess(T data) {

    }
}
