package com.github.movins.event.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能：事件中心
 * Created by movinliao
 */
final class EventCenter {
    private static final Map<String, ModuleBody> modules = new HashMap<>();

    public static void addListener(String key, String subKey, Notify notify, int priority) {
        if (notify == null) {
            return;
        }

        if (!modules.containsKey(key)) {
            synchronized (EventCenter.class) {
                modules.put(key, new ModuleBody(EventConst.isSwitched()));
            }
        }

        ModuleBody module = modules.get(key);
        module.add(subKey, notify, priority);

        EventConst.log("EventCenter", "Listener add: key=%s, subKey=%s", key, subKey);
    }

    public static void removeListener(String key, String subKey, Notify notify, int priority) {
        if (!modules.containsKey(key)) {
            return;
        }

        ModuleBody module = modules.get(key);
        module.remove(subKey, notify, priority);

        EventConst.log("EventCenter", "Listener remove: key=%s, subKey=%s", key, subKey);
    }

    public static <T extends Object> void dispatch(String key, String subKey, BaseEvent<T> evt) {
        if (EventConst.isSwitched() || !modules.containsKey(key)) {
            return;
        }

        ModuleBody module = modules.get(key);
        module.exec(subKey, evt);

        EventConst.log("EventCenter", "Event dispatch: key=%s, subKey=%s", key, subKey);
    }

    public static void setPause(String key, boolean enabled) {
        if (!modules.containsKey(key)) {
            return;
        }

        ModuleBody module = modules.get(key);
        module.setPaused(enabled);

        EventConst.log("EventCenter", "setPause: key=%s, enabled=%s", key, enabled);
    }

    public static void clear() {
        synchronized (EventCenter.class) {
            modules.clear();
        }

        EventConst.log("EventCenter", "clearAll");
    }

    public static void clear(String key) {
        if (!modules.containsKey(key)) {
            return;
        }

        ModuleBody module = modules.get(key);
        module.clear();

        EventConst.log("EventCenter", "clear: key=%s", key);
    }

    public static void clear(String key, String subKey, Object listener) {
        if (!modules.containsKey(key)) {
            return;
        }

        ModuleBody module = modules.get(key);
        module.clear(subKey, listener);

        EventConst.log("EventCenter", "Event dispatch: key=%s, subKey=%s, listener=%s", key, subKey, listener);
    }
}
