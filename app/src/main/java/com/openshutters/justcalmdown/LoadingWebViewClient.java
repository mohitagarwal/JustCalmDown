package com.openshutters.justcalmdown;

import android.app.ProgressDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class LoadingWebViewClient extends WebViewClient {

    public static final String TAG = "LoadingWebViewClient";
    private ProgressDialog progressDialog;

    public LoadingWebViewClient(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
