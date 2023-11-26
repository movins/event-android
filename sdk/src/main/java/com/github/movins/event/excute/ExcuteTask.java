package com.github.movins.event.excute;

public class ExcuteTask<T> {
    private ExcuteWaiter waiter;
    private ExcuteResult<T> result;
    private boolean finished = false;

    public ExcuteTask() {
    }

    public void create(ExcuteWaiter waiter) {
        this.waiter = waiter;
    }

    public void create(int code) {
        if (this.result == null) {
            this.result = new ExcuteResult(code);
        }
    }

    void create(int code, T data, Throwable exception) {
        if (this.result == null) {
            this.result = new ExcuteResult(code, data, exception);
            this.finished = true;
            if (this.waiter != null) {
                this.waiter.finished();
            }
        }
    }

    public boolean finished() {
        return this.finished;
    }

    public ExcuteResult<T> getResult() {
        return this.result;
    }
}
