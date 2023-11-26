package com.github.movins.event.core;

/**
 * 功能：事件中心定义
 * Created by movinliao
 */
public final class EventConst {
    public static final int LOW = 0;
    public static final int NORMAL = 1;
    public static final int HIGH = 2;

    public static boolean switched = false;

    public static void clear() {

    }

    public static boolean isSwitched() {
        return switched;
    }

    public static void setSwitched(boolean enabled) {
        switched = enabled;
    }

    public static void log(Object obj, String format, Object... args) {
    }
}
