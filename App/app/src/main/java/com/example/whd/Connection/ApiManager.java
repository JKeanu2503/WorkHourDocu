package com.example.whd.Connection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    public static final String API_URL = "https://workhourdocu.duckdns.org/";
    private static ApiManager instance;
    private final com.example.whd.Connection.ApiService apiService;

    private ApiManager() {
        // OkHttpClient, um Header automatisch einzufügen
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(com.example.whd.Connection.ApiService.class);
    }

    public static synchronized ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public com.example.whd.Connection.ApiService getService() {
        return apiService;
    }
}