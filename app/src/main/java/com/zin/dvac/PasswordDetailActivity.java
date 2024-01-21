// src/main/java/com.zin.dvac/PasswordDetailActivity.java
package com.zin.dvac;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PasswordDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private long passwordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_detail);
        setTitle("DVAC: Vulnerable Components");

        databaseHelper = new DatabaseHelper(this);

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPassword = findViewById(R.id.tvPassword);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Button btnDelete = findViewById(R.id.btnDelete);

        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());


        // Retrieve passwordId from Intent
        passwordId = getIntent().getLongExtra("passwordId", -1);

        if (passwordId != -1) {
            // Fetch password details from the database
            Password password = databaseHelper.getPassword(passwordId);

            if (password != null) {
                tvUsername.setText("Username: " + password.getUsername());
                tvPassword.setText("Password: " + password.getPassword());
                tvDescription.setText("Description: " + password.getDescription());
            } else {
                Toast.makeText(this, "Failed to retrieve password details", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Invalid password ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete password logic
                databaseHelper.deletePassword(passwordId);
                Toast.makeText(PasswordDetailActivity.this, "Password deleted", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after deleting the password
            }
        });
    }

    private void openGitHubWebsite() {
        // Open GitHub website
        String githubUrl = "https://github.com/zinja-coder";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        databaseHelper.close();
        super.onDestroy();
    }
}
