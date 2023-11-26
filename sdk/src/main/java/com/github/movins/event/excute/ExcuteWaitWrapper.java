package com.github.movins.event.excute;

public class ExcuteWaitWrapper extends ExcuteWaiter {
    private ExcuteTask task;

    public ExcuteWaitWrapper(ExcuteTask task) {
        this.task = task;
        this.task.create(this);
    }

    public boolean finished() {
        return this.task.finished();
    }
}
