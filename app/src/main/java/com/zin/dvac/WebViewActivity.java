package com.zin.dvac;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = findViewById(R.id.webView);

        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a WebViewClient to handle redirects within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Get the URI from the intent
        Uri webUri = Uri.parse(getIntent().getStringExtra("webUri"));

        // Load the URI into the WebView
        webView.loadUrl(webUri.toString());
    }
}
