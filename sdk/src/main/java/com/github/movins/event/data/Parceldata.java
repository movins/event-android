package com.github.movins.event.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;

public class Parceldata<T extends Parceldata> implements Dataable<T>, Parcelable, Parcelable.Creator<T> {
    public static final Creator<Parceldata> CREATOR = new Parceldata();

    private boolean empty = true;
    private int ___updateId = 0;

    public Parceldata() {
    }

    protected Parceldata(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected void readFromParcel(Parcel parcel) {
        ___updateId = parcel.readInt();
        empty = parcel.readByte() == 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(___updateId);
        dest.writeByte((byte) (!empty ? 1 : 0));
    }

    @Override
    public T createFromParcel(Parcel parcel) {
        return null;
    }

    @Override
    public T[] newArray(int i) {
        return null;
    }

    public boolean equal(T obj) {
        boolean result = false;
        if (obj == null) {
            return result;
        }

        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.setDataPosition(0);
            obj.writeToParcel(parcel, 0);
            String hash1 = new String(parcel.marshall(), "UTF-8");

            parcel.setDataPosition(0);
            writeToParcel(parcel, 0);
            String hash2 = new String(parcel.marshall(), "UTF-8");

            result = hash1.equals(hash2);
        } catch (UnsupportedEncodingException e) {
            result = false;
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
        return result;
    }

    public boolean assign(T obj) {
        boolean result = false;
        if (obj == null) {
            return result;
        }

        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            obj.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);

            readFromParcel(parcel);
            result = true;
        } catch(Exception e) {
            result = false;
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
        return result;
    }

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

    public T clone () {
        T result = null;
        try {
            Class cls = this.getClass();
            result = (T)cls.newInstance();
            result.assign(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public byte[] marshall() {
        byte[] result = new byte[0];
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.setDataPosition(0);
            writeToParcel(parcel, 0);
            result = parcel.marshall();
        } catch(Exception e) {
            result = new byte[0];
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
        return result;
    }

    public void unmarshall(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return;
        }

        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);
            readFromParcel(parcel);
        } catch(Exception e) {
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
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
