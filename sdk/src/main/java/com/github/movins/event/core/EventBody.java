package com.github.movins.event.core;

import android.util.SparseArray;

/**
 * 功能：事件中心 - 事件执行体
 * Created by movinliao
 */

final class EventBody {
//    private static final Map<Integer, Notify> handers = new HashMap<>();
    private static final SparseArray<Notify> handers = new SparseArray<>();
    private static int baseKey = 0;

    private int mBodyKey;
    private int mPriority;

    public static void clear() {
        synchronized (EventBody.class) {
            handers.clear();
        }
    }

    public EventBody(Notify notify, int priority) {
        synchronized (EventBody.class) {
            mBodyKey = ++baseKey;
            handers.put(mBodyKey, notify);
        }

        this.mPriority = priority;
    }

    protected void finalize() throws java.lang.Throwable {
        synchronized (EventBody.class) {
            handers.remove(mBodyKey);
        }

        super.finalize();
    }

    public Notify getNotify() {
        return handers.get(mBodyKey);
    }

    public boolean same(Notify notify, int priority) {
        Notify data = handers.get(mBodyKey);

        return (data != null) && data.equals(notify) && (this.mPriority == priority);
    }

    public boolean same(Object listener) {
        Notify data = handers.get(mBodyKey);

        return (data != null) && data.equals(listener);
    }

    public <T extends Object> void exec(BaseEvent<T> evt) {
        Notify data = handers.get(mBodyKey);
        if (data.isNull()) return;
        data.invoke(evt);
    }

    public int getPriority() {
        return mPriority;
    }
}
