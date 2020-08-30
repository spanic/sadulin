package com.example.developers_life.models;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ImageAPIService {

    @GET("random?json=true")
    Call<ImageResponse> getRandomImage();

}
