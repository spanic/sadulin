package com.example.developers_life.models;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class HideProgressBarGlideRequestListener implements RequestListener<Drawable> {

    private ProgressBar progressBar;

    public HideProgressBarGlideRequestListener(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public boolean onLoadFailed(
            @Nullable GlideException e,
            Object model,
            Target target,
            boolean isFirstResource) {
        progressBar.setVisibility(View.INVISIBLE);
        return false;
    }

    @Override
    public boolean onResourceReady(
            Drawable resource,
            Object model,
            Target<Drawable> target,
            DataSource dataSource,
            boolean isFirstResource) {
        progressBar.setVisibility(View.INVISIBLE);
        return false;
    }

}
