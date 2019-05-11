package com.cssoftwaretech.smm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import com.cssoftwaretech.smm.database.DB_Groups;
import com.cssoftwaretech.smm.database.DB_SetMessages;
import com.cssoftwaretech.smm.database.DB_Settings;
import com.cssoftwaretech.smm.database.DB_sentSMS;

import java.util.Calendar;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.showNotification;
import static com.cssoftwaretech.smm.Settings.getSysCurrentDateDDMMYYYY;
import static com.cssoftwaretech.smm.Settings.getSysCurrentTimeHHMM;

public class MessageAutoSendReceiver extends BroadcastReceiver {

    private final int GROUP_REQUEST_CODE = 1000;
    Context context;
    DB_SetMessages db_setMessages;
    DB_Groups db_groups;
    DB_Settings db_settings;
    DB_sentSMS db_sentSMS;
    public static final String TAG = MessageAutoSendReceiver.class.getSimpleName();
    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
    private Calendar c = Calendar.getInstance();
    private  SmsManager sms;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context = context;
            if (intent != null)
                if (ACTION_ALARM_RECEIVER.equals(intent.getAction())) {
                    Log.d(TAG, new Exception().getStackTrace()[0].getMethodName() + " " + c.getTime());
                    print("Alarm Activated...");
                }

            print("onReceived Called");
            db_settings = new DB_Settings(context);
            db_sentSMS = new DB_sentSMS(context);
            if (intent == null) {
                print("ID Not", "ID Not Received ");
            } else {
                int smsIdNo = intent.getIntExtra("mSmsIdNo", -1);
                int gId = intent.getIntExtra("mGroupSMSgId", -1);
                int gpId = intent.getIntExtra("mGroupSMSgpId", -1);
                if (smsIdNo != -1) {
                    print("Sms ID ", "Id No " + smsIdNo);
                    db_setMessages = new DB_SetMessages(context);
                    Cursor smsData = db_setMessages.getMessagesByNo(smsIdNo);
                    if (smsData.getCount() < 1) {
                        print("NOT", "SMS not Received");
                        //  messAlert(context, "NOT F", "Message Id not Recived", 3);
                    } else {
                        print("SMS Received");
                        smsData.moveToFirst();
                        String mNumber = smsData.getString(1);
                        String mMessage = smsData.getString(2);
                        // messAlert(context, "DATA", "No " + mNumber + " " + "Me " + mMessage, 3);
                        smsSend(smsIdNo, mNumber, mMessage);
                    }
                } else if (gId != -1) {
                    String gMessage = null;
                    print("gId ", "" + gId);
                    db_groups = new DB_Groups(context);
                    Cursor gMessData = db_groups.getGMessage(gId);
                    if (gMessData.getCount() > 0) {
                        gMessData.moveToFirst();
                        gMessage = gMessData.getString(1);
                    }
                    Cursor gNumberData = db_groups.getAllGNumber(gpId);
                    if (gNumberData.getCount() != 0) {
                        int noOfMembers = gNumberData.getCount();
                        String memberNo[] = new String[noOfMembers];
                        print("SMS Received");
                        for (int i = 0; i < noOfMembers; i++) {
                            gNumberData.moveToNext();
                            memberNo[i] = gNumberData.getString(0);
                            sendGroupSMS(gId, memberNo[i], gMessage);
                        }
                    }
                }
            }
        } catch (Exception e) {
            excMess(context, "onReceived", e);
        }
    }


    private void smsSend(int smsIdNo, String number, String message) {
        try {
            print("smsSend Called");
            print("TOTAL No " + number);
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
                        phoneNo[i] = number.substring(0, number.length());
                        print("End Now");
                    }
                } else {
                    phoneNo[i] = number.substring(0, pos);
                    print(i + " No is " + phoneNo[i]);
                    number = number.substring(++pos, number.length());
                    print("Next No " + number);
                }
            }
            phoneNo[num - 1] = number;
            print(num - 1 + " No is " + phoneNo[num - 1]);
            print("PRINT ARRAY");
            for (i = 0; i < num; i++) {
                print("No " + i, "" + phoneNo[i]);
                sendSMS(smsIdNo, phoneNo[i], message);
            }
            print("Sent on " + number);
        } catch (Exception e) {
            excMess(context, "smsSend", e);
        }
    }

    private void sendSMS(final int smsIdNo, final String phoneNumber, String message) {
        try {
            final int smsIdNum = smsIdNo;
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            print("sendSMS Called");

            PendingIntent sentPI = PendingIntent.getBroadcast(context.getApplicationContext(), smsIdNum,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = null;

            //---when the SMS has been sent---
            context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                           statusUpdateSMS(smsIdNum,4);
                            print("SMS sent");
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            statusUpdateSMS(smsIdNum,5);
                            print("Generic failure");
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            statusUpdateSMS(smsIdNum,6);
                            print("No service");
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            statusUpdateSMS(smsIdNum, 7);
                            print("Null PDU");
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            statusUpdateSMS(smsIdNum, 8);
                            print("Radio off");
                            break;
                    }
                }
            }, new IntentFilter(SENT));
            if (Integer.parseInt(db_settings.getDeliverType()) > 1) {
                deliveredPI = PendingIntent.getBroadcast(context.getApplicationContext(), smsIdNum,
                        new Intent(DELIVERED), 0);
                //---when the SMS has been delivered---
                context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                statusUpdateSMS(smsIdNum, 9);
                                showNotification(smsIdNum, context, phoneNumber, "Delivered");
                                break;
                            case Activity.RESULT_CANCELED:
                                statusUpdateSMS(smsIdNum, 10);
                                showNotification(smsIdNum, context, phoneNumber, "Not Delivered");
                                break;
                        }
                    }
                }, new IntentFilter(DELIVERED));
            }
             sms = SmsManager.getDefault();
            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //     SmsManager.getSmsManagerForSubscriptionId(db_settings.getDefaultSIM_ID())
            //  sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                switch (db_setMessages.getM_SIM_type(smsIdNum)) {
                    case "1":
                        sms.getSmsManagerForSubscriptionId(1).sendTextMessage(phoneNumber, null,
                                message, sentPI, deliveredPI);
                        db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","1");
                        print("SEND By SIM 1", true + "");
                        break;
                    case "2":
                        sms.getSmsManagerForSubscriptionId(2).sendTextMessage(phoneNumber, null,
                                message, sentPI, deliveredPI);
                        db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","2");
                        print("SEND By SIM 2", true + "");
                        break;
                    case "0":
                        String simID = db_settings.getDefaultSIM_ID();
                        if (simID.equalsIgnoreCase("1")) {
                            sms.getSmsManagerForSubscriptionId(1).sendTextMessage(phoneNumber, null,
                                    message, sentPI, deliveredPI);
                            db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","1");
                        } else if (simID.equalsIgnoreCase("2")) {
                            sms.getSmsManagerForSubscriptionId(2).sendTextMessage(phoneNumber, null,
                                    message, sentPI, deliveredPI);
                            db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","2");
                        } else {
                            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                            db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","0");
                            print("SEND By default", true + "");

                        }
                        break;
                    default:
                        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                        db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","0");
                        print("SEND By default", true + "");
                        break;
                }
            } else {
                sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                db_sentSMS.sentMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",smsIdNum+"","0");
                print("SDK >19", true + "");
            }
            // }
        } catch (Exception e) {
            excMess(context, "sendSMS", e);
        }

    }

    private void statusUpdateSMS(int smsIdNum,int status) {
        db_setMessages.updateSMSsendStatus(smsIdNum, status);
        db_sentSMS.updateSendSMSStatus(smsIdNum,status);
    }    private void statusUpdateGpSMS(int gId,int status) {
        db_groups.updateGStatus(gId, status);
        db_sentSMS.updateSendSMSGpStatus(gId,status);
    }

    //
    private void sendGroupSMS(int gId, final String phoneNumber, String message) {
        try {
            final int gIdNum = gId;
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            print("sendSMS Called");

            PendingIntent sentPI = PendingIntent.getBroadcast(context.getApplicationContext(), GROUP_REQUEST_CODE + gIdNum,
                    new Intent(SENT), 0);
            PendingIntent deliveredPI = null;


            //---when the SMS has been sent---
            context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            statusUpdateGpSMS(gIdNum, 4);
                            print("SMS sent");
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            statusUpdateGpSMS(gIdNum, 5);
                            print("Generic failure");
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            statusUpdateGpSMS(gIdNum, 6);
                            print("No service");
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            statusUpdateGpSMS(gIdNum, 7);
                            print("Null PDU");
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            statusUpdateGpSMS(gIdNum, 8);
                            print("Radio off");
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            //---when the SMS has been delivered---
            if (Integer.parseInt(db_settings.getDeliverType()) > 1) {
                deliveredPI = PendingIntent.getBroadcast(context.getApplicationContext(), GROUP_REQUEST_CODE + gIdNum,
                        new Intent(DELIVERED), 0);
                context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                statusUpdateGpSMS(gIdNum, 9);
                                showNotification(gIdNum, context, phoneNumber, "Delivered");
                                print("SMS delivered");
                                break;
                            case Activity.RESULT_CANCELED:
                                statusUpdateGpSMS(gIdNum, 10);
                                showNotification(gIdNum, context, phoneNumber, "Not Delivered");
                                print("SMS not delivered");
                                break;

                        }
                    }
                }, new IntentFilter(DELIVERED));
            }
             sms = SmsManager.getDefault();
         /*   print("MESSAGE", "**********SENT " + phoneNumber + " - " + message + "**********");
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                switch (db_groups.getGsimType(gIdNum)) {
                    case 1:
                        sms.getSmsManagerForSubscriptionId(1).sendTextMessage(phoneNumber, null,
                                message, sentPI, deliveredPI);
                        db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","1");
                        print("SEND By SIM 1", true + "");
                        break;
                    case 2:
                        sms.getSmsManagerForSubscriptionId(2).sendTextMessage(phoneNumber, null,
                                message, sentPI, deliveredPI);
                        db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","2");
                        print("SEND By SIM 2", true + "");
                        break;
                    case 0:
                        String simID = db_settings.getDefaultSIM_ID();
                        if (simID.equalsIgnoreCase("1")) {
                            sms.getSmsManagerForSubscriptionId(1).sendTextMessage(phoneNumber, null,
                                    message, sentPI, deliveredPI);
                            db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","1");
                        } else if (simID.equalsIgnoreCase("2")) {
                            sms.getSmsManagerForSubscriptionId(2).sendTextMessage(phoneNumber, null,
                                    message, sentPI, deliveredPI);
                            db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","2");
                        } else {
                            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                            db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","0");
                            print("SEND By default", true + "");
                        }
                        break;
                    default:
                        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                        db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","0");
                        print("SEND By default", true + "");
                        break;
                }
            } else {
                sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                db_sentSMS.sendGpMessage(phoneNumber,message,getSysCurrentDateDDMMYYYY(),getSysCurrentTimeHHMM(),"4",gId+"","0");
                print("SDK <19", "" + true);
            }
        } catch (Exception e) {
            excMess(context, "sendSMS", e);
        }

    }
}
