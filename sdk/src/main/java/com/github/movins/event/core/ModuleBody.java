package com.github.movins.event.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：事件中心 - 模块执行体
 * Created by movinliaoss
 */
final class ModuleBody {
    private boolean mPaused = false;
    private Map<String, ArrayList<EventBody>> mEvents = new HashMap<>();

    public ModuleBody(boolean paused) {
        this.mPaused = paused;
    }

    public <T extends Object> void exec(String key, BaseEvent<T> evt) {
        if (mPaused || !mEvents.containsKey(key)) {
            return;
        }

        ArrayList<EventBody> list =  mEvents.get(key);
        synchronized (this) {
            list = (ArrayList<EventBody>)list.clone();
        }

        execItems(list, evt);
    }

    public void add(String key, Notify notify, int priority) {
        if (!mEvents.containsKey(key)) {
            mEvents.put(key, new ArrayList<EventBody>());
        }
        ArrayList<EventBody> list =  mEvents.get(key);

        synchronized (this) {
            if (indexOf(list, notify, priority) < 0) {
                list.add(new EventBody(notify, priority));
                Collections.sort(list, new Comparator<EventBody>() {
                    @Override
                    public int compare(EventBody item1, EventBody item2) {
                        return item2.getPriority() - item1.getPriority();
                    }
                });
            }
        }
    }

    public void remove(String key, Notify notify, int priority) {
        if (!mEvents.containsKey(key)) {
            return;
        }
        ArrayList<EventBody> list =  mEvents.get(key);

        synchronized (this) {
            int index;
            do {
                index = indexOf(list, notify, priority);
                if (index >= 0) list.remove(index);
            } while (index >= 0);
        }
    }

    public void clear(String key, Object listener) {
        if (!mEvents.containsKey(key)) {
            return;
        }
        ArrayList<EventBody> list =  mEvents.get(key);

        synchronized (this) {
            int index;
            do {
                index = indexOf(list, listener);
                if (index >= 0) list.remove(index);
            } while (index >= 0);
        }
    }

    public void clear() {
        synchronized (this) {
            mEvents.clear();
        }
    }

    public void setPaused(boolean paused) {
        this.mPaused = paused;
    }

    private <T extends Object> void execItems(ArrayList<EventBody> list, BaseEvent<T> evt) {
        for (EventBody body: list) {
            body.exec(evt);
        }
    }

    private int indexOf(ArrayList<EventBody> list, Notify notify, int priority) {
        int result = -1;
        for (int i = 0; i < list.size(); ++i) {
            EventBody item = list.get(i);
            if((item != null) && item.same(notify, priority)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private int indexOf(ArrayList<EventBody> list, Object listener) {
        int result = -1;
        for (int i = 0; i < list.size(); ++i) {
            EventBody item = list.get(i);
            if((item != null) && item.same(listener)) {
                result = i;
                break;
            }
        }
        return result;
    }
}
