package com.example.developers_life;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.developers_life.models.ImageAPIService;
import com.example.developers_life.models.ImageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://developerslife.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ImageAPIService imageAPIService = retrofit.create(ImageAPIService.class);

        imageAPIService.getRandomImage().enqueue(new Callback<ImageResponse>() {

            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                if (!response.isSuccessful())
                    return;

                ImageResponse imageResponseBody = response.body();

                if (!"gif".equals(imageResponseBody.getType()))
                    return;

                String imageURL = imageResponseBody.getGifURL();
                Glide.with(MainActivity.this)
                        .load(imageURL)
                        .into(imageView);

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }

        });

    }

}