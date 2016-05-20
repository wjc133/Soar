package com.elite.tools.soar.toolbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by wjc133
 * DATE: 16/5/20
 * TIME: 下午3:35
 */
public class JsonUtils {
    private static final Gson GSON = new GsonBuilder().create();

    public static <T> String toJson(T obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clz) {
        return GSON.fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }
}
