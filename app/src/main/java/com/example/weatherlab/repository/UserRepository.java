package com.example.weatherlab.repository;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.weatherlab.model.UserData;
import com.example.weatherlab.utils.AuthManager;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {
    private final AuthManager authManager;
    private final MutableLiveData<UserData> userData = new MutableLiveData<>();

    public UserRepository(AuthManager authManager) {
        this.authManager = authManager;
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }

    public void signIn(ActivityResultLauncher<Intent> launcher) {
        authManager.signIn(launcher);
    }

    public void handleSignInResult(Intent data) {
        authManager.handleSignInResult(data);
    }

    public void updateUserData(FirebaseUser user) {
        if (user != null && user.getPhotoUrl() != null) {
            UserData newUserData = new UserData(
                    user.getDisplayName(),
                    user.getEmail(),
                    user.getPhotoUrl().toString()
            );
            userData.setValue(newUserData);
        }
    }

    public interface AuthCallback {
        void onAuthSuccess(FirebaseUser user);
        void onAuthError(String error);
    }

    private AuthCallback authCallback;

    public void setAuthCallback(AuthCallback callback) {
        this.authCallback = callback;
        authManager.setAuthCallback(new AuthManager.AuthCallback() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                updateUserData(user);
                if (authCallback != null) {
                    authCallback.onAuthSuccess(user);
                }
            }

            @Override
            public void onAuthError(String error) {
                if (authCallback != null) {
                    authCallback.onAuthError(error);
                }
            }
        });
    }
}