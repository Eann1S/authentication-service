package com.example.authentication.json;

import com.google.gson.Gson;

public class JsonConverter {

    private static final Gson gson = new Gson();

    public static <T> String toJson(T src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
