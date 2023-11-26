package com.github.movins.event.excute;

public class ExcuteWrapper<T> extends ExcuteCallbackWrapper<T> {
    private ExcuteTask<T> task;

    public ExcuteWrapper(ExcuteTask<T> task) {
        this.task = task;
    }

    public void onResult(int code, T data, Throwable error) {
        this.task.create(code, data, error);
    }
}
