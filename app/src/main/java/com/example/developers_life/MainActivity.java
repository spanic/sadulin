package com.example.developers_life;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.developers_life.models.ImageAPIService;
import com.example.developers_life.models.ImageResponse;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;

    private ImageAPIService imageAPIService;

    private LinkedList<ImageResponse> imageResponsesHistory = new LinkedList<>();
    private ListIterator<ImageResponse> imageResponsesHistoryIterator =
            imageResponsesHistory.listIterator();

    public MainActivity() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://developerslife.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        imageAPIService = retrofit.create(ImageAPIService.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.image_description);

        getNextImage(null);

    }

    public void getNextImage(View view) {

        if (imageResponsesHistoryIterator.hasNext()) {
            showImageAndDescription(imageResponsesHistoryIterator.next());
        } else {
            getAndStoreRandomImage();
        }

    }

    public void getPreviousImageFromCache(View view) {

        Optional<ImageResponse> previousImageResponse = getPreviousImageResponseFromHistory();
        if (!previousImageResponse.isPresent())
            return;

        showImageAndDescription(previousImageResponse.get());

    }

    public void getAndStoreRandomImage() {

        imageAPIService.getRandomImage().enqueue(new Callback<ImageResponse>() {

            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                if (!response.isSuccessful())
                    return;

                ImageResponse image = response.body();

                imageResponsesHistoryIterator.add(image);
                showImageAndDescription(image);

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {}

        });

    }

    private void showImageAndDescription(ImageResponse sourceImageResponse) {

        if (!"gif".equals(sourceImageResponse.getType()))
            return;

        descriptionTextView.setText(sourceImageResponse.getDescription());

        Glide.with(MainActivity.this)
                .load(sourceImageResponse.getGifURL())
                .into(imageView);

    }

    private Optional<ImageResponse> getPreviousImageResponseFromHistory() {

        if (imageResponsesHistory.size() <= 1
                || imageResponsesHistoryIterator.previousIndex() == 0
                || !imageResponsesHistoryIterator.hasPrevious())
            return Optional.empty();

        imageResponsesHistoryIterator.previous();
        return Optional.ofNullable(
                imageResponsesHistory.get(imageResponsesHistoryIterator.previousIndex())
        );

    }

}