package com.github.movins.event.data;

public interface Dataable<T> {
    public boolean equal(T obj);
    public boolean assign(T obj);
    public T clone();
    public boolean clear();
    public boolean isEmpty();
    public int getUpdateId();
    public void needUpdate();
    public boolean getEmpty();
    public void setEmpty(boolean empty);
}
