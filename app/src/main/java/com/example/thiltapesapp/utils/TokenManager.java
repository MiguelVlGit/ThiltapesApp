package com.example.thiltapesapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "ThiltapesPrefs";
    private static final String KEY_TOKEN = "access_token";
    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        // "Context" é necessário para acessar o armazenamento do Android
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Salvar o token após o login bem-sucedido
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    // Recuperar o token para futuras chamadas de API
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Remover o token (Logout)
    public void clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }
}
