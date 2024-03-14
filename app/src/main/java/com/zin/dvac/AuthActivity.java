package com.zin.dvac;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = findViewById(R.id.webView);

        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a custom WebViewClient
        webView.setWebViewClient(new MyWebViewClient());

        // Get the URI from the intent
        Uri webUri = Uri.parse(getIntent().getStringExtra("webUri"));

        // Add custom header
        Map<String, String> headers = new HashMap<>();
        headers.put("Auth", "bearer 123123");

        // Load the URI into the WebView with custom headers
        webView.loadUrl(webUri.toString(), headers);
        // Load the URI into the WebView
        //webView.loadUrl(webUri.toString());
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Load the URL within the WebView
            view.loadUrl(url);
            return true;
        }
    }
}
