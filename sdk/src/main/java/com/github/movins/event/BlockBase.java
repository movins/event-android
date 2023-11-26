package com.github.movins.event;

import com.github.movins.event.core.Dispatcher;
import com.github.movins.event.utils.MethodUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：功能块基类
 * Created by movinliao
 */
public class BlockBase extends Dispatcher implements IBlock {
    private Object mModule;
    private boolean mStoped;
    private Map<String, Method> mHandles= new HashMap<>();

    @Override
    public void init(Object module) {
        this.mModule = module;
        initMethods();
    }

    @Override
    public void destroy() {
        clear();
        mHandles.clear();
        mHandles = null;
        mModule = null;
    }

    @Override
    public void start() {
        mStoped = false;
        setPaused(false);
    }

    @Override
    public void stop() {
        setPaused(true);
        mStoped = true;
    }

    @Override
    public void back() {

    }

    @Override
    public void fore() {

    }

    @Override
    public boolean isStoped() {
        return mStoped;
    }

    @Override
    public Object getModule() {
        return mModule;
    }

    @Override
    public void log(Object tag, String format, Object... args) {
        MethodUtils.apply(mModule, "log", tag, format, args);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object result = null;

        try {
            result = method.invoke(this, args);
        } catch (Exception e) {
        }

        return result;
    }

    @Override
    public Object call(String cmd, Object[] args) {
        Method method = mHandles.get(cmd);
        Object result = null;
        if (method != null) {
            result = invoke(null, method, args);
        }
        return result;
    }

    private void initMethods() {
        mHandles.clear();
        Class cls = this.getClass();
        for (Method method : cls.getDeclaredMethods()) {
            Command an = method.getAnnotation(Command.class);
            if (an == null) continue;
            String cmd = an.value();

            mHandles.put(cmd, method);
        }

    }
}
