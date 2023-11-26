package com.github.movins.event.excute;

public abstract class ExcuteCallbackWrapper<T> extends ExcuteCallback<T> {
    public ExcuteCallbackWrapper() {
    }

    public abstract void onResult(int code, T data, Throwable error);

    @Override
    public void onProgress(int progress) {
    }
    @Override
    public void onSuccess(T data) {
        this.onResult(200, data, null);
    }
    @Override
    public void onError(int code, String desc) {
        this.onResult(code, null, new Throwable(desc));
    }
}
