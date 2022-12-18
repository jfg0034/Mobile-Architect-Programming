package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity {

    // Views
    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText questionEditText;
    private EditText answerEditText;

    // Database
    private LogInDB mLogInDB;

    // Extra key
    public static final String EXTRA_USERNAME = "com.example.inventoryapp.username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Retrieve views by id
        nameEditText = findViewById(R.id.nameInput);
        usernameEditText = findViewById(R.id.usernameInput);
        passwordEditText = findViewById(R.id.passwordInput);
        questionEditText = findViewById(R.id.securityQuestionInput);
        answerEditText = findViewById(R.id.securityAnswerInput);

        // Start single instance of LogIn database
        mLogInDB = LogInDB.getInstance(getApplicationContext());
    }

    // Creates a new user based on the information gathered from the user
    // Only a username and password are required to create an account
    // other values are optional
    public void onSubmit(View v) {
        // Temporary values to retrieve input
        String name = nameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String question = questionEditText.getText().toString().trim();
        String answer = answerEditText.getText().toString().trim();

        User user = new User();

        // Username is restricted to at least 6 characters
        // Password is restricted to at least 8 characters
        if (username.length() >= 6 && password.length() >= 8) {
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setSecurityQuestion(question);
            user.setSecurityAnswer(answer);

            // If the user can be added to the database then the username value will be passed
            // to the Main Activity
            if (mLogInDB.addUser(user)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(EXTRA_USERNAME, username);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clears middle stack
                startActivity(intent);
                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Enter a valid input!", Toast.LENGTH_SHORT).show();
        }
    }
}