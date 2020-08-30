package com.example.developers_life.models;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImagesAPIRetrofitClient {

    private static volatile Retrofit RETROFIT_CLIENT;

    public static Retrofit getRetrofitClient(Context context) {

        Retrofit client = RETROFIT_CLIENT;
        if (client != null)
            return client;

        synchronized (ImagesAPIRetrofitClient.class) {

            if (RETROFIT_CLIENT == null) {

                OkHttpClient clientWithNetworkConnectionInterceptor = new OkHttpClient.Builder()
                        .addInterceptor(new NetworkConnectionInterceptor(context))
                        .build();

                RETROFIT_CLIENT = new Retrofit.Builder()
                        .baseUrl("https://developerslife.ru/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(clientWithNetworkConnectionInterceptor)
                        .build();

            }

            return RETROFIT_CLIENT;

        }

    }

}
