package com.example.weatherlab.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

public class BindingAdapers {
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String url) {
        Picasso.get().load(url).into(view);
    }
}
