package com.example.weatherlab.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.weatherlab.repository.UserRepository;
import com.example.weatherlab.utils.AuthManager;
import com.example.weatherlab.viewmodel.UserViewModel;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final AuthManager authManager;

    public UserViewModelFactory(AuthManager authManager) {
        this.authManager = authManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            UserRepository userRepository = new UserRepository(authManager);
            return (T) new UserViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}