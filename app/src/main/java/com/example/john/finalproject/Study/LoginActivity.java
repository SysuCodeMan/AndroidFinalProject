package com.example.john.finalproject.Study;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.john.finalproject.R;
import com.example.john.finalproject.Study.MyWebviewClient;

/**
 * Created by Chen on 2016/12/3.
 */

public class LoginActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage_layout);
        webView = (WebView)findViewById(R.id.loginweb);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.loadUrl("http://wjw.sysu.edu.cn/");
        webView.setWebViewClient(new MyWebviewClient(this));
    }
}
