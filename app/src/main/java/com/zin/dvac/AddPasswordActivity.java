package com.zin.dvac;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddPasswordActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        setTitle("DVAC: Vulnerable Components");

        databaseHelper = new DatabaseHelper(this);

        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etDescription = findViewById(R.id.etDescription);
        Button btnAddPassword = findViewById(R.id.btnAddPassword);
        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());


        btnAddPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add user logic
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    long id = databaseHelper.addPassword(new Password(username, password, description));
                    if (id != -1) {
                        Toast.makeText(AddPasswordActivity.this, "User added with ID: " + id, Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after adding the user
                    } else {
                        Toast.makeText(AddPasswordActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPasswordActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                }
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
