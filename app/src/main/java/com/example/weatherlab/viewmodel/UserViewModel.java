package com.example.weatherlab.viewmodel;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.weatherlab.model.UserData;
import com.example.weatherlab.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<UserData> getUserData() {
        return userRepository.getUserData();
    }

    public void signIn(ActivityResultLauncher<Intent> launcher) {
        userRepository.signIn(launcher);
    }

    public void handleSignInResult(Intent data) {
        userRepository.handleSignInResult(data);
    }

    public void updateUserData(FirebaseUser user) {
        userRepository.updateUserData(user);
    }

    public void setAuthCallback(UserRepository.AuthCallback callback) {
        userRepository.setAuthCallback(callback);
    }
}