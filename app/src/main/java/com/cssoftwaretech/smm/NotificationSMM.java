package com.cssoftwaretech.smm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import static com.cssoftwaretech.smm.MessNotice.excMess;

public class NotificationSMM extends Application {
    public static final String CHANNEL_1_ID = "Channel1";
    public static final String CHANNEL_2_ID = "Channel2";

    @Override
    public void onCreate() {
        try {
        super.onCreate();
            createNotification();
        } catch (Exception e) {
            excMess(getBaseContext(), "Notification", e);
        }
    }

    private void createNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("SMM channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("SMM SMS channel 2");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }
}
