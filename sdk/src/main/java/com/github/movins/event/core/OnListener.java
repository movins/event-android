package com.github.movins.event.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by movinliao
 * 邮箱：
 * 说明：
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnListener {
    String key();
    int priority() default 1;
    boolean sync() default false;
}
