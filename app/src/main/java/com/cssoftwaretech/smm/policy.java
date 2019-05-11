package com.cssoftwaretech.smm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class policy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        WebView privacyPolicy = (WebView) findViewById(R.id.policy_wv_showPrivacyPolicy);
        privacyPolicy.loadUrl("file:///android_asset/policy/terms_conditions.html");
        privacyPolicy.getSettings().setJavaScriptEnabled(true);
    }
}
