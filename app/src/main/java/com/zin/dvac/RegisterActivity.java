// src/main/java/com.zin.dvac/RegisterActivity.java
package com.zin.dvac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("DVAC: Vulnerable Components");

        editTextPassword = findViewById(R.id.editTextPassword);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                savePassword(password);
                setFirstLaunchFlag();
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setFirstLaunchFlag() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstLaunch", false);
        editor.apply();
    }

    private void savePassword(String password) {
        getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit()
                .putString("registeredPassword", password)
                .apply();
    }
}
