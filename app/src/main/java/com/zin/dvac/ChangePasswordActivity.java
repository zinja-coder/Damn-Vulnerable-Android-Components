package com.zin.dvac;

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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_PASSWORD = "registeredPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle("DVAC: Vulnerable Components");

        // Check for intent data
        Intent intent = getIntent();
        if (intent.getData() != null) {
            // If intent data is a valid URI, handle password reset flow
            Uri uri = intent.getData();
            String token = uri.getQueryParameter("token");
            String ipAddress = uri.getHost();

            // Validate the token with the server
            validateResetToken(token, ipAddress);
        } else if (intent.hasExtra("currentPassword") && intent.hasExtra("newPassword")) {
            // If intent data is not a URI and contains the current password,
            // proceed with the change password flow without checking for a valid token
            String currentPassword = intent.getStringExtra("currentPassword");
            String newPassword = intent.getStringExtra("newPassword");
            changePassword(newPassword,currentPassword);
            renderUI(currentPassword, newPassword);
        } else {
            // Handle the case where no valid data is provided
            Toast.makeText(this, "Invalid data in the intent", Toast.LENGTH_SHORT).show();
            redirectToLoginActivity();
        }
    }

    private void openGitHubWebsite() {
        // Open GitHub website
        String githubUrl = "https://github.com/zinja-coder";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
        startActivity(intent);
    }

    private boolean changePassword(String newPassword, String currentPassword) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(currentPassword.equals(preferences.getString(KEY_PASSWORD,"def"))){

            editor.putString(KEY_PASSWORD, newPassword);
            editor.apply();
            return true;
        }else{
        return false;}
    }

    private void validateResetToken(String token, String ipAddress) {
        // Check if the IP address is not null or empty
        if (ipAddress != null && !ipAddress.isEmpty()) {
            // Use the IP address for making requests to the server
            OkHttpClient client = new OkHttpClient();

            // Use the ipAddress variable in the URL
            Request request = new Request.Builder()
                    .url("http://" + ipAddress + ":5000/validate_reset_token")
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"reset_token\":\"" + token + "\"}"))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePasswordActivity.this, "Failed to validate token", Toast.LENGTH_SHORT).show();
                        redirectToLoginActivity();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(ChangePasswordActivity.this, "Token is valid. Allowing password reset.", Toast.LENGTH_SHORT).show();
                            renderUI();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(ChangePasswordActivity.this, "Invalid token. Password reset failed.", Toast.LENGTH_SHORT).show();
                            redirectToLoginActivity();
                        });
                    }
                }
            });
        } else {
            // Handle the case where IP address is not provided in the deep link
            Toast.makeText(this, "IP address not provided in the deep link", Toast.LENGTH_SHORT).show();
            redirectToLoginActivity();
        }
    }

    private void redirectToLoginActivity() {
       Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void renderUI() {
        final EditText etCurrentPassword = findViewById(R.id.etCurrentPassword);
        final EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnChangePassword = findViewById(R.id.btnChange);

        // FAB click listener
        FloatingActionButton fabOpenGitHub = findViewById(R.id.fabOpenGitHub);
        fabOpenGitHub.setOnClickListener(view -> openGitHubWebsite());

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get existing and new passwords
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                // Validate and change password
                if (changePassword(newPassword,currentPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after changing the password
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Invalid existing password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void renderUI(String currentPassword, String newPassword) {
        final EditText etCurrentPassword = findViewById(R.id.etCurrentPassword);
        final EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnChangePassword = findViewById(R.id.btnChange);

        etCurrentPassword.setText(currentPassword);
        etNewPassword.setText(newPassword);

        renderUI();
    }
}
