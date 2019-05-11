package com.cssoftwaretech.smm;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cssoftwaretech.smm.database.DB_Groups;
import com.cssoftwaretech.smm.database.DB_SetMessages;
import com.google.firebase.crash.FirebaseCrash;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.cssoftwaretech.smm.NotificationSMM.CHANNEL_1_ID;
import static com.cssoftwaretech.smm.Settings.getContactName;

public class MessNotice {
    static boolean tuchOnOff = false;
    static AlertDialog.Builder dialog;
    static TextView tvHeader, tvMessage;
    static LayoutInflater inflater;
    static View layout;
    static ImageView imgIcon;
    static Button btnOK, btnCancel;
    static AlertDialog Dial;

    public static void excMess(Context context, String message, Exception e) {
        FirebaseCrash.log(""+message);
        FirebaseCrash.report(e);
        e.printStackTrace();
        print("Exception", e + "");
        messAlert(context, "Exception", message + "-" + e, 4);
    }

    public static void toastMess(Context context, String message, boolean longTime) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_alert, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvTA_message);
        tvMessage.setText(message);
        Toast s = new Toast(context);
        s.setView(layout);
        s.show();
        print(context.getClass().toString(), message);
    }
    public static void toastMess(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_alert, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvTA_message);
        tvMessage.setText(message);
        Toast s = new Toast(context);
        s.setView(layout);
        s.show();
        print(context.getClass().toString(), message);
    }

    public static void messAlert(final Context context, String title, String message, int iconId) {
        try {
            initialization(context);
            tvHeader.setText(title);
            tvMessage.setText(message);
            if (iconId == 0) {
            } else if (iconId == 1) {
                imgIcon.setImageResource(R.drawable.ic_info_message);
            } else if (iconId == 2) {
                imgIcon.setImageResource(R.drawable.ic_error_message);
            } else if (iconId == 3) {
                imgIcon.setImageResource(R.drawable.ic_warning_message);
            } else if (iconId == 4) {
                imgIcon.setImageResource(R.drawable.ic_exception_message);
            } else {
                imgIcon.setImageResource(iconId);
            }
            btnCancel.setVisibility(View.INVISIBLE);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            Log.e("Exception", "MessNotice");
        }
    }

    public static void messSure(final Context context, final int idNo, String title, String message) {
        try {
            initialization(context);
            tvHeader.setText(title);
            tvMessage.setText(message);
            btnCancel.setText("NO");
            btnOK.setText("YES");
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dial.dismiss();
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DB_SetMessages db_setMessages = new DB_SetMessages(context);
                    db_setMessages.updateSMSsendStatus(idNo, 1);
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            Toast.makeText(context, "Err In MSG", Toast.LENGTH_LONG).show();
        }
    }

    public static void messSureDelete(final Context context, final int idNo, String title, String message) {
        try {
            initialization(context);
            tvHeader.setText(title);
            tvMessage.setText(message);
            btnCancel.setText("NO");
            btnOK.setText("YES");
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dial.dismiss();
                }

            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DB_Groups db_groups = new DB_Groups(context);
                    db_groups.removeMember(idNo);
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            Toast.makeText(context, "Err In MSG", Toast.LENGTH_LONG).show();
        }
    }

    public static void messSureChange(final Context context, final int idNo, String title, String message) {
        try {
            initialization(context);
            tvHeader.setText(title);
            tvMessage.setText(message);
            btnCancel.setText("Cancel");
            btnOK.setText("YES");
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dial.dismiss();
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DB_SetMessages db_setMessages = new DB_SetMessages(context);
                    db_setMessages.updateSMSsendStatus(idNo, 1);
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "Err In MSG", e);
        }
    }

    private static void initialization(Context context) {
        if (Dial != null) {
            Dial.dismiss();
        }
        dialog = new AlertDialog.Builder(context);
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.mess_alert, null);
        tvHeader = (TextView) layout.findViewById(R.id.tvMA_header);
        tvMessage = (TextView) layout.findViewById(R.id.tvMA_message);
        imgIcon = (ImageView) layout.findViewById(R.id.imgMA_icon);
        btnOK = (Button) layout.findViewById(R.id.btnMA_OK);
        btnCancel = (Button) layout.findViewById(R.id.btnMA_cancel);
        Dial = dialog.create();
        Dial.setCancelable(false);
        Dial.setView(layout);
    }

    public static void print(int message) {
        logPrint("PRINT-Int ", message + "");
    }

    public static void print(String message) {
        logPrint("PRINT-S ", message);
    }

    public static void error(String message) {
        logPrint("Error ", message);
    }

    public static void print(String title, String message) {
        logPrint(title, message);
    }

    private static void logPrint(String title, String message) {
        Log.e(title, message);
    }

    public static void showNotification(int idNofity, Context context, String phoneNo, String message) {
        showNotification(context, idNofity, getContactName(phoneNo, context), "SMS Successfully " + message + " on " + phoneNo + " this Number");
    }

    public static void showNotification(Context context, int idNofity, String title, String text) {
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(Color.parseColor("#900c3e"))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(idNofity, notification);

        } catch (Exception e) {
            excMess(context, "showNotify", e);
        }
    }
}
