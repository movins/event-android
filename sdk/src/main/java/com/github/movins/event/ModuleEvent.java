package com.github.movins.event;

import com.github.movins.event.core.BaseEvent;
import com.github.movins.event.core.EventKey;

/**
 * 功能：模块事件定义
 * Created by movinliao.
 */
public class ModuleEvent extends BaseEvent {
    public int priority = 0;

    @EventKey("kLog")
    public static final String kLog = "ModuleEvent.kLog";
    @EventKey("kInit")
    public static final String kInit = "ModuleEvent.kInit";
    @EventKey("kDestroy")
    public static final String kDestroy = "ModuleEvent.kDestroy";
    @EventKey("kStart")
    public static final String kStart = "ModuleEvent.kStart";
    @EventKey("kStop")
    public static final String kStop = "ModuleEvent.kStop";
    @EventKey("kAdd")
    public static final String kAdd = "ModuleEvent.kAdd";
    @EventKey("kRemove")
    public static final String kRemove = "ModuleEvent.kRemove";
    @EventKey("进入后台")
    public static final String kBack = "ModuleEvent.kBack";
    @EventKey("进入前台")
    public static final String kFore = "ModuleEvent.kFore";

    public ModuleEvent(String key, Object data) {
        super(key, data);
    }

    public ModuleEvent(String key, Object data, int priority) {
        super(key, data);
        this.priority = priority;
    }
}
