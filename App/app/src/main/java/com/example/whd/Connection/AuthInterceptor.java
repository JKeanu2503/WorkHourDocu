package com.example.whd.Connection;

import androidx.annotation.NonNull;

import com.example.whd.Connection.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Response;

public class AuthInterceptor implements Interceptor {
    @NonNull
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Login nicht mit Token versehen
        if (original.url().encodedPath().contains("login/")) {
            return chain.proceed(original);
        }

        String token = AuthManager.getInstance().getToken();
        Request.Builder builder = original.newBuilder();

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}