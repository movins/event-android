package com.github.movins.event.excute;

public interface ExcuteInvoke<T> {
    boolean onExcute(InvokeCallback<T> result);
}
