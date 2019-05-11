package com.cssoftwaretech.smm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.cssoftwaretech.smm.MessNotice.error;
import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;

public class DB_Settings extends DBSQLiteHelper {
    private static SQLiteDatabase db;
    private Context context;

    public DB_Settings(Context context) {
        super(context);
        this.context = context;
        db = getWritableDatabase();
    }

    public boolean insertSettings() {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_1, 1);
            contentValues.put(settCol_2, false);
            contentValues.put(settCol_3, true);
            contentValues.put(settCol_4, true);
            contentValues.put(settCol_5, false);
            contentValues.put(settCol_6, false);
            contentValues.put(settCol_7, "2");
            contentValues.put(settCol_8, "0");
            long isInserted = db.insert(TABLE_Setting, null, contentValues);
            if (isInserted == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting", e);
        }
        return false;
    }

    public boolean updateClockType(boolean clockType) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_2, clockType);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting", e);
        }
        return false;
    }

    public boolean updateSmsAlwaysON(boolean smsAlwaysON) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_3, smsAlwaysON);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting", e);
        }
        return false;
    }

    public boolean updateCurrentDateTimeSet(boolean currentDateTimeSet) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_4, currentDateTimeSet);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting", e);
        }
        return false;
    }

    public boolean updateShowFrontMessage(boolean showFrontMessage) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_5, showFrontMessage);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting", e);
        }
        return false;
    }

    public boolean updateShowRemainingTime(boolean showRemainingTime) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_6, showRemainingTime);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting", e);
        }
        return false;
    }

    public boolean updateDeliverType(String type) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_7, type);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Setting Deliver Type", e);
        }
        return false;
    }

    public boolean updateDefaultSIM(String SubscriptionId) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(settCol_8, SubscriptionId);
            long isUpdated = db.update(TABLE_Setting, contentValues, settCol_1 + " = ?", new String[]{"1"});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB DefaultSIM", e);
        }
        return false;
    }

    public String getDefaultSIM_ID() {
        print("getDefaultSIM_ID Called");
        Cursor res = db.rawQuery("SELECT " + settCol_8 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        if (res.getCount() == 0) {
            return "-1";
        } else {
            res.moveToFirst();
            return (res.getString(0));
        }
    }

    public String getDeliverType() {
        print("isEmptyDataBase Called");
        Cursor res = db.rawQuery("SELECT " + settCol_7 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        if (res.getCount() == 0) {
            return "-1";
        } else {
            res.moveToFirst();
            return (res.getString(0));
        }
    }

    public boolean isEmptyDataBase() {
        print("isEmptyDataBase Called");
        Cursor res = db.rawQuery("SELECT " + settCol_1 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        if (res.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean is24HourView() {
        print("is24Clock Called");
        Cursor res = db.rawQuery("SELECT " + settCol_2 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        res.moveToFirst();
        if (res.getString(0).equalsIgnoreCase("TRUE") || res.getString(0).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSmsAlwaysON() {
        print("smsAlwaysON Called");
        Cursor res = db.rawQuery("SELECT " + settCol_3 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        res.moveToFirst();
        if (res.getString(0).equalsIgnoreCase("TRUE") || res.getString(0).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCurrentDateTimeSet() {
        print("Current Date Time Set Called");
        Cursor res = db.rawQuery("SELECT " + settCol_4 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        if (res.getCount() == 0) {
            error("No Data");
            return false;
        }
        res.moveToFirst();
        if (res.getString(0).equalsIgnoreCase("TRUE") || res.getString(0).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isShowFrontMessage() {
        print("Show Front Message");
        Cursor res = db.rawQuery("SELECT " + settCol_5 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        if (res.getCount() == 0) {
            error("No Data");
            return false;
        }
        res.moveToFirst();
        if (res.getString(0).equalsIgnoreCase("TRUE") || res.getString(0).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isShowRemainingTime() {
        print("Show Remaining Time");
        Cursor res = db.rawQuery("SELECT " + settCol_6 + " FROM " + TABLE_Setting + " WHERE " + settCol_1 + "=" + 1, null);
        if (res.getCount() == 0) {
            error("No Data");
            return false;
        }
        res.moveToFirst();
        if (res.getString(0).equalsIgnoreCase("TRUE") || res.getString(0).equals("1")) {
            return true;
        } else {
            return false;
        }
    }
}
