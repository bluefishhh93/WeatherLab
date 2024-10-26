package com.example.weatherlab.utils;


import android.content.Context;
import android.media.MediaPlayer;
import com.example.weatherlab.R;

public class AudioManager {
    private MediaPlayer mediaPlayer;
    private final Context context;

    public AudioManager(Context context) {
        this.context = context;
    }

    public void playWeatherSound(String weatherCondition) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        int soundResId = getSoundResourceId(weatherCondition);
        mediaPlayer = MediaPlayer.create(context, soundResId);

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private int getSoundResourceId(String weatherCondition) {
        switch (weatherCondition.toLowerCase()) {
            case "rain":
            case "drizzle":
                return R.raw.rain_sound;
            case "thunderstorm":
                return R.raw.thunder_sound;
            case "clear":
                return R.raw.clear_sound;
            case "clouds":
                return R.raw.cloud_sound;
            default:
                return R.raw.default_sound;
        }
    }

    public void playDefaultSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.default_sound);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}