package com.example.thiltapesapp.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class ModelUtils {
    public static Map<String, String> toMap(Object obj) {
        Gson gson = new Gson();
        // Converte o objeto para JSON e depois para um Map
        String json = gson.toJson(obj);
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
