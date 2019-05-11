package com.cssoftwaretech.smm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutCS extends AppCompatActivity {

    TextView webSiteOpen;
    ImageView logoCS, iv_facebook, iv_webSite;
    private LinearLayout goToSMMpro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_cs);
        webSiteOpen = (TextView) findViewById(R.id.aboutcs_webaddress);
        logoCS = (ImageView) findViewById(R.id.aboutcs_logo);
        iv_webSite = (ImageView) findViewById(R.id.aboutcs_webSite);
        iv_facebook = (ImageView) findViewById(R.id.aboutcs_facebook);
        goToSMMpro =(LinearLayout)findViewById(R.id.aboutcs_SMMproApp);
        webSiteOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSite();
            }
        });
        iv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook();
            }
        });
        iv_webSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSite();
            }
        });
        logoCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSite();
            }
        });
        goToSMMpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSMMpro();
            }
        });
    }

    private void facebook() {
        String url = "https://www.facebook.com/i.chandrashekharpatel";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    private void openSite() {
        String url = "http://cssoftwaretech.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    private void goToSMMpro() {
        String url = "https://play.google.com/store/apps/details?id=com.cssoftwaretech.smmpro";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


}
