package com.cssoftwaretech.smm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSQLiteHelper extends SQLiteOpenHelper {


    public static String DB_NAME = "MessagesDB";

    public static String TABLE_NAME = "setMessages";
    public static String sMesCol_1 = "id";
    public static String sMesCol_2 = "number";
    public static String sMesCol_3 = "message";
    public static String sMesCol_4 = "s_date";
    public static String sMesCol_5 = "s_time";
    public static String sMesCol_6 = "s_SIM_type";
    public static String sMesCol_7 = "s_send_status";
    public static String sMesCol_8 = "repeatSMS";

    //Table for Settings

    public static String TABLE_Setting = "tabSettings";
    public static String settCol_1 = "id";
    public static String settCol_2 = "clockType";
    public static String settCol_3 = "newMStatus";
    public static String settCol_4 = "curDateTimeSet";
    public static String settCol_5 = "curShowFrontMessage";
    public static String settCol_6 = "curShowRemainingTime";
    public static String settCol_7 = "deliverType";
    public static String settCol_8 = "defaultSIM";

    // Groups
    public static String TAB_GROUP = "tabGROUP";
    public static String GROUPcol_1 = "id";
    public static String GROUPcol_2 = "gpName";
    public static String GROUPcol_3 = "gpSubTitle";
    // MemberId
    public static String TAB_GpMember = "tabGpMember";
    public static String GpMemberCol_1 = "id";
    public static String GpMemberCol_2 = "memberName";
    public static String GpMemberCol_3 = "gpId";
    //Group Messages
    public static String TAB_GpMessage = "tabGpMessages";
    public static String GpMessageCol_1 = "id";
    public static String GpMessageCol_2 = "message";
    public static String GpMessageCol_3 = "date";
    public static String GpMessageCol_4 = "time";
    public static String GpMessageCol_5 = "status";
    public static String GpMessageCol_6 = "gpId";
    public static String GpMessageCol_7 = "simType";
    public static String GpMessageCol_8 = "smsRepeat";
    //Sent SMS
    public static String TAB_sentSMS = "tabSentSMS";
    public static String sentSMSCol_1 = "id";
    public static String sentSMSCol_2 = "phNumber";
    public static String sentSMSCol_3 = "message";
    public static String sentSMSCol_4 = "date";
    public static String sentSMSCol_5 = "time";
    public static String sentSMSCol_6 = "status";
    public static String sentSMSCol_7 = "gpId";
    public static String sentSMSCol_8 = "mId";
    public static String sentSMSCol_9 = "sim";

    public DBSQLiteHelper(Context context) {
        super(context, DB_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String cmd = "CREATE TABLE " + TABLE_NAME + " (" + sMesCol_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + sMesCol_2 + " TEXT ," + sMesCol_3 + " TEXT ," + sMesCol_4 + " TEXT ," + sMesCol_5 + " TEXT," + sMesCol_6 + " TEXT," + sMesCol_7 + " TEXT," + sMesCol_8 + " TEXT)";
        sqLiteDatabase.execSQL(cmd);
        //Setting Table Crate
        String cmdForSett = "CREATE TABLE " + TABLE_Setting + " (" + settCol_1 + " INTEGER PRIMARY KEY ," + settCol_2 + " TEXT," + settCol_3 + " TEXT," + settCol_4 + " TEXT," + settCol_5 + " TEXT," + settCol_6 + " TEXT," + settCol_7 + " TEXT," + settCol_8 + " TEXT)";
        sqLiteDatabase.execSQL(cmdForSett);
        //Group Table Create
        String cmdGpTable = "CREATE TABLE " + TAB_GROUP + " (" + GROUPcol_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + GROUPcol_2 + " TEXT," + GROUPcol_3 + " TEXT)";
        //Member Table
        sqLiteDatabase.execSQL(cmdGpTable);
        String cmdGpMember = "CREATE TABLE " + TAB_GpMember + " (" + GpMemberCol_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + GpMemberCol_2 + " TEXT," + GpMemberCol_3 + " TEXT)";
        sqLiteDatabase.execSQL(cmdGpMember);

        String cmdGpMessage = "CREATE TABLE " + TAB_GpMessage + " (" + GpMessageCol_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + GpMessageCol_2 + " TEXT," + GpMessageCol_3 + " TEXT," + GpMessageCol_4 + " TEXT," + GpMessageCol_5 + " TEXT," + GpMessageCol_6 + " TEXT," + GpMessageCol_7 + " TEXT," + GpMessageCol_8 + " TEXT)";
        sqLiteDatabase.execSQL(cmdGpMessage);
        //sentSMS
        String cmdsentSMSMessage = "CREATE TABLE " + TAB_sentSMS + " (" + sentSMSCol_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + sentSMSCol_2 + " TEXT," + sentSMSCol_3 + " TEXT," + sentSMSCol_4 + " TEXT," + sentSMSCol_5 + " TEXT," + sentSMSCol_6 + " TEXT," + sentSMSCol_7 + " TEXT," + sentSMSCol_8 + " TEXT," + sentSMSCol_9 + " TEXT)";
        sqLiteDatabase.execSQL(cmdsentSMSMessage);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Setting);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_GROUP);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_GpMember);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_GpMessage);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_sentSMS);
        onCreate(sqLiteDatabase);
    }
}
