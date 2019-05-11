package com.cssoftwaretech.smm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cssoftwaretech.smm.database.DB_Groups;
import com.cssoftwaretech.smm.database.DB_SetMessages;
import com.cssoftwaretech.smm.database.DB_Settings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.List;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;
import static com.cssoftwaretech.smm.Settings.getDateForUser;
import static com.cssoftwaretech.smm.Settings.getSysCurrentDateDDMMYYYY;
import static com.cssoftwaretech.smm.Settings.getSystemCurrentTime;
import static com.cssoftwaretech.smm.Settings.getTimeDefference;
import static com.cssoftwaretech.smm.Settings.getTimeIn12hour;
import static com.cssoftwaretech.smm.Settings.getTruePhoneNumber;
import static com.cssoftwaretech.smm.Settings.isSendSMSWorking;
import static com.cssoftwaretech.smm.Settings.replacePhone2Name;

public class NewMessages extends Activity {
    private final int GROUP_REQUEST_CODE = 1000;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 101;
    private DB_SetMessages db_setMessages;
    private DB_Settings db_settings;
    private LinearLayout ll_lay_SIM_layout, lay_date_picker, lay_time_picker, lay_SMS_repeat, lay_saveDone;
    private DB_Groups db_groups;
    private EditText etNumber, etMessage;
    private TextView tvMsgShow, etDate, etTime, saveMessage, btnDelete, tvNameShow, tvNumberLabel, tvTimeMessage, tvDateMessage, tvMess_sendNow, tvStatusMessage;
    private String vNumber, vMessage, vDate, vTime, vSIM_type, vSMS_sendStatus, vRepeatSMS;
    private int vNo;
    private Button btnSelectDate, btnSelectTime;
    private Switch swiStatus;
    private Context context;
    private int mHour, mMinute, mDay, mYear, mMonth, msgId, gpId, gId;
    private Calendar c;
    private int pos = 0;
    private boolean isNewMessage = false, isGroupUI = false, isNoramalSMS = false;
    private boolean isNotSaved_Num = false, isNotSaved_Mess = false, isNotSaved_Date = false, isNotSaved_Time = false, isNotSaved_RPT = false, isNotSaved_SIM = false, isFutureTime = true;
    private AlarmManager alarmManager;
    private Spinner spiRepeatSMS;
    private RadioGroup rgSIM_type;
    private RadioButton rbSIM_1, rbSIM_2, rbSIM_default;
    private ArrayAdapter repeatAdapter;
    private AlertDialog.Builder dialog;
    private AlertDialog Dial;
    private ImageView btnAddNumber;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;

    public static void cancelSendingSMS(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MessageAutoSendReceiver.class);
        intent.setAction(MessageAutoSendReceiver.ACTION_ALARM_RECEIVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message_newset);
            context = this;
            MobileAds.initialize(this, "ca-app-pub-6139134558279624~1278371004");
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-6139134558279624/8554225798");
            initialization();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            loadInitalData();
            onClickMethod();
            permission();
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "newSMS");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SendSMS");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        } catch (Exception e) {
            excMess(context, "In Main L", e);
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(NewMessages.this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            tvMsgShow.setText("Permission Denied");
            tvMsgShow.setTextColor(Color.RED);
            setEnabledMode(false);
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewMessages.this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    private void loadInitalData() {
        isNewMessage = false;
        isGroupUI = false;
        isNoramalSMS = false;
        tvMsgShow.requestFocus();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            isNewMessage = true;
            tvMsgShow.setText("NEW");
            tvMsgShow.setTextColor(Color.GREEN);
            swiStatus.setChecked(db_settings.isSmsAlwaysON());
            if (db_settings.isCurrentDateTimeSet()) {
                vDate = getSysCurrentDateDDMMYYYY();
                etDate.setText(getDateForUser(vDate));
                vTime = getSystemCurrentTime(5);
                etTime.setText(db_settings.is24HourView() ? vTime : getTimeIn12hour(vTime));
            }
        } else {
            isNewMessage = false;
            msgId = getIntent().getExtras().getInt("messageIdNo", -1);
            vNo = msgId;
            gpId = getIntent().getExtras().getInt("groupMessSetgpId", -1);
            gId = getIntent().getExtras().getInt("groupMessUpdategId", -1);
            print("Extra Data ", "messageIdNo " + msgId + " ,groupMessSetId " + gpId + " ,groupMessUpdategId " + gId);
            if (msgId != -1) {
                print("Message Initialization");
                isNoramalSMS = true;
                isGroupUI = false;
                numberInput("Phone", true);
                Cursor cur = db_setMessages.getMessagesByNo(msgId);
                if (cur.getCount() > 0) {
                    cur.moveToFirst();
                    vNo = Integer.parseInt(cur.getString(0));
                    vNumber = cur.getString(1);
                    vMessage = cur.getString(2);
                    vDate = cur.getString(3);
                    vTime = cur.getString(4);
                    vSIM_type = cur.getString(5);

                    switch (vSIM_type) {
                        case "0":
                            rbSIM_default.setChecked(true);
                            break;
                        case "1":
                            rbSIM_1.setChecked(true);
                            break;
                        case "2":
                            rbSIM_2.setChecked(true);
                            break;
                    }
                    vSMS_sendStatus = cur.getString(6);
                    setSMSProcessStatus(vSMS_sendStatus);
                    vRepeatSMS = cur.getString(7);
                    spiRepeatSMS.setSelection(Integer.parseInt(vRepeatSMS));
                    etNumber.setText(vNumber);
                    etMessage.setText(vMessage);
                    etDate.setText(getDateForUser(vDate));
                    etTime.setText(db_settings.is24HourView() ? vTime : getTimeIn12hour(vTime));
                    print("STATUS", "" + vSIM_type);
                    setDeleteBTN(true);
                    tvMsgShow.setTextColor(Color.YELLOW);
                    tvMsgShow.setText("Modify");
                    setNameMessage(vNumber);
                    swiStatus.setChecked(isSendSMSWorking(context, vNo));
                    layoutVisible();
                    if (db_setMessages.getMSMsendStatus(vNo) == 9) {
                        setMessageNumberLabel("This message has been Delivered");
                        setEnabledMode(false);
                    } else if (db_setMessages.getMSMsendStatus(vNo) == 4) {
                        setMessageNumberLabel("This message has been sent");
                        setEnabledMode(false);
                    } else {
                        validDateTime();
                    }
                }
            } else if (gpId != -1) {
                print("Group Initialization");
                isGroupUI = true;
                isNoramalSMS = false;
                numberInput("Group Message", false);
                if (gId != -1) {
                    tvMsgShow.setText("Modify");
                    Cursor cur = db_groups.getGMessage(gId);
                    if (cur.getCount() > 0) {
                        cur.moveToFirst();
                        vMessage = cur.getString(1);
                        vDate = cur.getString(2);
                        vTime = cur.getString(3);
                        vSMS_sendStatus = cur.getString(4);
                        setSMSProcessStatus(vSMS_sendStatus);
                        swiStatus.setChecked(isSendSMSWorking(context, GROUP_REQUEST_CODE + gId));
                        vSIM_type = cur.getString(5);
                        switch (vSIM_type) {
                            case "1":
                                rbSIM_1.setChecked(true);
                                break;
                            case "2":
                                rbSIM_2.setChecked(true);
                                break;
                            default:
                                rbSIM_default.setChecked(true);
                                break;
                        }
                        vRepeatSMS = cur.getString(6);
                        spiRepeatSMS.setSelection(Integer.parseInt(vRepeatSMS));
                        etMessage.setText(vMessage);
                        etDate.setText(getDateForUser(vDate));
                        etTime.setText(db_settings.is24HourView() ? vTime : getTimeIn12hour(vTime));
                        setDeleteBTN(true);
                        layoutVisible();
                        if (db_groups.getGStatus(gId) == 9) {
                            setMessageNumberLabel("This message has been Delivered");
                            setEnabledMode(false);
                        } else if (db_groups.getGStatus(gId) == 4) {
                            setMessageNumberLabel("This message has been sent");
                            setEnabledMode(false);
                        } else {
                            validDateTime();
                        }
                    }
                    layoutVisiblety(View.VISIBLE);
                } else {
                    tvMsgShow.setText("Group SMS");
                    tvMsgShow.setTextColor(Color.GREEN);
                    swiStatus.setChecked(db_settings.isSmsAlwaysON());
                    if (db_settings.isCurrentDateTimeSet()) {
                        vDate = getSysCurrentDateDDMMYYYY();
                        etDate.setText(getDateForUser(vDate));
                        vTime = getSystemCurrentTime(5);
                        etTime.setText(db_settings.is24HourView() ? vTime : getTimeIn12hour(vTime));
                    }
                }
            } else {
                tvMsgShow.setText("Error In loadInitalData");
                tvMsgShow.setTextColor(Color.RED);
            }
            // View view = getWindow().getDecorView();
            int orientation = getResources().getConfiguration().orientation;
            spiRepeatSMS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    print("vRpt", "" + vRepeatSMS);
                    if (Integer.parseInt((vRepeatSMS != null) ? vRepeatSMS : "0") != i) {
                        isNotSaved_RPT = true;
                    } else {
                        isNotSaved_RPT = false;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }


        if (etDate.length() == 0) {
            dateSetInVariable(getSysCurrentDateDDMMYYYY());
        } else {
            dateSetInVariable(vDate);
        }

        if (etTime.length() == 0) {
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        } else {
            pos = vTime.indexOf(":");
            mHour = Integer.parseInt(vTime.substring(0, pos));
            mMinute = Integer.parseInt(vTime.substring(++pos, vTime.length()));
        }

        etNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etNumber.setBackgroundResource(R.drawable.bg_edit_text_focus);
                } else {
                    etNumber.setBackgroundResource(R.drawable.bg_edit_text);
                }
            }
        });
        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etMessage.setBackgroundResource(R.drawable.bg_edit_text_focus);
                } else {
                    etMessage.setBackgroundResource(R.drawable.bg_edit_text);
                }
            }
        });

    }

    private void setSMSProcessStatus(String statusID) {

        switch (Integer.parseInt(statusID)) {
            case 0:
                setProgressMessage("INSERT", true);
                break;
            case 1:
                setProgressMessage("SMS SAVED", true);
                break;
            case 2:
                setProgressMessage("OFF", false);
                break;
            case 3:
                setProgressMessage("ON", true);
                break;
            case 4:
                setProgressMessage("SMS SEND", true);
                break;
            case 5:
                setProgressMessage("GENERIC FAILURE -Please try again", false);
                break;
            case 6:
                setProgressMessage("SERVICE is currently unavailable", false);
                break;
            case 7:
                setProgressMessage("NULL PDU provided", false);
                break;
            case 8:
                setProgressMessage("RADIO OFF -Your device airplane mode off and try again", false);
                break;
            case 9:
                setProgressMessage("SMS DELIVERED", true);
                break;
            case 10:
                setProgressMessage("SMS NOT DELIVERED ", false);
                break;

        }
    }

    private void setProgressMessage(String message, boolean color) {
        tvStatusMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvStatusMessage.setText(Html.fromHtml("<font color=#" + (color ? "00FF00" : "FF0000") + ">" + message + "</font>"));
    }

    private void setMessageNumberLabel(String message) {
        tvNumberLabel.setText(Html.fromHtml("<font color=#aeaeae>Number </font><font color=#00FF00>" + message + "</font>"));
    }

    private void setEnabledMode(boolean type) {
        etNumber.setEnabled(type);
        etMessage.setEnabled(type);
        etDate.setEnabled(type);
        etTime.setEnabled(type);
        btnAddNumber.setEnabled(type);
        btnSelectDate.setEnabled(type);
        btnSelectTime.setEnabled(type);
        spiRepeatSMS.setEnabled(type);
        rgSIM_type.setEnabled(type);
        saveMessage.setEnabled(type);
        rbSIM_1.setEnabled(type);
        rbSIM_2.setEnabled(type);
        rbSIM_default.setEnabled(type);
    }

    private void getSIMdetail() {
        int i = 0;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                List<SubscriptionInfo> activeSubscriptionInfoList = null;
                activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    CharSequence displayName = subscriptionInfo.getDisplayName();
                    //  int subscriptionId = subscriptionInfo.getSubscriptionId();
                    i++;
                    if (i == 1) {
                        rgSIM_type.setVisibility(View.VISIBLE);
                        ll_lay_SIM_layout.setVisibility(View.VISIBLE);
                        ll_lay_SIM_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        ll_lay_SIM_layout.setPadding(0, 5, 0, 0);
                        rbSIM_1.setVisibility(View.VISIBLE);
                        rbSIM_1.setText(displayName + "");
                    } else if (i == 2) {
                        rbSIM_2.setVisibility(View.VISIBLE);
                        rbSIM_2.setText(displayName + "");
                    }
                }
            }
        }
        rbSIM_default.setVisibility(View.VISIBLE);
        rbSIM_default.setText("Default");
        rbSIM_default.setChecked(true);
    }

    private void setRepeatSMS() {

        String[] repeatSMS = {"Does not repeat", "Daily", "Weekly", "Monthly", "Yearly"};
        repeatAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item,
                repeatSMS);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiRepeatSMS.setAdapter(repeatAdapter);
    }

    private void dateSetInVariable(String date) {
        pos = 0;
        pos = date.indexOf("/", pos);
        mDay = Integer.parseInt(date.substring(0, pos));
        pos++;
        int od = pos;
        pos = date.indexOf("/", pos);
        mMonth = Integer.parseInt(date.substring(od, pos));
        pos++;
        mYear = Integer.parseInt(date.substring(pos, date.length()));
    }

    private void onClickMethod() {
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (etNumber.getText().length() > 0) {
                        isNotSaved_Num = true;
                    } else {
                        isNotSaved_Num = false;
                    }
                    print(editable.toString());
                    if (etNumber.length() > 7) {

                        if (etNumber.getText().charAt(etNumber.length() - 1) == ',') {
                            if (etNumber.getText().charAt(etNumber.length() - 2) == ',')
                                etNumber.setText(etNumber.getText().toString().replace(",,", ","));
                            etNumber.setSelection(etNumber.getText().length());
                            return;
                        } else {
                            setNameMessage(etNumber.getText().toString());

                        }
                    } else {
                        if (etNumber.length() > 0) {
                            if (etNumber.getText().charAt(0) == ',') {
                                etNumber.setText("");
                            }
                        }
                        tvNameShow.setText("");
                        tvNameShow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                    }
                    layoutVisible();
                } catch (Exception e) {
                    excMess(context, "afterTextChanged", e);
                }
            }
        });
        swiStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isNoramalSMS) {
                    if (b) {
                        if (db_setMessages.getMSMsendStatus(vNo) == 9) {
                            resendMessAlert();
                        }
                    } else {
                        if (isSendSMSWorking(context, vNo)) {
                            cancelSendingSMS(context, vNo);
                            db_setMessages.updateSMSsendStatus(vNo, 2);
                        }
                    }

                } else if (isGroupUI) {
                    if (b) {
                        if (db_groups.getGStatus(gId) == 9) {
                            resendMessAlert();
                        }
                    } else {
                        if (isSendSMSWorking(context, GROUP_REQUEST_CODE + gId)) {
                            cancelSendingSMS(context, GROUP_REQUEST_CODE + gId);
                            db_groups.updateGStatus(gId, 2);
                        }
                    }
                }
            }
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etMessage.getText().length() > 0) {
                    isNotSaved_Mess = true;
                    layoutVisible();
                } else {
                    layoutVisiblety(View.INVISIBLE);
                    isNotSaved_Mess = false;
                }
            }
        });
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerData();
            }
        });
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerTime();
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerData();
            }
        });
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerTime();
            }
        });
        saveMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSMS();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNoramalSMS) {
                    if (db_setMessages.deleteMessage(vNo)) {
                        toastMess(context, "Deleted Done", false);
                    }
                } else if (isGroupUI) {
                    db_groups.deleteGMessage(gId);
                    toastMess(context, "Deleted Done", false);
                }
                meClose();
            }
        });

        btnAddNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });
        rgSIM_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String id = "0";

                rbSIM_default = findViewById(i);
                String rbId = rbSIM_default.getText() + "";
                print("rb Id ", "" + rbId);
                if (rbId.equalsIgnoreCase(rbSIM_1.getText().toString())) {
                    id = "1";
                } else if (rbId.equalsIgnoreCase(rbSIM_2.getText().toString())) {
                    id = "2";
                } else {
                    id = "0";
                }
                if (id.equalsIgnoreCase(vSIM_type)) {
                    isNotSaved_SIM = true;
                } else {
                    isNotSaved_SIM = false;
                }

            }
        });

    }

    private void setNameMessage(String number) {
        tvNameShow.setTextSize(15);
        tvNameShow.setPadding(5, 0, 5, 0);
        //  tvNameShow.getLayout().getHeight()
        tvNameShow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvNameShow.setText(Html.fromHtml(replacePhone2Name(context, number)));
    }

    private void saveSMS() {
        try {
            if (isNoramalSMS || isNewMessage) {
                print("saveMessage Normal SMS Called");
                if (etNumber.length() < 5) {
                    etNumber.requestFocus();
                    toastMess(context, "Enter valid number ", true);
                    return;
                }
                if (validMessage()) {
                    print("VALID SMS", "Normal SMS Valid Done");
                    if (tvMsgShow.getText().toString().equalsIgnoreCase("NEW")) {

                        if (db_setMessages.setNewMessage(vNumber, vMessage, vDate, vTime, vSIM_type, vRepeatSMS)) {
                            print("New Message set");
                            toastMess(context, "Saved", false);
                        } else {
                            print("Message not set");
                            toastMess(context, "Not Saved", false);
                            return;
                        }
                        int select = db_setMessages.getMaxIdNo();
                        if (select != -1) {
                            vNo = select;
                        } else {
                            print("ERROR Id not Valid", "In New Message");
                            return;
                        }
                    } else {
                        if (db_setMessages.updateNewMessage(vNo + "", vNumber, vMessage, vDate, vTime, vSIM_type, vRepeatSMS)) {
                            toastMess(context, "Updated", false);
                        } else {
                            toastMess(context, "Not Updated", false);
                            return;
                        }
                    }
                    print("Message Id is " + vNo);
                    db_setMessages.updateSMSsendStatus(vNo, 2);
                    if (swiStatus.isChecked()) {
                        onMessageSend();
                    } else {
                        meClose();
                    }
                }
            } else if (isGroupUI) {
                if (validMessage()) {
                    print("VALID SMS", "Group SMS Valid Done");

                    if (tvMsgShow.getText().toString().equalsIgnoreCase("Group SMS")) {
                        if (db_groups.addGMessage(vMessage, vDate, vTime, gpId + "", vSIM_type, vRepeatSMS)) {
                            toastMess(context, "Successfully Saved", false);
                        } else {
                            toastMess(context, "Not save", false);
                            return;
                        }
                        gId = db_groups.getMaxIdNo();
                    } else if (tvMsgShow.getText().toString().equalsIgnoreCase("Modify")) {
                        if (db_groups.updateGMessage(gId, vMessage, vDate, vTime, gpId + "", vSIM_type, vRepeatSMS)) {
                            toastMess(context, "Successfully Updated", false);
                        } else {
                            toastMess(context, "Not Updated", false);
                            return;
                        }
                    }
                    print("GMessage Id is " + gId);
                    db_groups.updateGStatus(gId, 2);
                    if (swiStatus.isChecked()) {
                        onMessageSend();
                    }
                    meClose();
                }
            }

        } catch (Exception e) {
            excMess(context, "save New SMS", e);
        }
    }

    private void meClose() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            print("TAG", "The interstitial wasn't loaded yet.");
        }
        print("finish", "New Message");
        super.finish();
        finish();
    }

    private boolean validMessage() {

        if (etMessage.length() == 0) {
            etMessage.requestFocus();
            toastMess(context, "Enter Message", true);
            return false;
        }

        if (etDate.length() < 5) {
            toastMess(context, "Select Date", true);
            pickerData();
            btnSelectDate.requestFocus();
            return false;
        }
        if (etTime.length() < 3 || (!isFutureTime)) {
            toastMess(context, "Select Time", true);
            pickerTime();
            btnSelectTime.requestFocus();
            return false;
        }
        long timeInMillis = getTimeInMillis();
        long timeDifference = getTimeDefference(timeInMillis);
        print("TimeDifference", "" + timeDifference);
        if (timeDifference < 5000) {
            toastMess(context, "Please Select Correct Time", true);
            validDateTime();
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            vSIM_type = getSelectedSIMid();
            print("Swi is", "" + vSIM_type);
        } else {
            vSIM_type = "0";
        }
        vNumber = etNumber.getText().toString();
        vMessage = etMessage.getText().toString();
        vRepeatSMS = spiRepeatSMS.getSelectedItemId() + "";
        return true;
    }

    private String getSelectedSIMid() {
        String id = "0";
        rbSIM_default = findViewById(rgSIM_type.getCheckedRadioButtonId());

        String rbId = rbSIM_default.getText() + "";
        print("rb Id ", "" + rbId);
        if (rbId.equalsIgnoreCase(rbSIM_1.getText().toString())) {
            id = "1";
        } else if (rbId.equalsIgnoreCase(rbSIM_2.getText().toString())) {
            id = "2";
        } else {
            id = "0";
        }
        print("Selected SIM", "" + id);
        return id;
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (1):
                if (data != null) {
                    Cursor cursor = null;
                    try {
                        String phoneNo = null;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        phoneNo = cursor.getString(phoneIndex);
                        name = cursor.getString(nameIndex);
                        print("Name and Contact number is", name + "," + phoneNo);
                        setNumber(name, phoneNo);

                    } catch (Exception e) {
                        excMess(context, "pickNumber", e);
                    }
                    break;
                }
            default:

                break;
        }
    }

    private void setNumber(String name, String cNumber) {
        String number = "";
        cNumber = getTruePhoneNumber(cNumber);
        int res = etNumber.getText().toString().indexOf(cNumber);
        if (etNumber.getText().toString().indexOf(cNumber) >= 0) {
            toastMess(context, "Already Select" + (res), false);
            return;
        } else {
            if (etNumber.length() > 0) {
                number = etNumber.getText().toString() + "," + cNumber;
            } else {
                number = cNumber;
            }
            etNumber.setText(number);
            setNameMessage(number);
        }
    }

    private void pickerTime() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        print("Time", "T " + hourOfDay + " : " + minute);
                        vTime = hourOfDay + ":" + minute;
                        etTime.setText(db_settings.is24HourView() ? vTime : getTimeIn12hour(vTime));
                        isNotSaved_Time = true;
                        mHour = hourOfDay;
                        mMinute = minute;
                        validDateTime();
                        layoutVisible();
                    }
                }, mHour, mMinute, db_settings.is24HourView());
        timePickerDialog.show();

    }

    private void validDateTime() {
        try {
            String newDate = vDate;
            String newTime = vTime;
            int curhours = 0, curminutes = 0, newTpos = 0, newDpos = 0, newhours = 0, newminutes = 0, curDD = 0, curMM = 0, curYYYY = 0, newDD = 0, newMM = 0, newYYYY = 0;
            String dateMessage = "Date", timeMessage = "Time";
            Calendar currentDate = Calendar.getInstance();
            curYYYY = currentDate.get(Calendar.YEAR);
            curMM = currentDate.get(Calendar.MONTH);
            curDD = currentDate.get(Calendar.DAY_OF_MONTH);
            curhours = currentDate.get(Calendar.HOUR_OF_DAY);
            curminutes = currentDate.get(Calendar.MINUTE);
            if (vTime != null) {
                if (vTime.length() > 3) {
                    newTpos = newTime.indexOf(":");
                    newhours = Integer.parseInt(newTime.substring(0, newTpos));
                    newminutes = Integer.parseInt(newTime.substring(++newTpos, newTime.length()));
                }
            }
            if (vDate != null) {
                if (vDate.length() > 3) {
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
            Calendar validDateCurrent = Calendar.getInstance();
            Calendar validDateNew = Calendar.getInstance();
            validDateCurrent.set(curYYYY, curMM, curDD, curhours, curminutes);
            validDateNew.set(newYYYY, newMM, newDD, newhours, newminutes);
            long timeDifference = validDateNew.getTimeInMillis() - validDateCurrent.getTimeInMillis();
            if (timeDifference <= 5000) {
                isFutureTime = false;
                timeMessage = ("<font color=#aeaeae>Time </font><font color=#6bffd900 >Choose future TIME</font>");
                etTime.setTextColor(Color.RED);
                if (timeDifference < -86400000) {
                    dateMessage = ("<font color=#aeaeae>Date </font><font color=#6bffd900>Choose future DATE</font>");
                    etDate.setTextColor(Color.RED);
                }
                tvMess_sendNow.setVisibility(View.VISIBLE);
                tvMess_sendNow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tvMess_sendNow.setTextColor(Color.RED);
                tvMess_sendNow.setText("Message will be send now");
                print("time Difference", "" + timeDifference);
            } else {
                isFutureTime = true;
                tvMess_sendNow.setVisibility(View.VISIBLE);
                tvMess_sendNow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tvMess_sendNow.setTextColor(Color.GREEN);
                tvMess_sendNow.setText(sendTime(timeDifference));
                etDate.setTextColor(Color.BLACK);
                etTime.setTextColor(Color.BLACK);
                dateMessage = ("<font color=#aeaeae>Date</font>");
                timeMessage = ("<font color=#aeaeae>Time</font>");
            }
            tvDateMessage.setText(Html.fromHtml(dateMessage));
            tvTimeMessage.setText(Html.fromHtml(timeMessage));
        } catch (Exception e) {
            excMess(context, "validDateTime", e);
        }
    }

    private long getTimeInMillis() {
        Calendar calTime = Calendar.getInstance();
        calTime.set(mYear, mMonth, mDay, mHour, mMinute, 0);
        return (calTime.getTimeInMillis());
    }

    private String sendTime(long time) {
        if (time > 60000) {
         /*   Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = calendar.get(Calendar.MINUTE);
            print("RemainTime",  mMonth + "," + mDay + "," + mHour + "," + mMinute);*/
            return ("");
        } else {
            return ("With in a minute");
        }
    }

    private void pickerData() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        vDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                        print("DATE", vDate);
                        etDate.setText(getDateForUser(vDate));
                        isNotSaved_Date = true;
                        mYear = year;
                        mMonth = (monthOfYear);
                        mDay = dayOfMonth;
                        validDateTime();
                        layoutVisible();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }


    public void onMessageSend() {
        try {
            setEnabledMode(true);
            print("onMessageSend");
            if (isNoramalSMS || isNewMessage) {
                print("set SMS true");
                setAlarm("normalSMSsend", vNo);
            } else if (isGroupUI) {
                print("gId", "" + gId);
                print("set Group Message true");
                setAlarm("groupSMSsend", gId);
            }
        } catch (Exception e) {
            excMess(context, "onMessageSend", e);
        }
    }


    private void initialization() {
        swiStatus = (Switch) findViewById(R.id.swiNMSstatus);
        etNumber = (EditText) findViewById(R.id.etNMSnumber);
        tvNumberLabel = (TextView) findViewById(R.id.tvNMSNumberLabel);
        tvMsgShow = (TextView) findViewById(R.id.tvNMSmessageShow);
        tvStatusMessage = (TextView) findViewById(R.id.tvNMSmessageShowStatus);
        etMessage = (EditText) findViewById(R.id.etNMSmessage);
        etDate = (TextView) findViewById(R.id.etNMSdate);
        etTime = (TextView) findViewById(R.id.etNMStime);
        db_setMessages = new DB_SetMessages(context);
        db_settings = new DB_Settings(context);
        db_groups = new DB_Groups(context);
        btnSelectDate = (Button) findViewById(R.id.btnNSMDateSelect);
        btnSelectTime = (Button) findViewById(R.id.btnNSMTimeSelect);
        btnAddNumber = (ImageView) findViewById(R.id.btnNMSaddNumber);
        saveMessage = (TextView) findViewById(R.id.btnNSMdone);
        tvNameShow = (TextView) findViewById(R.id.tvNMSnameShow);
        btnDelete = (TextView) findViewById(R.id.btnNSMDelete);
        tvDateMessage = (TextView) findViewById(R.id.tvNMSdateMessage);
        tvTimeMessage = (TextView) findViewById(R.id.tvNMStimeMessage);
        tvMess_sendNow = (TextView) findViewById(R.id.tvNMSmessage_sendNow);
        setDeleteBTN(false);
        c = Calendar.getInstance();
        etDate.setFocusable(false);
        etTime.setFocusable(false);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        rgSIM_type = (RadioGroup) findViewById(R.id.NMS_rg_SIM_radioGroup);
        rbSIM_1 = (RadioButton) findViewById(R.id.NMS_rb_SIM_1);
        rbSIM_2 = (RadioButton) findViewById(R.id.NMS_rb_SIM_2);
        rbSIM_default = (RadioButton) findViewById(R.id.NMS_rb_SIM_default);
        spiRepeatSMS = (Spinner) findViewById(R.id.NMS_spi_SMSRepeat);
        rbSIM_1.setVisibility(View.INVISIBLE);
        rbSIM_2.setVisibility(View.INVISIBLE);
        rbSIM_default.setVisibility(View.INVISIBLE);
        ll_lay_SIM_layout = (LinearLayout) findViewById(R.id.NMS_lay_SIM_layout);
        lay_date_picker = (LinearLayout) findViewById(R.id.NMS_lay_date_picker);
        lay_time_picker = (LinearLayout) findViewById(R.id.NMS_lay_time_picker);
        lay_SMS_repeat = (LinearLayout) findViewById(R.id.NMS_lay_SMS_repeat);
        lay_saveDone = (LinearLayout) findViewById(R.id.NMS_lay_saveDone);
        layoutVisiblety(View.INVISIBLE);
        ll_lay_SIM_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        setRepeatSMS();
    }

    private void layoutVisiblety(int visiblity) {
        lay_date_picker.setVisibility(visiblity);
        lay_time_picker.setVisibility(visiblity);
        ll_lay_SIM_layout.setVisibility(visiblity);
        lay_SMS_repeat.setVisibility(visiblity);
        lay_saveDone.setVisibility(visiblity);

    }

    private void layoutVisible() {
        if (etMessage.length() > 0) {
            lay_date_picker.setVisibility(View.VISIBLE);
            if (etDate.length() > 3) {
                lay_time_picker.setVisibility(View.VISIBLE);
                if (etTime.length() > 2) {
                    lay_SMS_repeat.setVisibility(View.VISIBLE);
                    if (etMessage.length() > 0 && isFutureTime) {
                        lay_saveDone.setVisibility(View.VISIBLE);
                        getSIMdetail();
                    } else {
                        lay_saveDone.setVisibility(View.INVISIBLE);

                    }
                }
            }
        } else {
            layoutVisiblety(View.INVISIBLE);
        }
    }

    public void setDeleteBTN(boolean status) {
        btnDelete.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        btnDelete.setEnabled(status);
    }

    private long getRepeatTime(int id) {
        long repeatTime = 0;
        switch (db_setMessages.getRepeatTimeId(id)) {
            case 0:
                repeatTime = 0;
            case 1:
                repeatTime = 86400000;
                break;
            case 2:
                repeatTime = 604800000;
                break;
            case 3:
                repeatTime = 2592000000L;
                break;
            case 4:
                repeatTime = 31449600000L;
                break;
            default:
                repeatTime = 0;
                break;
        }
        return repeatTime;
    }

    public void setAlarm(String type, int id) {
        long timeInMillis = getTimeInMillis();

        print("SMS Called");
        if (type.equalsIgnoreCase("normalSMSsend")) {
            Intent intent = new Intent(context, MessageAutoSendReceiver.class);
            intent.putExtra("mSmsIdNo", id);
            intent.setAction(MessageAutoSendReceiver.ACTION_ALARM_RECEIVER);
            long repeatTime = getRepeatTime(db_setMessages.getRepeatTimeId(id));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            if (db_setMessages.getRepeatTimeId(id) == 0) {
                print("AlarmType", "Dose not repeat");
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                print("Alarm Type", "Repeat after " + repeatTime);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, repeatTime, pendingIntent);
            }
            db_setMessages.updateSMSsendStatus(id, 3);
            print("Normal SMS Active ***********");

        } else if (type.equalsIgnoreCase("groupSMSsend")) {
            Intent intent = new Intent(context, MessageAutoSendReceiver.class);
            intent.putExtra("mGroupSMSgId", id);
            intent.putExtra("mGroupSMSgpId", gpId);
            intent.setAction(MessageAutoSendReceiver.ACTION_ALARM_RECEIVER);
            long repeatTime = getRepeatTime(db_setMessages.getRepeatTimeId(id));
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            print("requestID", "" + GROUP_REQUEST_CODE + id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, GROUP_REQUEST_CODE + id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (db_groups.getGsmsRepeatTime(gId) == 0) {
                print("AlarmType", "Dose not repeat");
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                print("Alarm Type", "Repeat after " + repeatTime);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, repeatTime, pendingIntent);
            }
            db_groups.updateGStatus(gId, 3);
            print("Group SMS Active ***");
        }
        meClose();
    }

    @Override
    public void onBackPressed() {
        if (isNotSaved_Num || isNotSaved_Mess || isNotSaved_Date || isNotSaved_Time || isNotSaved_RPT || isNotSaved_SIM) {
            messAlert(context, "Are you sure?", "Do you want to save the changes to message?");
            return;
        } else {
            meClose();
        }
    }

    public void messAlert(final Context context, String title, String message) {
        try {

            TextView tvHeader, tvMessage;
            LayoutInflater inflater;
            View layout;
            ImageView imgIcon;
            Button btnOK, btnCancel, btnClose;
            dialog = new AlertDialog.Builder(context);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.mess_alert, null);
            tvHeader = (TextView) layout.findViewById(R.id.tvMA_header);
            tvMessage = (TextView) layout.findViewById(R.id.tvMA_message);
            imgIcon = (ImageView) layout.findViewById(R.id.imgMA_icon);
            btnOK = (Button) layout.findViewById(R.id.btnMA_OK);
            btnClose = (Button) layout.findViewById(R.id.btnMA_Close);
            btnCancel = (Button) layout.findViewById(R.id.btnMA_cancel);
            btnClose.setVisibility(View.VISIBLE);
            btnClose.setText("Close");
            btnOK.setText("Save");
            Dial = dialog.create();
            Dial.setCancelable(false);
            Dial.setView(layout);
            tvHeader.setText(title);
            tvMessage.setText(message);
            imgIcon.setImageResource(R.drawable.ic_info_message);

            btnCancel.setVisibility(View.VISIBLE);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Dial != null) {
                        Dial.dismiss();
                    }
                    meClose();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Dial != null) {
                        Dial.dismiss();
                    }
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveSMS();
                    if (Dial != null) {
                        Dial.dismiss();
                    }
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "Err In MSG", e);
        }
    }

    private void numberInput(String lable, boolean visible) {
        if (visible) {
            etNumber.setVisibility(View.VISIBLE);
            btnAddNumber.setVisibility(View.VISIBLE);
            tvNumberLabel.setVisibility(View.VISIBLE);
            etNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvNumberLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnAddNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f));
            setMessageNumberLabel("");
        } else {
            etNumber.setVisibility(View.INVISIBLE);
            btnAddNumber.setVisibility(View.INVISIBLE);
            tvNumberLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            tvNumberLabel.setVisibility(View.INVISIBLE);
            etNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            btnAddNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0));
            setMessageNumberLabel("");
        }
    }

    private void resendMessAlert() {
        try {
            TextView tvHeader, tvMessage;
            LayoutInflater inflater;
            View layout;
            ImageView imgIcon;
            Button btnOK, btnCancel, btnClose;
            dialog = new AlertDialog.Builder(context);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.mess_alert, null);
            tvHeader = (TextView) layout.findViewById(R.id.tvMA_header);
            tvMessage = (TextView) layout.findViewById(R.id.tvMA_message);
            imgIcon = (ImageView) layout.findViewById(R.id.imgMA_icon);
            btnOK = (Button) layout.findViewById(R.id.btnMA_OK);
            btnClose = (Button) layout.findViewById(R.id.btnMA_Close);
            btnCancel = (Button) layout.findViewById(R.id.btnMA_cancel);
            btnClose.setVisibility(View.INVISIBLE);
            btnClose.setText("NO");
            btnOK.setText("YES");
            Dial = dialog.create();
            Dial.setCancelable(false);
            Dial.setView(layout);
            tvHeader.setText("Are you Sure ?");
            tvMessage.setText("Do you want to send message again ?");

            imgIcon.setImageResource(R.drawable.ic_info_message);

            btnCancel.setVisibility(View.VISIBLE);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Dial != null) {
                        swiStatus.setChecked(false);
                        setEnabledMode(false);
                        Dial.dismiss();
                    }
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveSMS();
                    if (Dial != null) {
                        swiStatus.setChecked(true);
                        setEnabledMode(true);
                        Dial.dismiss();
                    }
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "resendMessAlert", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tvMsgShow.setText("NEW");
                    setEnabledMode(true);
                    // contacts-related task you need to do.
                } else {
                    tvMsgShow.setText("Permission Denied");
                    setEnabledMode(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
