package com.cssoftwaretech.smm;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.cssoftwaretech.smm.database.DB_Groups;
import com.cssoftwaretech.smm.database.DB_Settings;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;

public class Settings extends AppCompatActivity {

    private Context context;
    private DB_Settings db_settings;
    private static TelephonyManager telephonyManager;
    private DB_Groups db_groups;
    private Switch SWsmsAlwaysON, swi_ClockType, swi_currentDT, swi_ShowMessage, swi_ShowRemainingTime;
    private ImageView imgMsgShow,imgTimeShow;
    private RadioGroup rgDeliverMessage, rgPrefeSIMforSMS;
    private RadioButton rbReportOff, rbReportOnly, rbReportPlusNotification, rbPrefSIM_1, rbPrefSIM_2, rbPrefSIM_default;
    private LinearLayout ll_prefSIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            initialization();
            getSIMdetail();
            dataLoad();
            saveSettingState();
            onAction();


        } catch (Exception e) {
            excMess(context, "Setting", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getSIMdetail() {
        if (ContextCompat.checkSelfPermission(Settings.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Settings.this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(Settings.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        } else {

            int i = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    CharSequence displayName = subscriptionInfo.getDisplayName();
                    i++;
                    if (i == 1) {
                        rbPrefSIM_1.setVisibility(View.VISIBLE);
                        rbPrefSIM_1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        rbPrefSIM_1.setText(displayName + "");
                    } else if (i == 2) {
                        rbPrefSIM_2.setVisibility(View.VISIBLE);
                        rbPrefSIM_2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        rbPrefSIM_2.setText(displayName + "");
                    }
                }
            }
        }
    }

    private void onAction() {
        rgDeliverMessage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int deliverID = rgDeliverMessage.getCheckedRadioButtonId();
                RadioButton rbSelect = findViewById(deliverID);
                String text = rbSelect.getText().toString();
                switch (text) {
                    case "Report Off":
                        db_settings.updateDeliverType("1");
                        break;
                    case "Report Only":
                        db_settings.updateDeliverType("2");
                        break;
                    case "Report + Notification":
                        db_settings.updateDeliverType("3");
                        break;
                    default:
                        break;
                }
            }
        });
        swi_ShowMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    imgMsgShow.setImageResource(R.drawable.img_msg_show);
                }else{
                    imgMsgShow.setImageResource(R.drawable.img_msg_hide);
                }
            }
        });
        swi_ShowRemainingTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    imgTimeShow.setImageResource(R.drawable.img_time_show);
                }else{
                    imgTimeShow.setImageResource(R.drawable.img_time_hide);
                }
            }
        });
    }

    private void initialization() {
        context = this;
        db_settings = new DB_Settings(context);
        swi_ClockType = (Switch) findViewById(R.id.settings_sw_clockType);
        SWsmsAlwaysON = (Switch) findViewById(R.id.settings_sw_smsAlwaysON);
        swi_currentDT = (Switch) findViewById(R.id.settings_sw_currentDateTimeSet);
        swi_ShowMessage = (Switch) findViewById(R.id.settings_sw_showMessageOnScreen);
        imgMsgShow = (ImageView) findViewById(R.id.settings_iv_showMessageOnScreen);
        imgTimeShow = (ImageView) findViewById(R.id.settings_iv_showRemainingTime);
        swi_ShowRemainingTime = (Switch) findViewById(R.id.settings_sw_showRemainingTime);
        rgDeliverMessage = (RadioGroup) findViewById(R.id.settings_rg_delivery);
        rbReportOff = (RadioButton) findViewById(R.id.settings_rb_reportOff);
        rbReportOnly = (RadioButton) findViewById(R.id.settings_rb_reportOnly);
        rbReportPlusNotification = (RadioButton) findViewById(R.id.settings_rb_reportPlusNotification);
        rgPrefeSIMforSMS = (RadioGroup) findViewById(R.id.settings_rg_PreferredSIMforSMS);
        ll_prefSIM = (LinearLayout) findViewById(R.id.settings_ll_pref_SIM_for_sms);
        rbPrefSIM_1 = (RadioButton) findViewById(R.id.settings_rb_PreferredSIM_1);
        rbPrefSIM_2 = (RadioButton) findViewById(R.id.settings_rb_PreferredSIM_2);
        rbPrefSIM_default = (RadioButton) findViewById(R.id.settings_rb_PreferredSIM_default);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        rbPrefSIM_1.setVisibility(View.INVISIBLE);
        rbPrefSIM_1.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        rbPrefSIM_2.setVisibility(View.INVISIBLE);
        rbPrefSIM_2.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        rbPrefSIM_default.setChecked(true);
    }

    private void dataLoad() {

        if (db_settings.isEmptyDataBase()) {
            db_settings.insertSettings();
        }
        if (db_settings.is24HourView()) {
            swi_ClockType.setChecked(true);
        } else {
            swi_ClockType.setChecked(false);
        }
        if (db_settings.isSmsAlwaysON()) {
            SWsmsAlwaysON.setChecked(true);
        } else {
            SWsmsAlwaysON.setChecked(false);
        }
        if (db_settings.isCurrentDateTimeSet()) {
            swi_currentDT.setChecked(true);
        } else {
            swi_currentDT.setChecked(false);
        }
        if (db_settings.isShowFrontMessage()) {
            imgMsgShow.setImageResource(R.drawable.img_msg_show);
            swi_ShowMessage.setChecked(true);

        } else {
            imgMsgShow.setImageResource(R.drawable.img_msg_hide);
            swi_ShowMessage.setChecked(false);
        }
        if (db_settings.isShowRemainingTime()) {
            imgTimeShow.setImageResource(R.drawable.img_time_show);
            swi_ShowRemainingTime.setChecked(true);
        } else {
            imgTimeShow.setImageResource(R.drawable.img_time_hide);
            swi_ShowRemainingTime.setChecked(false);
        }
        switch (db_settings.getDeliverType()) {
            case "1":
                rbReportOff.setChecked(true);
                break;
            case "2":
                rbReportOnly.setChecked(true);
                break;
            case "3":
                rbReportPlusNotification.setChecked(true);
                break;
            default:
                rbReportOff.setChecked(false);
                rbReportOnly.setChecked(false);
                rbReportPlusNotification.setChecked(false);
                break;
        }
        switch (db_settings.getDefaultSIM_ID()) {
            case "1":
                rbPrefSIM_1.setChecked(true);
                break;
            case "2":
                rbPrefSIM_2.setChecked(true);
                break;
            default:
                rbPrefSIM_default.setChecked(true);
                break;
        }


        /*
        int sim=getDefaultPhoneSMS_SIM(context);
        print("Default","SIM "+sim);
        if(sim==1)
        {
            rbPrefSIM_1.setChecked(true);
        }else if(sim==2){
            rbPrefSIM_2.setChecked(true);
        }*/
        print("" + isSimAvailable(), "" + false);
        if (!isSimAvailable()) {
            rbPrefSIM_1.setText("SIM Not Available");
            rbPrefSIM_2.setVisibility(View.INVISIBLE);
            rbPrefSIM_1.setTextColor(Color.RED);
            rbReportOnly.setEnabled(false);
            rbReportPlusNotification.setEnabled(false);
        }
        if (db_settings.getDefaultSIM_ID().equalsIgnoreCase("1")) {
            rbPrefSIM_1.setChecked(true);
        } else if (db_settings.getDefaultSIM_ID().equalsIgnoreCase("2")) {
            rbPrefSIM_2.setChecked(true);
        }
    }

    private void saveSettingState() {
        swi_ClockType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db_settings.updateClockType(b);
            }
        });
        SWsmsAlwaysON.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db_settings.updateSmsAlwaysON(b);
            }
        });
        swi_currentDT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db_settings.updateCurrentDateTimeSet(b);
            }
        });
        swi_ShowMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db_settings.updateShowFrontMessage(b);
            }
        });
        swi_ShowRemainingTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db_settings.updateShowRemainingTime(b);
            }
        });

        rgPrefeSIMforSMS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int simID = rgPrefeSIMforSMS.getCheckedRadioButtonId();
                RadioButton rbSelect = findViewById(simID);
                String text = rbSelect.getText().toString();
                if (rbPrefSIM_1.getText().toString().equalsIgnoreCase(text)) {
                    db_settings.updateDefaultSIM("1");
                } else if (rbPrefSIM_2.getText().toString().equalsIgnoreCase(text)) {
                    db_settings.updateDefaultSIM("2");
                } else {
                    db_settings.updateDefaultSIM("0");
                }
            }
        });
    }

    public static String getDateForUser(String mSysDateFormat) {
        int pos = 0, mDay, mMonth, mYear;
        pos = mSysDateFormat.indexOf("/", pos);
        mDay = Integer.parseInt(mSysDateFormat.substring(0, pos));
        pos++;
        int od = pos;
        pos = mSysDateFormat.indexOf("/", pos);
        mMonth = Integer.parseInt(mSysDateFormat.substring(od, pos));
        pos++;
        mYear = Integer.parseInt(mSysDateFormat.substring(pos, mSysDateFormat.length()));
        return (mDay + "/" + (mMonth + 1) + "/" + mYear);
    }

    public static String getTimeDifference(String oldTime, String newTime) {
        //oldTime
        int posOld, mOldHour, mOldMinute, posNew, mNewHour, mNewMinute;
        posOld = oldTime.indexOf(":");
        mOldHour = Integer.parseInt(oldTime.substring(0, posOld));
        mOldMinute = Integer.parseInt(oldTime.substring(++posOld, oldTime.length()));
        //New Time
        posNew = newTime.indexOf(":");
        mNewHour = Integer.parseInt(newTime.substring(0, posNew));
        mNewMinute = Integer.parseInt(newTime.substring(++posNew, newTime.length()));

        int HH, MM;
        String sHH, sMM;
        HH = mNewHour - mOldHour;
        MM = mNewMinute - mOldMinute;
        sHH = HH + "";
        sMM = MM + "";
        if (HH < 0) {
            // sHH = "Gone" + (HH + "").replace("-", "");
        }
        if (MM < 0) {
            // error("Negative Minutes");
            sMM = (MM + "").replace("-", "");
        }
        return (sHH + ":" + sMM);
    }

    public static long getTimeDefference(long timeInMillis) {
        int yyyy, mm, dd, hh, m;
        Calendar calendar = Calendar.getInstance();
        yyyy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
        hh = calendar.get(Calendar.HOUR_OF_DAY);
        m = calendar.get(Calendar.MINUTE);
        Calendar validDateCurrent = Calendar.getInstance();
        validDateCurrent.set(yyyy, mm, dd, hh, m, 0);
        return (timeInMillis - validDateCurrent.getTimeInMillis());
    }

    public static long getDateTimeIntoMillis(String newDate, String newTime) {
        int newTpos = 0, newhours = 0, newminutes = 0, newDpos = 0, newDD = 0, newMM = 0, newYYYY = 0;

        if (newTime != null) {
            if (newTime.length() > 3) {
                newTpos = newTime.indexOf(":");
                newhours = Integer.parseInt(newTime.substring(0, newTpos));
                newminutes = Integer.parseInt(newTime.substring(++newTpos, newTime.length()));
            }
        }
        if (newDate != null) {
            if (newDate.length() > 3) {
                //newDate
                newDpos = newDate.indexOf("/", newDpos);
                newDD = Integer.parseInt(newDate.substring(0, newDpos));
                newDpos++;
                int newod = newDpos;
                newDpos = newDate.indexOf("/", newDpos);
                newMM = Integer.parseInt(newDate.substring(newod, newDpos));
                newDpos++;
                newYYYY = Integer.parseInt(newDate.substring(newDpos, newDate.length()));
            }
        }
        Calendar validDateNew = Calendar.getInstance();
        validDateNew.set(newYYYY, newMM, newDD, newhours, newminutes, 0);
        return (validDateNew.getTimeInMillis());
    }

    public static long getSystemDateTimeInMillis() {
        int yyyy, mm, dd, hh, m;
        Calendar calendar = Calendar.getInstance();
        yyyy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
        hh = calendar.get(Calendar.HOUR_OF_DAY);
        m = calendar.get(Calendar.MINUTE);
        Calendar validDateCurrent = Calendar.getInstance();
        validDateCurrent.set(yyyy, mm, dd, hh, m, 0);
        return (validDateCurrent.getTimeInMillis());
    }

    public static String getSystemCurrentTime() {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        return (hours + ":" + minutes);
    }

    public static String getSystemCurrentTime(int plus) {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int mmm = Calendar.MINUTE;
        print("minutes", "mmm =" + mmm);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        minutes += plus;
        if (minutes > 59) {
            minutes = minutes - 59;
            hours++;
            if (hours > 23) {
                hours = hours - 23;
            }
        }
        return (hours + ":" + (minutes));
    }

    public static String getSysCurrentTimeHHMMSS() {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        return (hours + ":" + minutes + ":" + second);
    }

    public static String getTimeIn12hour(String timeIn24hour) {
        int pos = timeIn24hour.indexOf(":");
        int hours = Integer.parseInt(timeIn24hour.substring(0, pos));
        int minutes = Integer.parseInt(timeIn24hour.substring(++pos, timeIn24hour.length()));
        int hh = hours;
        print("hours", "" + hours);
        String mm = minutes + "";
        String AM_PM = "AM";
        if (hours > 12) {
            AM_PM = "PM";
            hh = hours - 12;
        } else {
            if (hours == 0) {
                hh = 12;
            }
        }
        if (minutes < 10) {
            mm = "0" + minutes;
        }

        String time = (hh + ":" + mm + " " + AM_PM);
        return (time);
    }

    public static String getSysCurrentDateDDMMYYYY() {
        int yyyy, mm, dd;
        Calendar calendar = Calendar.getInstance();
        yyyy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
        return (dd + "/" + mm + "/" + yyyy);
    }
    public static String getSysCurrentTimeHHMM() {
        int HH, MM;
        Calendar calendar = Calendar.getInstance();
        HH = calendar.get(Calendar.HOUR_OF_DAY);
        MM = calendar.get(Calendar.MINUTE);
        return (HH + ":" + MM);
    }

    public static String getContactName(final String phoneNumber, Context context) {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
                String contactName = "";
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor.getCount() == 0) {
                    print("Null Value " + phoneNumber);
                    return phoneNumber;
                } else {
                    if (cursor.moveToFirst()) {
                        contactName = cursor.getString(0);
                        print("Name is " + contactName);

                        cursor.close();

                        return contactName;

                    }
                }
            } else {
                return (phoneNumber);
            }
        } catch (Exception e) {
            excMess(context, "getContactName", e);
        }
        return null;
    }

    public static Bitmap getContactImage(Context context, String number) {
        try {
            Bitmap photo = null;
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                ContentResolver contentResolver = context.getContentResolver();
                String contactId = null;
                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                print("Image URI", "" + uri);
                String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

                Cursor imgCur = contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

                if (imgCur != null && imgCur.moveToFirst()) {
                    // while(imgCur.moveToFirst()) {
                    contactId = imgCur.getString(imgCur.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                    print("Photo ID ", "Id " + contactId);
                    // }
                    imgCur.close();
                }

                 photo = null;

                try {
                    if (contactId != null) {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);
                        }
                    }
           /* assert inputStream != null;
            inputStream.close();*/
                } catch (Exception e) {
                    excMess(context, "getContactImageINP", e);
                }
                return photo;
            }
        } catch (Exception e) {
            excMess(context, "getContactImage Full", e);
        }
        return null;
    }

  /*  public static void groupCreateDialogUpdate(Context context, int gpId) {
        groupCreateDialog(context, "Update", gpId);
    }

    public static void groupCreateDialogNew(Context context) {
        groupCreateDialog(context, "NEW", 0);
    }

    public static void groupCreateDialog(final Context context, final String type, final int gpId) {
        try {
            //Create GP dialog
            final EditText etGPname, etGPsubTitle;
            Button btnCreateGP, btnGpCreate_Cancel;
            String gpName, gpSubTitle;
            final AlertDialog createGpDial;
            AlertDialog.Builder createGroupDialog;
            LayoutInflater inflater;
            View layout;
            createGroupDialog = new AlertDialog.Builder(context);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.group_create_activity, null);
            etGPname = (EditText) layout.findViewById(R.id.gc_gpName);
            etGPsubTitle = (EditText) layout.findViewById(R.id.gc_gpSubTitle);
            btnGpCreate_Cancel = (Button) layout.findViewById(R.id.gc_btnGpCreate_Cancel);
            btnCreateGP = (Button) layout.findViewById(R.id.gc_btnCreate);
            db_groups = new DB_Groups(context);
            createGpDial = createGroupDialog.create();
            createGpDial.setCancelable(false);
            createGpDial.setView(layout);
            createGpDial.show();
            //Data Set
            if (type.equalsIgnoreCase("NEW")) {

                print("group new", "Create Group");
            } else if (type.equalsIgnoreCase("Update")) {
                print("group ID", "IS - " + gpId);
                Cursor cur = db_groups.getGroupById(gpId);
                if (cur.getCount() > 0) {
                    cur.moveToFirst();
                    etGPname.setText(cur.getString(1));
                    etGPsubTitle.setText(cur.getString(2));
                }
            }
            btnGpCreate_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createGpDial.dismiss();

                }
            });
            btnCreateGP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etGPname.length() < 1) {
                        etGPname.requestFocus();
                        toastMess(context, "Enter Group Name", true);
                        return;
                    } else if (etGPsubTitle.length() < 1) {
                        etGPname.requestFocus();
                        toastMess(context, "Enter Group Name", true);
                        return;
                    } else {
                        if (type.equalsIgnoreCase("Update")) {
                            if (db_groups.updateGroup(gpId, etGPname.getText().toString(), etGPsubTitle.getText().toString())) {
                                toastMess(context, "Group Update Successful", false);
                            } else {
                                toastMess(context, "Err Group Not Update", false);
                            }
                        } else if (type.equalsIgnoreCase("NEW")) {
                            if (db_groups.createGroup(etGPname.getText().toString(), etGPsubTitle.getText().toString())) {
                                toastMess(context, "New Group Created", false);
                            } else {
                                toastMess(context, "Err Group Not Create", false);
                            }
                        }
                        createGpDial.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            excMess(context, "getContact Image", e);
        }
    }
*/

    public boolean isSimAvailable() {
        try {
            boolean isAvailable = false;
            int simState = telephonyManager.getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT: //SimState = “No Sim Found!”;
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = “Network Locked!”;
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = “PIN Required to access SIM!”;
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = “PUK Required to access SIM!”; // Personal Unblocking Code
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    isAvailable = true;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = “Unknown SIM State!”;
                    break;
            }
            return isAvailable;
        } catch (Exception e) {
            excMess(context, "isSimAvailable", e);
        }
        return false;
    }

    public static String replacePhone2Name(Context context, String number) {
        try {
            int colorNo = -1;
            StringBuilder sb = new StringBuilder();
            String color[] = {"#fda02e", "#1c2253", "#FFFD2E92", "#5b2efd", "#04564c", "#616161", "#ec05be"};
            int i, num = 1;
            for (i = 0; i < number.length(); i++) {
                if (number.charAt(i) == ',') {
                    num++;
                }
            }
            print("NUMBER", "" + num);
            String phoneNo[] = new String[num];

            for (i = 0; i < num; i++) {
                int pos = number.indexOf(",");
                print("pos " + pos);
                if (pos == -1) {
                    if (number.length() > 9) {
                        if (number.substring(0, number.length()).length() > 0) {
                            phoneNo[i] = number.substring(0, number.length());
                            print("End Now");
                        } else {
                            return null;
                        }
                    }
                } else {
                    if (number.substring(0, pos).length() > 0) {
                        phoneNo[i] = number.substring(0, pos);
                        print(i + " No is " + phoneNo[i]);
                        number = number.substring(++pos, number.length());
                    } else {
                        return null;
                    }
                }
            }
            phoneNo[num - 1] = number;
            print("color set");
            for (i = 0; i < num; i++) {
                print("No " + i, "" + phoneNo[i]);
                if (colorNo > 5) {
                    colorNo = -1;
                }
                colorNo++;
                if ((i) != 0) {
                    sb.append("<br>");

                }
                print("Color No", "" + colorNo);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (num > 1) {
                        sb.append("<font color=" + color[colorNo] + ">[" + (i + 1) + "] " + getContactName(phoneNo[i], context) + "</font>");
                    } else {
                        sb.append("<font color=" + color[colorNo] + ">" + getContactName(phoneNo[i], context) + "</font>");
                    }
                } else {
                    return "Permission Denied";
                }
            }
            return (sb.toString());
        } catch (Exception e) {
            excMess(context, "replacePhone2Name", e);
        }
        return null;
    }

    public static String getNameTowChar(String name) {
        if (name != null) {
            char a = '*', b = '*';
            int pos = name.indexOf(' ', 0);
            if (name.length() != 0) {
                a = name.charAt(0);
            }
            if (pos != -1 && (pos + 1) != name.length()) {
                b = name.charAt(pos + 1);
            } else {
                b = '*';
            }

            if (Character.isLetter(a)) {
                return (a + "" + b).toUpperCase();
            } else {
                return ("1");
            }

        }
        return ("-1");

    }

    public static String getTruePhoneNumber(String phoneNo) {
        String trueNumber = "";
        for (int i = 0; i < phoneNo.length(); i++) {
            char aChar = phoneNo.charAt(i);
            if (Character.isDigit(aChar) || aChar == '+') {
                trueNumber += aChar;
            }
        }
        return (trueNumber);
    }

    public static boolean isSendSMSWorking(Context context, int requestCode) {
        Intent intent = new Intent(context, MessageAutoSendReceiver.class);
        intent.setAction(MessageAutoSendReceiver.ACTION_ALARM_RECEIVER);
        boolean isWorking = (PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE) != null);
        return (isWorking);
    }


}
