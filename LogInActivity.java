package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    private LogInDB mLogInDB;

    public static final String EXTRA_USERNAME = "com.example.inventoryapp.username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        usernameEditText = findViewById(R.id.usernameInput);
        passwordEditText = findViewById(R.id.passwordInput);

        // Initiates Log In database
        mLogInDB = LogInDB.getInstance(getApplicationContext());
    }

    // Sends user to the create account activity
    public void onClickRegister(View v) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // Reads user input and validates credentials
    public void onLogIn(View v) {
        User currentUser;

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        currentUser = mLogInDB.getUser(username, password);

        if (currentUser == null) { // No user is found
            Toast.makeText(this, "Wrong username or password.", Toast.LENGTH_SHORT).show();
        }
        else { // User is found
            // Value of valid registered username is passed to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_USERNAME, username);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        }
    }
}