package com.example.developers_life;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.developers_life.models.HideProgressBarGlideRequestListener;
import com.example.developers_life.models.ImageAPIService;
import com.example.developers_life.models.ImageResponse;
import com.example.developers_life.models.ImagesAPIRetrofitClient;
import com.example.developers_life.models.NoConnectivityException;

import java.util.LinkedList;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;
    private Button backButton;
    private ProgressBar progressBar;
    private TextView noConnectionWarning;
    private TextView noConnectionWarningText;

    private ImageAPIService imageAPIService;

    private LinkedList<ImageResponse> imageResponsesHistory = new LinkedList<>();
    private ListIterator<ImageResponse> imageResponsesHistoryIterator =
            imageResponsesHistory.listIterator();

    public MainActivity() {

        Retrofit retrofit = ImagesAPIRetrofitClient.getRetrofitClient(this);
        imageAPIService = retrofit.create(ImageAPIService.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.imageDescription);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.preloader);
        noConnectionWarning = findViewById(R.id.noConnectionWarning);
        noConnectionWarningText = findViewById(R.id.noConnectionWarningText);

        getNextImage(null);

        backButton.setEnabled(false);

    }

    public void getNextImage(View view) {

        if (imageResponsesHistoryIterator.hasNext()) {
            showImageAndDescription(imageResponsesHistoryIterator.next());
            updateBackButtonEnabledState();
        } else {
            getAndStoreRandomImage();
        }

    }

    public void getPreviousImageFromCache(View view) {
        showImageAndDescription(getPreviousImageResponseFromHistory());
        updateBackButtonEnabledState();
    }

    public void getAndStoreRandomImage() {

        progressBar.setVisibility(View.VISIBLE);

        imageAPIService.getRandomImage().enqueue(new Callback<ImageResponse>() {

            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                if (!response.isSuccessful())
                    return;

                toggleViewsOnConnectionStatusChange(true);

                ImageResponse image = response.body();
                imageResponsesHistoryIterator.add(image);

                showImageAndDescription(image);
                updateBackButtonEnabledState();

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

                if (t instanceof NoConnectivityException) {
                    toggleViewsOnConnectionStatusChange(false);
                }

            }

        });

    }

    private void showImageAndDescription(ImageResponse sourceImageResponse) {

        if (!"gif".equals(sourceImageResponse.getType()))
            return;

        descriptionTextView.setText(sourceImageResponse.getDescription());

        Glide.with(MainActivity.this)
                .load(sourceImageResponse.getGifURL())
                .listener(new HideProgressBarGlideRequestListener(progressBar))
                .into(imageView);

    }

    private ImageResponse getPreviousImageResponseFromHistory() {

        imageResponsesHistoryIterator.previous();
        return imageResponsesHistory.get(imageResponsesHistoryIterator.previousIndex());

    }

    private void toggleViewsOnConnectionStatusChange(boolean isConnectionAvailable) {

        imageView.setVisibility(isConnectionAvailable ? View.VISIBLE : View.INVISIBLE);
        progressBar.setVisibility(isConnectionAvailable ? View.VISIBLE : View.INVISIBLE);

        noConnectionWarning.setVisibility(isConnectionAvailable ? View.INVISIBLE : View.VISIBLE);
        noConnectionWarningText.setVisibility(isConnectionAvailable ? View.INVISIBLE : View.VISIBLE);

    }

    private boolean updateBackButtonEnabledState() {

        boolean isBackButtonEnabled = imageResponsesHistory.size() > 1 &&
                imageResponsesHistoryIterator.previousIndex() != 0;
        backButton.setEnabled(isBackButtonEnabled);
        return isBackButtonEnabled;

    }

}