package com.zin.dvac;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SecretFileActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // startActivityForResult(i,1003);
        setResult(-1, getIntent());
        finish();
    }
}


