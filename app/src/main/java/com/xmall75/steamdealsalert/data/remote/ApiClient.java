package com.xmall75.steamdealsalert.data.remote;

import com.xmall75.steamdealsalert.data.remote.CheapSharkApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static volatile OkHttpClient httpInstance;
    private static volatile CheapSharkApi apiInstance;

    private ApiClient() {}

    public static OkHttpClient http() {
        if (httpInstance == null) {
            synchronized (ApiClient.class) {
                if (httpInstance == null) {
                    httpInstance = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(chain -> {
                                Request original = chain.request();
                                Request requestWithUserAgent = original.newBuilder()
                                        .header("User-Agent", "SteamDealsAlert-AndroidApp/1.0")
                                        .build();
                                return chain.proceed(requestWithUserAgent);
                            })
                            .build();
                }
            }
        }
        return httpInstance;
    }

    public static CheapSharkApi get() {
        if (apiInstance == null) {
            synchronized (ApiClient.class) {
                if (apiInstance == null) {
                    apiInstance = new Retrofit.Builder()
                            .baseUrl(CheapSharkApi.BASE_URL)
                            .client(http())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(CheapSharkApi.class);
                }
            }
        }
        return apiInstance;
    }
}