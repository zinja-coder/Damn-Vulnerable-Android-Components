package com.zin.dvac;

// src/main/java/com.zin.dvac/ChangePasswordActivity.java

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_PASSWORD = "registeredPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle("DVAC: Vulnerable Components");

        final EditText etExistingPassword = findViewById(R.id.etExistingPassword);
        final EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnChangePassword = findViewById(R.id.btnChange);

        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get existing and new passwords
                String existingPassword = etExistingPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                // Validate and change password
                if (changePassword(newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after changing the password
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Invalid existing password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check for intent data
        Intent intent = getIntent();
        if (intent.hasExtra("existingPassword") && intent.hasExtra("newPassword")) {
            // If intent data is present, pre-fill the fields
            String existingPassword = intent.getStringExtra("existingPassword");
            String newPassword = intent.getStringExtra("newPassword");
            changePassword(newPassword);
            etExistingPassword.setText(existingPassword);
            etNewPassword.setText(newPassword);
            
        }
    }

    private void openGitHubWebsite() {
        // Open GitHub website
        String githubUrl = "https://github.com/zinja-coder";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivity(intent);
    }

    private boolean changePassword(String newPassword) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if the existing password matches the stored password
        //String storedPassword = preferences.getString(KEY_PASSWORD, "");
       // if (existingPassword.equals(storedPassword)) {
            // Save the new password to SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_PASSWORD, newPassword);
            editor.apply();

            return true;
        //}

        //return false;
    }
}
