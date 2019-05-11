package com.cssoftwaretech.smm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.cssoftwaretech.smm.MessNotice.error;
import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;

public class DB_SetMessages extends DBSQLiteHelper {

    private static Context context;
    private static SQLiteDatabase db;


    public DB_SetMessages(Context context) {
        super(context);
        this.context = context;
        db = this.getWritableDatabase();
    }

    public boolean setNewMessage(String s_number, String s_message, String s_date, String s_time, String sSIM_Type, String sRepeatSMS) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sMesCol_2, s_number);
            contentValues.put(sMesCol_3, s_message);
            contentValues.put(sMesCol_4, s_date);
            contentValues.put(sMesCol_5, s_time);
            contentValues.put(sMesCol_6, sSIM_Type);
            contentValues.put(sMesCol_7, "1");
            contentValues.put(sMesCol_8, sRepeatSMS);
            print("MessageDT", "" + contentValues);
            long isInserted = db.insert(TABLE_NAME, null, contentValues);

            if (isInserted == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB SetMess", e);
        }
        return false;
    }

    public boolean updateNewMessage(String s_no, String s_number, String s_message, String s_date, String s_time, String s_SIM_Type, String repeatSMS) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sMesCol_1, s_no);
            contentValues.put(sMesCol_2, s_number);
            contentValues.put(sMesCol_3, s_message);
            contentValues.put(sMesCol_4, s_date);
            contentValues.put(sMesCol_5, s_time);
            contentValues.put(sMesCol_6, s_SIM_Type);
            contentValues.put(sMesCol_8, repeatSMS);
            long isUpdated = db.update(TABLE_NAME, contentValues, sMesCol_1 + " = ?", new String[]{s_no});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB SetMess", e);
        }
        return false;
    }

    public boolean updateSMSsendStatus(int idNo, int sendStatus) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sMesCol_7, sendStatus + "");
            long isUpdated = db.update(TABLE_NAME, contentValues, sMesCol_1 + "=" + idNo, null);
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB SetMess", e);
        }
        return false;
    }

    public int getRepeatTimeId(int idNo) {
        try {
            String Command = "SELECT " + sMesCol_8 + " FROM " + TABLE_NAME + " WHERE " + sMesCol_1 + "=" + idNo;
            Cursor res = db.rawQuery(Command, null);
            if (res.getCount() == 0) {
                error("No Data select in sendStatus");
                return -1;
            } else {
                res.moveToFirst();
                return (Integer.parseInt(res.getString(0)));
            }
        } catch (Exception e) {
            excMess(context, "DB SetMess", e);
        }
        return -1;
    }

    public boolean updateSIM_type(int idNo, int sSIM_type) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sMesCol_6, sSIM_type);
            long isUpdated = db.update(TABLE_NAME, contentValues, sMesCol_1 + "=" + idNo, null);
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB SetMess", e);
        }
        return false;
    }

    public Cursor getMessagesByNo(int no) {
        try {
            String Command = "SELECT " + sMesCol_1 + "," + sMesCol_2 + "," + sMesCol_3 + "," + sMesCol_4 + "," + sMesCol_5 + "," + sMesCol_6 + "," + sMesCol_7 + "," + sMesCol_8 + " FROM " + TABLE_NAME + " WHERE " + sMesCol_1 + "=" + no;
            Cursor res = db.rawQuery(Command, null);
            return res;
        } catch (Exception e) {
            excMess(context, "getMessagesByNo", e);
        }
        return null;
    }

    public int getMSMsendStatus(int no) {
        String Command = "SELECT " + sMesCol_7 + " FROM " + TABLE_NAME + " WHERE " + sMesCol_1 + "=" + no;
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            error("No Data select in sendStatus");
            return -1;
        } else {
            res.moveToFirst();
            return (Integer.parseInt(res.getString(0)));
        }
    }

    public String getM_SIM_type(int no) {
        String Command = "SELECT " + sMesCol_6 + " FROM " + TABLE_NAME + " WHERE " + sMesCol_1 + "=" + no;
        print(Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            print("No Data select in SMS Status");
            return "-1";
        } else {
            res.moveToFirst();
            print("Data select in SMS Status");
            return (res.getString(0).toString());
        }
    }

    public Cursor getAllMessages() {
        try {
            Cursor res = db.rawQuery("SELECT " + sMesCol_1 + "," + sMesCol_2 + "," + sMesCol_3 + "," + sMesCol_4 + "," + sMesCol_5 + "," + sMesCol_6 + "," + sMesCol_7 + "," + sMesCol_8 + " FROM " + TABLE_NAME + " order by Id desc", null);
            return res;
        } catch (Exception e) {
            excMess(context, "getAllMessages", e);
        }
        return null;
    }

    public int getMaxIdNo() {
        try {
            Cursor res = db.rawQuery("SELECT " + sMesCol_1 + " FROM " + TABLE_NAME, null);
            if (res.getCount() == 0) {
                error("No Data select");
                return -1;
            }
            res.moveToLast();
            int maxIdNo = Integer.parseInt(res.getString(0));
            return maxIdNo;
        } catch (Exception e) {
            excMess(context, "getAllMessages", e);
        }
        return -1;
    }

    public boolean deleteMessage(int idNo) {
        if (db.delete(TABLE_NAME, sMesCol_1 + "=" + idNo, null) != -1) {
            return true;
        } else {
            return false;
        }
    }
}
