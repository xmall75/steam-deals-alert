package com.xmall75.steamdealsalert.data.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static volatile CheapSharkApi instance;

    private ApiClient() {}

    public static CheapSharkApi get() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    OkHttpClient http = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                    instance = new Retrofit.Builder()
                            .baseUrl(CheapSharkApi.BASE_URL)
                            .client(http)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(CheapSharkApi.class);
                }
            }
        }
        return instance;
    }
}
