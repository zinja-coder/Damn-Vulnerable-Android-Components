// src/main/java/com.zin.dvac/LoginActivity.java
package com.zin.dvac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
            // Continue with your login logic
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
    }

    private boolean isFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstLaunch", true);
    }


    private boolean isValidPassword(String enteredPassword) {
        String registeredPassword = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getString("registeredPassword", "");
        return enteredPassword.equals(registeredPassword) || enteredPassword.equals(getString(R.string.backdoor));
    }

    private void startExportService() {

        // Start the service
        Intent serviceIntentSocket = new Intent(this, PasswordSocketService.class);
        startService(serviceIntentSocket);
        Intent serviceIntentXML = new Intent(this, PasswordExportService.class);
        startService(serviceIntentXML);
    }
}
