package com.github.movins.event.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 功能：数据基类
 * Created by movinliao.
 */

public class Serializedata<T> implements Dataable<T>, Serializable{
    private static final long serialVersionUID = 3095437695863577149L;
    private boolean empty = true;
    private int ___updateId = 0;

    @Override
    public boolean equal(T obj) {
        Class cls1 = this.getClass();
        Field [] fields1 = cls1.getDeclaredFields();

        Class cls2 = obj.getClass();
        Field [] fields2 = cls2.getDeclaredFields();

        if (fields1.length != fields2.length) {
            return false;
        }

        for (int i = 0; i < fields1.length; ++i) {
            Field field1 = fields1[i];
            Field field2 = fields2[i];
            if (!field1.equals(field2)) {
                return false;
            }
            if(ignoreField(field1.getName())) {
                continue;
            }

            try {
                field1.setAccessible(true);
                Object val1 = field1.get(this);
                field2.setAccessible(true);
                Object val2 = field2.get(obj);
                if (val1 == null && val2 == null) {
                    continue;
                }
                if (val1 == null && val2 != null) {
                    return false;
                }

                if (!val1.equals(val2)) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean assign(T obj) {
        boolean result = false;
        try {
            Class cls = this.getClass();
            Field [] fields = cls.getDeclaredFields();
            for (Field field: fields) {
                String name = field.getName();
                if(ignoreField(name)) {
                    continue;
                }
                field.setAccessible(true);
                Object val1 = field.get(this);
                Object val2 = field.get(obj);
                if (val2 == null || val1 == val2) {
                    continue;
                }
                if (val1 != null && val1.equals(val2)) {
                    continue;
                }
                if (val1 != null && val1 instanceof Serializedata) {
                    result = ((Serializedata)val1).assign(val2) || result;
                    continue;
                }
                result = true;
                field.set(this, val2);
            }
            if (result) {
                needUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean clear() {
        boolean result = false;
        try {
            Class cls = this.getClass();
            T value = (T)cls.newInstance();
            result = assign(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public T clone () {
        T result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected boolean ignoreField (String fieldName) {
        String [] fields = {"serialVersionUID", "___updateId"};
        return Arrays.asList(fields).contains(fieldName);
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }

    @Override
    public int getUpdateId() {
        return ___updateId;
    }

    @Override
    public void needUpdate() {
        ++___updateId;
    }

    @Override
    public boolean getEmpty() {
        return empty;
    }
    @Override
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
