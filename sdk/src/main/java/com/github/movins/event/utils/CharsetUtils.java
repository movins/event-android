package com.github.movins.event.utils;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CharsetUtils {
    public static final String CHARSET = "UTF-8";
    public static final CharsetDecoder decoder = Charset.forName(CHARSET).newDecoder();

    public CharsetUtils() {
    }

    public static <T> String parse(T data) {
        if (data == null) {
            return "";
        }
        try {
            return (new Gson()).toJson(data);
        } catch (Exception err) {
            return "";
        }
    }

    public static String getString(byte[] b, int len) {
        try {
            return new String(b, 0, len, CHARSET);
        } catch (UnsupportedEncodingException var3) {
            return new String(b, 0, len);
        }
    }

    public static String getString(byte[] b, int start, int len) {
        try {
            return new String(b, start, len, CHARSET);
        } catch (UnsupportedEncodingException var4) {
            return new String(b, start, len);
        }
    }

    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET);
            } catch (UnsupportedEncodingException var2) {
                return str.getBytes();
            }
        } else {
            return new byte[0];
        }
    }

    public static <T> T unmarshal(String json, Class<T> clazz) {
        try {
            return (new Gson()).fromJson(json, clazz);
        } catch (Exception err) {
            return null;
        }
    }
    public static <T> T unmarshal(String json, Type typeOfT) {
        try {
            return (new Gson()).fromJson(json, typeOfT);
        } catch (Exception err) {
            return null;
        }
    }

    public static <T> byte[] marshal(T data) {
        return data != null ? getBytes(parse(data)) : null;
    }

    public static <T> T unmarshal(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            return null;
        }
        return unmarshal(getString(bytes, bytes.length), clazz);
    }

    public static <T> T unmarshal(byte[] bytes, Type typeOfT) {
        if (bytes == null) {
            return null;
        }
        return unmarshal(getString(bytes, bytes.length), typeOfT);
    }

    public static String unmarshal(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        return getString(bytes, bytes.length);
    }

    /**
     * 压缩
     */
    public static byte[] gzip(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(bytes);
            gzipOutputStream.flush();
            gzipOutputStream.finish();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] ungzip(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)){
            byte[] buffer = new byte[256];
            int len;
            while ((len = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
        }catch (Exception e){
            System.out.println(e);
            return null;
        }

        return byteArrayOutputStream.toByteArray();
    }
}
