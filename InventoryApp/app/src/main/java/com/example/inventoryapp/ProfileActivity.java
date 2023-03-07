package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
// This activity can be further developed to update user's name, password, question and security answer
public class ProfileActivity extends AppCompatActivity {

    private String currentUsername = "";

    private TextView nameText;
    private TextView usernameText;
    private TextView questionText;
    private TextView answerText;

    private LogInDB mLogInDB;
    private User currentUser;
    public static final String EXTRA_USERNAME = "com.example.inventoryapp.username";

    private ItemsDB mItemsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();

        // If a username is found, the activity stores its value to use it as key to retrieve data from database
        if (intent.hasExtra(MainActivity.EXTRA_USERNAME)) {
            currentUsername = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
        }

        // Views are assigned to the local fields
        nameText = findViewById(R.id.nameInput);
        usernameText = findViewById(R.id.usernameInput);
        questionText = findViewById(R.id.securityQuestionInput);
        answerText = findViewById(R.id.securityAnswerInput);

        mLogInDB = LogInDB.getInstance(getApplicationContext());

        currentUser = mLogInDB.getUserDisplay(currentUsername);

        // If no user is logged in, the fields will show "Nothing to display" as placeholder
        if (currentUser == null) {
            nameText.setText("Nothing to display");
            usernameText.setText("Nothing to display");
            questionText.setText("Nothing to display");
            answerText.setText("Nothing to display");
        }
        // If user is logged in, its values will be displayed on the screen, except the password
        else {
            currentUser = mLogInDB.getUserDisplay(currentUsername);
            nameText.setText(currentUser.getName());
            usernameText.setText(currentUser.getUsername());
            questionText.setText(currentUser.getSecurityQuestion());
            answerText.setText(currentUser.getSecurityAnswer());
        }
        mItemsDB = ItemsDB.getInstance(getApplicationContext());
    }

    // Deletes the user, currently only feature from this activity
    public void onDelete(View v) {
        if (mLogInDB.deleteUser(currentUsername)) {
            if (mItemsDB.deleteUserData(currentUsername)) {
                Toast.makeText(this, "Data deleted.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Account deleted.", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "Nothing to delete.", Toast.LENGTH_SHORT).show();
        }
    }

}