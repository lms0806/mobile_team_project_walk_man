package com.cookandroid.team_project4;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LastActivity extends AppCompatActivity {

    private WebView webView, webView2;
    private static String url = "https://www.youtube.com/embed/G32r7kx-MTw";
    private static String url2 = "https://www.youtube.com/embed/MiFcj6g5WzU";

    Button first_btn, second_btn, third_btn, last_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        /*다크모드 설정*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle("Walk Man - Video");

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientClass());

        webView2 = (WebView) findViewById(R.id.webView2);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl(url2);
        webView2.setWebChromeClient(new WebChromeClient());
        webView2.setWebViewClient(new WebViewClientClass2());

        /*창 넘기기 버튼*/
        first_btn = (Button) findViewById(R.id.first);
        first_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        second_btn = (Button) findViewById(R.id.second);
        second_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });

        third_btn = (Button) findViewById(R.id.third);
        third_btn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ThirdActivity.class);
                startActivity(intent);
                finish();
            }
        });

        last_btn = (Button) findViewById(R.id.last);
        last_btn.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class WebViewClientClass2 extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url2) {
            view.loadUrl(url2);
            return true;
        }
    }
}