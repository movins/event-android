package com.github.movins.event;

import com.github.movins.event.core.Dispatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 功能：模块定义框架
 * Created by movinliao
 */
public class ModuleBase extends Dispatcher implements IModule {
    private Object mApi = null;
    private boolean mInited = false;
    private Map<String, IBlock> mBlocks = new HashMap<>();

    @Override
    public void doEvent(String key, Object data, int priority) {
        ModuleEvent evt = new ModuleEvent(key, data, priority);
        dispatch(key, evt);
    }

    @Override
    public void init(Object api) {
        this.mApi = api;
        Iterator itr = mBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            IBlock item = (IBlock)entry.getValue();
            if (item != null) item.init(this);
        }

        doEvent(ModuleEvent.kInit, null, 0);
    }

    @Override
    public void destroy() {
        doEvent(ModuleEvent.kDestroy, null, 0);

        Iterator itr = mBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            IBlock item = (IBlock)entry.getValue();
            if (item != null) item.destroy();
        }
        mBlocks.clear();
        mApi = null;
        mBlocks = null;
    }

    @Override
    public void start() {
        setPaused(false);

        Iterator itr = mBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            IBlock item = (IBlock)entry.getValue();
            if (item != null) item.start();
        }

        doEvent(ModuleEvent.kStart, null, 0);
    }

    @Override
    public void stop() {
        doEvent(ModuleEvent.kStop, null, 0);

        setPaused(true);

        Iterator itr = mBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            IBlock item = (IBlock)entry.getValue();
            if (item != null) item.stop();
        }
    }

    @Override
    public void back() {
        doEvent(ModuleEvent.kBack, null, 0);

        Iterator itr = mBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            IBlock item = (IBlock)entry.getValue();
            if (item != null) item.back();
        }
    }

    @Override
    public void fore() {
        doEvent(ModuleEvent.kFore, null, 0);

        Iterator itr = mBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            IBlock item = (IBlock)entry.getValue();
            if (item != null) item.fore();
        }
    }

    @Override
    public void addBlock(String key, IBlock block) {
        if((block == null) || mBlocks.containsKey(key)) {
            return;
        }

        if (mInited) {
            block.init(this);
        }

        mBlocks.put(key, block);

        doEvent(ModuleEvent.kAdd, key, 0);
    }

    @Override
    public void removeBlock(String key) {
        if (!mBlocks.containsKey(key)) {
            return;
        }

        doEvent(ModuleEvent.kRemove, key, 0);

        IBlock block = mBlocks.remove(key);
        block.destroy();
    }

    public IBlock block(String key) {
        return mBlocks.get(key);
    }

    @Override
    public <T> T block(Class<T> cls) {
        String key = cls.getCanonicalName();
        IBlock block = mBlocks.get(key);
        if (block == null) block = new BlockBase();
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, block);
    }

    @Override
    public boolean hasBlock(String key) {
        return mBlocks.containsKey(key);
    }

    @Override
    public boolean isInited() {
        return mInited;
    }

    @Override
    public Object getApi() {
        return mApi;
    }

    @Override
    public Set<String> keys() {
        return mBlocks.keySet();
    }

    public void addBlockListener(String blockKey, String key, Object listener, String method, int priority) {
        IBlock block = mBlocks.get(blockKey);
        if (block != null) {
            block.addListener(key, listener, method, priority);
        }
    }

    public void removeBlockListener(String blockKey, String key, Object listener, String method, int priority) {
        IBlock block = mBlocks.get(blockKey);
        if (block != null) {
            block.removeListener(key, listener, method, priority);
        }
    }

    @Override
    public void log(Object tag, String format, Object... args) {
    }

    @Override
    public void addEvents(Object target, Class keyMap) {
        Class targetCls = target.getClass();
        for (Method method : targetCls.getDeclaredMethods()) {
            OnBlock an = method.getAnnotation(OnBlock.class);
            if (an == null) continue;
            String blockKey = an.value();
            IBlock block = mBlocks.get(blockKey);
            if (block != null) {
                block.addEvents(target, keyMap);
            }
        }
        super.addEvents(target, keyMap);
    }

    @Override
    public void removeEvents(Object target, Class keyMap) {
        Class targetCls = target.getClass();
        for (Method method : targetCls.getDeclaredMethods()) {
            OnBlock an = method.getAnnotation(OnBlock.class);
            if (an == null) continue;
            String blockKey = an.value();
            IBlock block = mBlocks.get(blockKey);
            if (block != null) {
                block.removeEvents(target, keyMap);
            }
        }

        super.removeEvents(target, keyMap);
    }

    @Override
    public Object call(Class cls, String cmd, Object... args) {
        String key = cls.getCanonicalName();
        IBlock block = mBlocks.get(key);
        Object result = null;
        if (block != null) {
            result = block.call(cmd, args);
        }
        return result;
    }
}
