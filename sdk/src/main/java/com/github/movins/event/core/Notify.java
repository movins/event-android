package com.github.movins.event.core;

import android.os.Handler;
import android.os.Looper;

import com.github.movins.event.utils.MethodUtils;

import java.lang.ref.WeakReference;


/**
 * 功能：通知体
 * Created by movinliao
 */
public final class Notify {
    private static long mainThreadId = Looper.getMainLooper().getThread().getId();
    private static Handler handler = new Handler(Looper.getMainLooper());

    private String mMethod;
    private WeakReference<Object> mListener;
    // private Object mListener;
    private boolean mSync;

    public Notify(Object listener, String method) {
        this.mListener = new WeakReference<>(listener);
        this.mMethod = method;
        this.mSync = false;
    }

    public Notify(Object listener, String method, boolean async) {
        this.mListener = new WeakReference<>(listener);
        this.mMethod = method;
        this.mSync = async;
    }

    public Object invoke(final Object... args) {
        Object result = null;
        if (!mSync || isMainThread()) {
            result = doInvoke(args);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    doInvoke(args);
                }
            });
        }

        return result;
    }

    public boolean equals(Notify obj) {
        Object listener = (Object)this.mListener.get();
        Object other = (Object)obj.mListener.get();
        return (obj != null) && (listener == other) && this.mMethod.equals(obj.mMethod);
    }

    public boolean equals(Object listener) {
        Object target = (Object)this.mListener.get();
        return (target == this.mListener);
    }

    public boolean isNull() {
        Object listener = (Object)this.mListener.get();
        return (listener == null);
    }

    private Object doInvoke(Object... args) {
        Object result = null;
        Object listener = (Object)this.mListener.get();
        if (listener != null && !mMethod.isEmpty()) {
            result = MethodUtils.apply(listener, mMethod, args);
        }

        return result;
    }

    private boolean isMainThread() {
        return Thread.currentThread().getId() == mainThreadId;
    }
}
