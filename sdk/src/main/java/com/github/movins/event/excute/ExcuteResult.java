package com.github.movins.event.excute;

public class ExcuteResult<T> {
    public int code;
    public Throwable exception;
    public T data;

    public ExcuteResult(int code, T data, Throwable exception) {
        this.code = code;
        this.data = data;
        this.exception = exception;
    }

    public ExcuteResult(int code) {
        this.code = code;
    }

    public String toString() {
        return "RequestResult{code=" + this.code + ", exception=" + this.exception + ", data=" + this.data + '}';
    }
}
