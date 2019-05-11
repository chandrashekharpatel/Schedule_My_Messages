package com.cssoftwaretech.smm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cssoftwaretech.smm.Fragment.FragmentPagerAdapter;
import com.cssoftwaretech.smm.Fragment.HelpInstruction;
import com.cssoftwaretech.smm.database.DB_SetMessages;
import com.cssoftwaretech.smm.database.DB_Settings;
import com.cssoftwaretech.smm.gp.GroupList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.List;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.messAlert;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Runnable, TabLayout.OnTabSelectedListener{
    private static Menu menu;
    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private Context context = this;
    private Toast toast;
    private long lastBackPressTime = 0;
    private DB_Settings db_settings;
    private RecyclerView rvMessagesSet;
    private LinearLayout layItemView, tvAddNewMessage;
    private TextView tvIdNo;
    private ImageView imgStatus;
    private MessageAdapter adapter;
    private List<Message_itemView> listItems = new ArrayList<>();
    private DB_SetMessages db_setMessages;
    private boolean isItemOpen = false, isNewIntentOpen = false, isNotChange = false;
    private int position = 0;
    private int openIntent = 0;
    private Handler handler = null;
    private Runnable listUpdate;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int TAB_OPENED = 0;
    private boolean isMessageTabInitil = false, isGroupTabInitil = false;
    private boolean inMainMenu = false;
    //private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            context = this;
         /*   MobileAds.initialize(this, "ca-app-pub-6139134558279624/7284025709");

            // Use an activity context to get the rewarded video instance.
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            mRewardedVideoAd.setRewardedVideoAdListener(this);
            loadRewardedVideoAd();*/
            checkAndRequestPermissions();
            initialization();
            onActivity();
            initializeTabs();
            onActionTabs();
        } catch (Exception e) {
            Toast.makeText(this, "Error" + e, Toast.LENGTH_LONG).show();
            excMess(context, "MainSC", e);
        }
    }

  /*  private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-6139134558279624/7284025709",
                new AdRequest.Builder().build());
    }
*/
    private void onActionTabs() {
        try {
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            if (viewPager.getCurrentItem() == 0) {

            }
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        TAB_OPENED = 0;
                        viewPager.setCurrentItem(tab.getPosition());

                        menu.findItem(R.id.action_settings).setTitle("Settings");
                        if (!isMessageTabInitil) {
                            isMessageTabInitil = true;
                        }
                    } else if (tab.getPosition() == 1) {
                        TAB_OPENED = 1;
                        viewPager.setCurrentItem(tab.getPosition());
                        menu.findItem(R.id.action_settings).setTitle("Create Group");
                        if (!isGroupTabInitil) {
                            isGroupTabInitil = true;
                        }

                    } else if (tab.getPosition() == 2) {
                        TAB_OPENED = 2;
                        viewPager.setCurrentItem(tab.getPosition());
                        menu.findItem(R.id.action_settings).setTitle("Go To Pro");

                    } else {
                        tab.select();
                    }
                    print("Tab " + tab.getPosition(), "onTabSelected");
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    print("Tab " + tab.getPosition(), "onTabUnselected");
                }


                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    print("Tab " + tab.getPosition(), "onTabReselected");

                }
            });
        } catch (Exception e) {
            excMess(context, "onActionTabs", e);
        }
    }

    private void initialization() {
        if (isFirstTime()) {
            startActivity(new Intent(context, HelpInstruction.class));
          //  toastMess(context, "First Time", false);
            db_settings = new DB_Settings(context);
            db_settings.insertSettings();
        }
        db_setMessages = new DB_SetMessages(context);
    }

    private void initializeTabs() {
        try {
            tabLayout = (TabLayout) findViewById(R.id.tabLayoutMain);
            tabLayout.addTab(tabLayout.newTab().setText("Messages"));
            tabLayout.addTab(tabLayout.newTab().setText("Groups"));
            tabLayout.addTab(tabLayout.newTab().setText("SMS Log"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = (ViewPager) findViewById(R.id.pager);
            FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00ffee"));
        } catch (Exception e) {
            excMess(context, "initializeTabs", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(listUpdate);
        }
    }

    private void onActivity() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
            }
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        } catch (Exception e) {
            excMess(context, "onActivity", e);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
           /* if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            }*/
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (TAB_OPENED == 0) {
            if (id == R.id.action_settings) {
                startActivity(new Intent(context, Settings.class));
                return true;
            }
        } else if (TAB_OPENED == 1) {
            if (id == R.id.action_settings) {
                GroupList groupList = new GroupList();
                groupList.groupCreateDialog(context, "NEW", 0);
                return true;
            }
        } else if (TAB_OPENED == 2) {
            if (id == R.id.action_settings) {
                goToSMMpro();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSMMpro() {
        String url = "https://play.google.com/store/apps/details?id=com.cssoftwaretech.smmpro";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_setGoToPro:
                goToSMMpro();
                break;
            case R.id.nav_setSettings:
                startActivity(new Intent(context, Settings.class));
                break;
            case R.id.nav_privacy_policy:
                startActivity(new Intent(context, policy.class));
                break;
            case R.id.nav_aboutCS:
                startActivity(new Intent(context, AboutCS.class));
                break;
            case R.id.nav_send:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Automatic Messaging System Application!\n\n https://play.google.com/store/apps/details?id=com.cssoftwaretech.smm");
                startActivity(shareIntent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int contactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int READ_PHONE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (READ_PHONE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void run() {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

      /*  viewPager.setCurrentItem(tab.getPosition());
        print("TT", "" + tab.getText());
        if (viewPager.getId() == 0) {

        }*/
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        print("Ad Unit", "onTabUnselected");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        print("Ad Unit", "onTabReselected");
    }
/*
    @Override
    public void onRewarded(RewardItem reward) {
        print("Ad Unit", "onRewarded");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        print("Ad Unit", "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        print("Ad Unit", "onRewardedVideoAdClosed");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        print("Ad Unit", "onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        print("Ad Unit", "onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        print("Ad Unit", "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        print("Ad Unit", "onRewardedVideoStarted");
    }

    public void onRewardedVideoCompleted() {
        print("Ad Unit", "onRewardedVideoCompleted");
    }
*/
}

