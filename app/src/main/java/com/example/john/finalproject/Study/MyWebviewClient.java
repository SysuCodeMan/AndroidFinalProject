package com.example.john.finalproject.Study;

import android.app.Activity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Chen on 2016/12/3.
 */

public class MyWebviewClient extends WebViewClient {
    CookieManager cookieManager = CookieManager.getInstance();
    Activity context;
    MyWebviewClient(Activity context) {
        this.context = context;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String cookie = cookieManager.getCookie(url);
        System.out.println(cookie);
        Log.v("test", "cookie:"+cookie);
        if (hasLogin(cookie)) {
            context.setResult(0);
            context.finish();
        }
    }

    private boolean hasLogin(String cookie) {
        return cookie.contains("sno");
    }
}
