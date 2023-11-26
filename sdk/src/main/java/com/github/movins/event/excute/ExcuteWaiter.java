package com.github.movins.event.excute;

public abstract class ExcuteWaiter {
    private boolean valid;

    public ExcuteWaiter() {
    }

    public abstract boolean finished();

    public boolean excute(long time) throws InterruptedException, ExcuteException {
        long current = System.currentTimeMillis();
        synchronized(this) {
            if (this.finished()) {
                return true;
            } else if (time <= 0L) {
                return this.finished();
            } else {
                do {
                    try {
                        this.valid = true;
                        this.wait(time);
                    } catch (InterruptedException exception) {
                        this.valid = false;
                        throw exception;
                    }

                    if (this.finished()) {
                        this.valid = false;
                        return true;
                    }

                    time -= System.currentTimeMillis() - current;
                } while(time > 0L);

                this.valid = false;
                throw new ExcuteException("wait time out");
            }
        }
    }

    public void check() {
        if (this.valid) {
            synchronized(this) {
                if (this.finished()) {
                    this.notifyAll();
                }
            }
        }
    }
}
