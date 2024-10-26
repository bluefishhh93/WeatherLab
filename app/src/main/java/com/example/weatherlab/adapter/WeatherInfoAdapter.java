package com.example.weatherlab.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherlab.databinding.ItemWeatherInfoBinding;
import com.example.weatherlab.model.WeatherInfoItem;


public class WeatherInfoAdapter extends ListAdapter<WeatherInfoItem, WeatherInfoAdapter.ViewHolder> {

    public WeatherInfoAdapter() {
        super(new WeatherInfoDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeatherInfoBinding binding = ItemWeatherInfoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWeatherInfoBinding binding;

        ViewHolder(ItemWeatherInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WeatherInfoItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }

    static class WeatherInfoDiffCallback extends DiffUtil.ItemCallback<WeatherInfoItem> {
        @Override
        public boolean areItemsTheSame(@NonNull WeatherInfoItem oldItem, @NonNull WeatherInfoItem newItem) {
            return oldItem.getLabel().equals(newItem.getLabel());
        }

        @Override
        public boolean areContentsTheSame(@NonNull WeatherInfoItem oldItem, @NonNull WeatherInfoItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
