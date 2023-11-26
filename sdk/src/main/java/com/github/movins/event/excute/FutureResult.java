package com.github.movins.event.excute;

public class FutureResult<T> implements ExcuteFuture<T>, InvokeCallback<T> {
    protected ExcuteCallback<T> callback;
    protected ExcuteInvoke<T> invoke;

    public FutureResult() {
    }

    public FutureResult(ExcuteInvoke invoke) {
        this.invoke = invoke;
    }

    @Override
    public boolean excute(ExcuteCallback<T> callback) {
        this.callback = callback;
        return this.invoke != null ? this.invoke.onExcute(this) : false;
    }

    @Override
    public void before(T data) {
        if (this.callback != null) {
            this.callback.onBefore(data);
        }
    }

    @Override
    public void progress(int value) {
        if (this.callback != null) {
            this.callback.onProgress(value);
        }
    }

    @Override
    public void success(T data) {
        if (this.callback != null) {
            this.callback.onSuccess(data);
        }
    }

    @Override
    public void error(int code, String desc) {
        if (this.callback != null) {
            this.callback.onError(code, desc);
        }
    }
}
