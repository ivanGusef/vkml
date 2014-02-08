package com.ifgroup.vkml.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.VkLoaderApplication;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 9:36 PM
 * May the force be with you always.
 */
public class LoginActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains(C.Pref.ACCESS_TOKEN)) {
                    view.setVisibility(View.INVISIBLE);
                    final String accessToken = url.split("=")[1].split("&")[0];
                    VkLoaderApplication.login(LoginActivity.this, accessToken);
                    startActivity(new Intent(LoginActivity.this, AudioListActivity.class));
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });
        mWebView.loadUrl(C.API.LOGIN_URL);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
