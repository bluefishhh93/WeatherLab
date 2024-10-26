// AuthManager.java
package com.example.weatherlab.utils;
import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.example.weatherlab.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class AuthManager {
    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;
    private AuthCallback authCallback;

    public interface AuthCallback {
        void onAuthSuccess(FirebaseUser user);
        void onAuthError(String error);
    }

    public AuthManager(Activity activity) {
        this.activity = activity;
        firebaseAuth = FirebaseAuth.getInstance();

        // Use R.string.client_id instead of BuildConfig.WEB_CLIENT_ID
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
        firebaseAuth.getInstance();
    }

    public void setAuthCallback(AuthCallback callback) {
        this.authCallback = callback;
    }

    public void signIn(ActivityResultLauncher<Intent> launcher) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        launcher.launch(signInIntent);
    }

    public void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                if (authCallback != null) {
                    authCallback.onAuthError("Failed to get Google account");
                }
            }
        } catch (ApiException e) {
            if (authCallback != null) {
                authCallback.onAuthError("Google sign in failed: " + e.getStatusCode());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            if (authCallback != null) {
                authCallback.onAuthError("Invalid ID token");
            }
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful() && authCallback != null) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            authCallback.onAuthSuccess(user);
                        } else {
                            authCallback.onAuthError("User is null after successful authentication");
                        }
                    } else if (authCallback != null) {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Unknown authentication error";
                        authCallback.onAuthError(errorMessage);
                    }
                });
    }

    public void signOut(OnCompleteListener<Void> onCompleteListener) {
        // Sign out from Firebase
        firebaseAuth.signOut();

        // Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener(onCompleteListener);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}