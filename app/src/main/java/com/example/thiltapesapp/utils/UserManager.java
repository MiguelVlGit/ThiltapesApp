package com.example.thiltapesapp.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    private static final String PREF_NAME = "ThiltapesPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_LOGIN = "user_login";

    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Salvar dados do usuário
    public void saveUser(int id, String login) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USER_LOGIN, login)
                .apply();
    }

    // Recuperar ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Recuperar login
    public String getUserLogin() {
        return sharedPreferences.getString(KEY_USER_LOGIN, null);
    }

    // Limpar (logout)
    public void clearUser() {
        sharedPreferences.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USER_LOGIN)
                .apply();
    }
}