package com.github.movins.event.utils;

import java.lang.reflect.Method;

/**
 * 功能：方法类函数库
 * Created by movinliao
 */
public final class MethodUtils {
    public static Object apply(Object obj, String name, Object... args) {
        Object result = null;
        if (obj != null) {
            Class cArg[] = {};
            if (args.length > 0) {
                cArg = new Class[args.length];
                for (int i = 0; i < args.length; ++i) {
                    cArg[i] = args[i].getClass();
                }
            }
            try{
                Method work = null;
                Class cls = obj.getClass();
                do {
                    try {
                        work = cls.getDeclaredMethod(name, cArg);
                    } catch(Exception e) {
                    }
                    if (work != null) {
                        break;
                    }
                    cls = cls.getSuperclass();
                } while (cls != null);

                if (work != null) {
                    work.setAccessible(true);
                    result = work.invoke(obj, args);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
