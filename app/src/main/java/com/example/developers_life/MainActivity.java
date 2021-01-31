package com.example.developers_life;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.developers_life.models.HideProgressBarGlideRequestListener;
import com.example.developers_life.models.ImageAPIService;
import com.example.developers_life.models.ImageResponse;
import com.example.developers_life.models.ImagesAPIRetrofitClient;
import com.example.developers_life.models.NoConnectivityException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static String IMAGES_HISTORY_STATE_KEY = "IMAGES_HISTORY";
    private static String CURRENT_IMAGE_INDEX_STATE_KEY = "CURRENT_IMAGE_INDEX";

    private CardView cardView;
    private ImageView imageView;
    private TextView descriptionTextView;
    private ProgressBar progressBar;
    private TextView noConnectionWarning;
    private TextView noConnectionWarningText;
    private FloatingActionButton backButton;

    private ImageAPIService imageAPIService;

    private List<ImageResponse> imageResponsesHistory = new LinkedList<>();
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

        cardView = findViewById(R.id.cardView);
        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.imageDescription);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.preloader);
        noConnectionWarning = findViewById(R.id.noConnectionWarning);
        noConnectionWarningText = findViewById(R.id.noConnectionWarningText);

        boolean isActivityStateRestored = this.restoreSavedImageHistory(savedInstanceState);

        if (isActivityStateRestored) {
            this.showImageAndDescription(this.imageResponsesHistoryIterator.next());
        } else {
            getNextImage(null);
        }

        this.updateBackButtonEnabledState();

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
        toggleConnectionLostWarning(true);
        updateBackButtonEnabledState();
    }

    public void getAndStoreRandomImage() {

        progressBar.setVisibility(View.VISIBLE);

        imageAPIService.getRandomImage().enqueue(new Callback<ImageResponse>() {

            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                if (!response.isSuccessful())
                    return;

                toggleConnectionLostWarning(true);

                ImageResponse image = response.body();
                imageResponsesHistoryIterator.add(image);

                showImageAndDescription(image);
                updateBackButtonEnabledState();

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

                if (t instanceof NoConnectivityException) {
                    toggleConnectionLostWarning(false);
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

    private void toggleConnectionLostWarning(boolean isWarningVisible) {

        progressBar.setVisibility(View.INVISIBLE);
        cardView.setVisibility(isWarningVisible ? View.VISIBLE : View.INVISIBLE);

        noConnectionWarning.setVisibility(isWarningVisible ? View.INVISIBLE : View.VISIBLE);
        noConnectionWarningText.setVisibility(isWarningVisible ? View.INVISIBLE : View.VISIBLE);

    }

    private boolean updateBackButtonEnabledState() {

        boolean isBackButtonEnabled =
                imageResponsesHistory.size() > 1 &&
                        imageResponsesHistoryIterator.previousIndex() != 0;
        backButton.setEnabled(isBackButtonEnabled);
        return isBackButtonEnabled;

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putSerializable(
                MainActivity.IMAGES_HISTORY_STATE_KEY,
                (LinkedList<ImageResponse>) imageResponsesHistory
        );

        outState.putInt(
                MainActivity.CURRENT_IMAGE_INDEX_STATE_KEY,
                imageResponsesHistoryIterator.previousIndex()
        );

        super.onSaveInstanceState(outState);

    }

    private boolean restoreSavedImageHistory(Bundle savedInstanceState) {

        if (savedInstanceState == null ||
                !savedInstanceState.containsKey(MainActivity.IMAGES_HISTORY_STATE_KEY) &&
                        !savedInstanceState.containsKey(MainActivity.CURRENT_IMAGE_INDEX_STATE_KEY)
        ) {
            return false;
        }

        List<ImageResponse> savedImagesHistory = (LinkedList<ImageResponse>)
                savedInstanceState.getSerializable(MainActivity.IMAGES_HISTORY_STATE_KEY);

        boolean isSavedImagesHistoryExists = savedImagesHistory != null && savedImagesHistory.size() > 0;

        if (isSavedImagesHistoryExists) {
            imageResponsesHistory = savedImagesHistory;
            int currentImageIndex = savedInstanceState.getInt(MainActivity.CURRENT_IMAGE_INDEX_STATE_KEY);
            imageResponsesHistoryIterator = savedImagesHistory.listIterator(currentImageIndex);
        }

        return isSavedImagesHistoryExists;

    }

}