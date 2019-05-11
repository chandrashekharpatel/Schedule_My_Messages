package com.cssoftwaretech.smm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.cssoftwaretech.smm.MessNotice.excMess;

public class DB_sentSMS extends DBSQLiteHelper {
    private static SQLiteDatabase db;
    private Context context;

    public DB_sentSMS(Context context) {
        super(context);
        db = this.getWritableDatabase();
        this.context = context;
    }

    public boolean sendGpMessage(String ssPhNumber, String ssMessage, String ssDate, String ssTime, String ssStatus, String ssGId, String ssSim) {
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(sentSMSCol_2, ssPhNumber);
            contentValues.put(sentSMSCol_3, ssMessage);
            contentValues.put(sentSMSCol_4, ssDate);
            contentValues.put(sentSMSCol_5, ssTime);
            contentValues.put(sentSMSCol_6, ssStatus);
            contentValues.put(sentSMSCol_7, ssGId + "");
            contentValues.put(sentSMSCol_9, ssSim);
            long isInserted = db.insert(TAB_sentSMS, null, contentValues);

            if (isInserted == -1)
                return false;
            else
                return true;

        } catch (Exception e) {
            excMess(context, "sentGpMessage", e);
        }
        return false;

    }

    public boolean sentMessage(String ssPhNumber, String ssMessage, String ssDate, String ssTime, String ssStatus, String ssMId, String ssSim) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sentSMSCol_2, ssPhNumber);
            contentValues.put(sentSMSCol_3, ssMessage);
            contentValues.put(sentSMSCol_4, ssDate);
            contentValues.put(sentSMSCol_5, ssTime);
            contentValues.put(sentSMSCol_6, ssStatus);
            contentValues.put(sentSMSCol_8, ssMId);
            contentValues.put(sentSMSCol_9, ssSim);
            long isInserted = db.insert(TAB_sentSMS, null, contentValues);

            if (isInserted == -1)
                return false;
            else
                return true;

        } catch (Exception e) {
            excMess(context, "sentMessage", e);
        }
        return false;
    }

    public Cursor getAllSentMessages() {
        try {
            Cursor res = db.rawQuery("SELECT " + sentSMSCol_1 + "," + sentSMSCol_2 + "," + sentSMSCol_3 + "," + sentSMSCol_4 + "," + sentSMSCol_5 + "," + sentSMSCol_6 + "," + sentSMSCol_7 + "," + sentSMSCol_8 + "," + sentSMSCol_9 + " FROM " + TAB_sentSMS, null);
            return res;
        } catch (Exception e) {
            excMess(context, "getAllSentMessages", e);
        }
        return null;
    }

    public boolean updateSendSMSStatus(int mId, int sendStatus) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sentSMSCol_6, sendStatus + "");
            long isUpdated = db.update(TAB_sentSMS, contentValues, sentSMSCol_8 + "=" + mId, null);
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "updateSendSMSStatus", e);
        }
        return false;
    }

    public boolean updateSendSMSGpStatus(int gId, int sendStatus) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sentSMSCol_6, sendStatus + "");
            long isUpdated = db.update(TAB_sentSMS, contentValues, sentSMSCol_7 + "=" + gId, null);
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "updateSendSMSStatus", e);
        }
        return false;
    }

    public boolean deleteSentSMS(String ssId) {
        try {
            if (db.delete(TAB_sentSMS, sentSMSCol_1 + "=" + ssId, null) != -1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            excMess(context, "deleteSentSMS", e);
        }
        return false;
    }
}
