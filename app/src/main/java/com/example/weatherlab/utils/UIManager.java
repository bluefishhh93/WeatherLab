package com.example.weatherlab.utils;

import android.app.Activity;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.weatherlab.R;
import com.example.weatherlab.model.WeatherResponse;
import com.google.android.material.imageview.ShapeableImageView;

public class UIManager {
    private final Activity activity;
    private final TextView tvCity;
    private final TextView tvTemperature;
    private final TextView tvDescription;
    private final ImageView ivWeatherIcon;
    private final ImageView ivBackground;
    private final LinearLayout llAdditionalInfo;
    private final TextView name;
    private final TextView mail;
    private final ShapeableImageView profileImage;
    private final EditText etCity;
    private final Button btnGetWeather;

    public UIManager(Activity activity) {
        this.activity = activity;
        tvCity = activity.findViewById(R.id.tvCity);
        tvTemperature = activity.findViewById(R.id.tvTemperature);
        tvDescription = activity.findViewById(R.id.tvDescription);
        ivWeatherIcon = activity.findViewById(R.id.ivWeatherIcon);
        ivBackground = activity.findViewById(R.id.ivBackground);
        llAdditionalInfo = activity.findViewById(R.id.llAdditionalInfo);
        name = activity.findViewById(R.id.nameTV);
        mail = activity.findViewById(R.id.mailTV);
        profileImage = activity.findViewById(R.id.profileImage);
        etCity = activity.findViewById(R.id.etCity);
        btnGetWeather = activity.findViewById(R.id.btnGetWeather);

        setDefaultBackground();
    }

    public void setDefaultBackground() {
        Glide.with(activity)
                .load(R.drawable.spalsh_screen)
                .into(ivBackground);
    }

    public void updateUI(WeatherResponse weatherResponse) {
        tvCity.setText(weatherResponse.getName());
        tvTemperature.setText(String.format("%.1f°C", weatherResponse.getMain().getTemp()));
        tvDescription.setText(weatherResponse.getWeather()[0].getDescription());

        String iconUrl = "https://openweathermap.org/img/w/" +
                weatherResponse.getWeather()[0].getIcon() + ".png";
        Glide.with(activity).load(iconUrl).into(ivWeatherIcon);

        updateBackground(weatherResponse.getWeather()[0].getMain());
        updateAdditionalInfo(weatherResponse);
    }

    public void updateUserProfile(String userName, String userEmail, String photoUrl) {
        name.setText(userName);
        mail.setText(userEmail);
        if (photoUrl != null) {
            Glide.with(activity).load(photoUrl).into(profileImage);
        }
    }

    private void updateBackground(String weatherMain) {
        int backgroundResId;
        switch (weatherMain.toLowerCase()) {
            case "clear":
                backgroundResId = R.drawable.clear_background;
                break;
            case "clouds":
                backgroundResId = R.drawable.colud_background;
                break;
            case "rain":
            case "drizzle":
                backgroundResId = R.drawable.rain_background;
                break;
            case "thunderstorm":
                backgroundResId = R.drawable.thunder_background;
                break;
            case "snow":
                backgroundResId = R.drawable.snow_background;
                break;
            default:
                backgroundResId = R.drawable.spalsh_screen;
                break;
        }
        Glide.with(activity).load(backgroundResId).into(ivBackground);
    }

    private void updateAdditionalInfo(WeatherResponse weatherResponse) {
        if (weatherResponse.getMain() != null) {
            updateInfoItem(0, R.drawable.humidity,
                    String.format("%.0f%%", weatherResponse.getMain().getHumidity()),
                    "Humidity");
            updateInfoItem(2, R.drawable.pressure,
                    String.format("%.0f hPa", weatherResponse.getMain().getPressure()),
                    "Pressure");
        }
        if (weatherResponse.getWind() != null) {
            updateInfoItem(1, R.drawable.wind,
                    String.format("%.1f m/s", weatherResponse.getWind().getSpeed()),
                    "Wind Speed");
        }
    }

    private void updateInfoItem(int index, int iconResId, String value, String label) {
        if (llAdditionalInfo != null && index < llAdditionalInfo.getChildCount()) {
            View infoItem = llAdditionalInfo.getChildAt(index);
            if (infoItem != null) {
                ImageView ivInfoIcon = infoItem.findViewById(R.id.ivInfoIcon);
                TextView tvInfoValue = infoItem.findViewById(R.id.tvInfoValue);
                TextView tvInfoLabel = infoItem.findViewById(R.id.tvInfoLabel);

                if (ivInfoIcon != null) ivInfoIcon.setImageResource(iconResId);
                if (tvInfoValue != null) tvInfoValue.setText(value);
                if (tvInfoLabel != null) tvInfoLabel.setText(label);
            }
        }
    }

    public void setGetWeatherClickListener(View.OnClickListener listener) {
        btnGetWeather.setOnClickListener(listener);
    }

    public String getCityInput() {
        return etCity.getText().toString().trim();
    }

    public void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}