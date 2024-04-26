package com.zin.dvac;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the PasswordExportService on startup
        startExportService();

        if (isFirstLaunch()) {
            // Start the RegisterActivity for the first launch
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity
        } else {
            setContentView(R.layout.activity_login);
            setTitle("DVAC: Vulnerable Components");
        }

        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPassword = editTextLoginPassword.getText().toString();
                if (isValidPassword(enteredPassword)) {
                    startActivity(new Intent(LoginActivity.this, PasswordManagerActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add the Forgot Password button and its click listener
       /* Button btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showForgotPasswordDialog();
            }
        });*/
    }

    private boolean isFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstLaunch", true);
    }

    private boolean isValidPassword(String enteredPassword) {
        String registeredPassword = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getString("registeredPassword", "");
        String dev_shortcut = getString(R.string.backdoor);
        byte[] dev_shortcut_bytes = Base64.decode(dev_shortcut, Base64.DEFAULT);
        dev_shortcut = new String(dev_shortcut_bytes);
        return enteredPassword.equals(registeredPassword) || enteredPassword.equals(dev_shortcut);
    }

    private void startExportService() {
        // Start the service
        Intent serviceIntentSocket = new Intent(this, PasswordSocketService.class);
        startService(serviceIntentSocket);
        Intent serviceIntentXML = new Intent(this, PasswordExportService.class);
        startService(serviceIntentXML);
    }

    private void showForgotPasswordDialog() {
        // Create a dialog to prompt the user for the host IP
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Host IP");

        final EditText input = new EditText(this);
        input.setHint("Host IP");
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hostIP = input.getText().toString();
                sendForgotPasswordRequest(hostIP);
            }
       });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendForgotPasswordRequest(String hostIP) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://"+hostIP+":5000/send_password_reset")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"user_id\":\"your_user_id\"}"))
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Failed to send forgot password request", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Forgot password request sent successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Failed to send forgot password request", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
