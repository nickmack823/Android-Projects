package com.c323FinalProject.nicmacki.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.c323FinalProject.nicmacki.fragments.InitialFragment;
import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.fragments.SignInFragment;
import com.c323FinalProject.nicmacki.fragments.SignUpFragment;
import com.google.android.material.imageview.ShapeableImageView;

public class LoginActivity extends AppCompatActivity {

    Context context;
    ActionBar actionBar;
    FragmentTransaction fragmentTransaction;
    FrameLayout fragmentLayout;
    ShapeableImageView signInImage;
    SharedPreferences loginPreferences;
    boolean implementBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in.
        loginPreferences = getSharedPreferences
                ("LoginDetails", MODE_PRIVATE);
        boolean loggedIn = loginPreferences.getBoolean("Logged In", false);
        if (loggedIn) {
            goMainActivity();
            finish();
            return;
        }
        fragmentLayout = findViewById(R.id.initialFragmentLayout);
        signInImage = findViewById(R.id.signInImage);
        context = this;
        implementBack = false;
        actionBar = getSupportActionBar();

        Thread initialize = new Thread(new Runnable() {
            @Override
            public void run() {
                initialScreen();
            }
        });
        initialize.start();
    }

    /**
     * Displays Initial Fragment.
     */
    public void initialScreen() {
        InitialFragment initialFragment = new InitialFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.initialFragmentLayout, initialFragment);
        fragmentTransaction.commit();
        if (actionBar != null) {
            actionBar.setTitle("Final Project");
        }
    }

    @Override
    public void onBackPressed() {
        if (implementBack) {
            initialScreen();
            implementBack = false;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Displays SignIn Fragment.
     * @param view The View clicked to execute this method
     */
    public void goSignIn(View view) {
        SignInFragment signInFragment = new SignInFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.initialFragmentLayout, signInFragment);
        fragmentTransaction.commit();
        implementBack = true;
        if (actionBar != null) {
            actionBar.setTitle("Sign In");
        }
    }

    /**
     * Displays SignUp Fragment
     * @param view The View clicked to execute this method
     */
    public void goSignUp(View view) {
        SignUpFragment signUpFragment = new SignUpFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.initialFragmentLayout, signUpFragment);
        fragmentTransaction.commit();
        implementBack = true;
        if (actionBar != null) {
            actionBar.setTitle("Sign Up");
        }
    }

    /**
     * Signs the user into the app.
     * @param view The View clicked to execute this method
     */
    public void signIn(View view) {
        TextView usernameInput = findViewById(R.id.signInUsername);
        TextView passwordInput = findViewById(R.id.signInPassword);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String spUsername = loginPreferences.getString("Username", "None");
        String spPassword = loginPreferences.getString("Password", "None");
        String message;
        if (!spUsername.equals(username)) {
            message = "Invalid username.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else if (!spPassword.equals(password)) {
            message = "Incorrect password.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor editor = loginPreferences.edit();
            // Note that user IS logged in now
            editor.putBoolean("Logged In", true);
            editor.commit();
            goMainActivity();
        }
    }

    /**
     * Creates an account based on input username and password
     * @param view The View clicked to execute this method
     */
    public void signUp(View view) {
        TextView usernameInput = findViewById(R.id.signUpUsername);
        TextView passwordInput = findViewById(R.id.signUpPassword);
        TextView confirmInput = findViewById(R.id.signUpConfirm);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmedPassword = confirmInput.getText().toString();
        // Accessing Shared Preferences
        SharedPreferences userPreferences = getSharedPreferences
                ("LoginDetails", MODE_PRIVATE);
        String spUsername = userPreferences.getString("Username", "None");
        // Check if username and password are long enough (and not blank)
        if (username.length() < 5 || username.equals("")) {
            Toast.makeText(this, "Username must contain 5 or more characters.", Toast.LENGTH_LONG).show();
            return;
        } else if (password.length() < 5 || password.equals("")) {
            Toast.makeText(this, "Password must contain 5 or more characters.", Toast.LENGTH_LONG).show();
            return;
        }
        // Check if passwords match
        if (!password.equals(confirmedPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            return;
        }
        // If no account has been made previously
        if (spUsername.length() >= 5) {
            System.out.println(spUsername);
            Toast.makeText(this, "An account already exists on this device, please sign in.", Toast.LENGTH_LONG).show();
        } else {
            // Editor required to edit SP
            SharedPreferences.Editor editor = userPreferences.edit();
            editor.putString("Username", username);
            editor.putString("Password", password);
            // Finalize writing to preferences
            editor.commit();
            Toast.makeText(this, "Account created, please sign in.", Toast.LENGTH_LONG).show();
            // Redirect to sign-in screen
            goSignIn(null);
        }
    }

    /**
     * Starts MainActivity.
     */
    public void goMainActivity() {
        Intent goMain = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(goMain);
    }
}