package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button logIn;
    private Button logOut;
    private TextView greeting;
    private String currentUsername = "";
    public static final String EXTRA_USERNAME = "com.example.inventoryapp.username";

    @Override
    // When the activity stops, the notification is sent
    protected void onStop() {
        super.onStop();
        Intent intentS = new Intent(this, InventoryService.class);
        startService(intentS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logIn = findViewById(R.id.log_in);
        logOut = findViewById(R.id.log_out);
        greeting = findViewById(R.id.greeting);

        Intent intent = getIntent();
        // If an EXTRA is passed by the account created or logged in (extra name is the same in both activities)
        // then it is retrieved and assigned to the greeting
        if (intent.hasExtra(CreateAccountActivity.EXTRA_USERNAME)) {
            currentUsername = intent.getStringExtra(CreateAccountActivity.EXTRA_USERNAME);
            logIn.setVisibility(View.GONE);
            logOut.setVisibility(View.VISIBLE);
            greeting.setText("Welcome " + currentUsername + "!");
        }

    }

    // Starts InventoryView activity
    public void onClickInventory(View v) {
        Intent intent = new Intent(this, InventoryViewActivity.class);
        // Only if an extra value exists the new extra can be passed
        if (currentUsername.length() != 0) {
            intent.putExtra(EXTRA_USERNAME, currentUsername);
        }
        startActivity(intent);
    }

    // Starts Settings activity, where permissions are requested
    public void onClickSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // Starts LogIn activity
    public void onClickLogIn(View v) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    // Starts LogOut activity, setting username to default empty value
    public void onClickLogOut(View v) {
        currentUsername = "";
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Starts profile activity, needs further development
    public void onClickProfile(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        // Only if an extra value exists the new extra can be passed
        if (currentUsername.length() != 0) {
            intent.putExtra(EXTRA_USERNAME, currentUsername);
        }
        startActivity(intent);
    }

    // Starts Search activity
    public void onClickSearch(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    // Exits application
    public void onClickExit(View v) {
        System.exit(0);
    }
}