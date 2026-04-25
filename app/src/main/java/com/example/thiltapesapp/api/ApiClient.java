package com.example.thiltapesapp.api;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // A URL da sua API (Obrigatório terminar com /)
    private static final String BASE_URL = "http://177.44.248.26:8000/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Se você já criou o AuthInterceptor (que conversamos antes),
            // você pode adicioná-lo aqui no OkHttpClient para enviar o token automaticamente.
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    // .addInterceptor(new AuthInterceptor(context)) // Descomente quando configurar o Interceptor
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()) // Converte JSON <-> Objeto Java
                    .build();
        }
        return retrofit;
    }
}