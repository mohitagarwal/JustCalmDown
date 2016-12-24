package com.openshutters.justcalmdown;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_about);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView webview = (WebView) findViewById(R.id.about_webview);
        setupWebView(webview);
    }

    private void setupWebView(WebView webview) {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        ProgressDialog progressBar = ProgressDialog.show(this, null, "Loading...");
        webview.setWebViewClient(new LoadingWebViewClient(progressBar));

        webview.loadUrl("http://www.mindpub.com/art549.htm");
    }
}
