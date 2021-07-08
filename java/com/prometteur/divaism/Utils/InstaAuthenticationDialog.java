package com.prometteur.divaism.Utils;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.prometteur.divaism.R;

public class InstaAuthenticationDialog extends Dialog {
    private final String redirect_url;
    private final String request_url;
    private final String insta_secret;
    private final String client_id;
    private AuthenticationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_instagram_auth);


        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
        webView.loadUrl(request_url);

        webView.setWebViewClient(webViewClient);
        CookieManager.getInstance().setAcceptCookie(true);

    }
    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                InstaAuthenticationDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("access_token=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                Log.e("InstaAuthTok", "onPageFinished: "+access_token);
                listener.onTokenReceived(access_token);
                InstaAuthenticationDialog.this.dismiss();

            }else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token");
                InstaAuthenticationDialog.this.dismiss();
            }
        }
    };

    public InstaAuthenticationDialog(@NonNull Context context, AuthenticationListener listener) {
        super(context);
        this.listener = listener;
        this.insta_secret=context.getResources().getString(R.string.instaSecretKey);
        this.client_id=context.getResources().getString(R.string.instaclient_id);
        this.redirect_url = context.getResources().getString(R.string.redirect_url);
        this.request_url = context.getResources().getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.instaclient_id) +"&client_secret="+insta_secret+
                "&grant_type="+"authorization_code"+
                "&redirect_url=" + redirect_url +
                "&response_type=token&display=touch&scope=user_profile,basic";
    }
    public interface AuthenticationListener {
        void onTokenReceived(String auth_token);
    }
}
