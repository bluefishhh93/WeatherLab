package com.example.weatherlab.model;

import androidx.annotation.DrawableRes;

public class WeatherInfoItem {
    @DrawableRes
    private final int iconResId;
    private final String label;
    private final String value;

    public WeatherInfoItem(@DrawableRes int iconResId, String value, String label) {
        this.iconResId = iconResId;
        this.value = value;
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherInfoItem that = (WeatherInfoItem) o;
        return iconResId == that.iconResId &&
                value.equals(that.value) &&
                label.equals(that.label);
    }

    //getter
    public int getIconResId() {return iconResId;}
    public String getLabel() {return label;}
    public String getValue() {return value;}
}
