package com.cssoftwaretech.smm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;

public class DB_Groups extends DBSQLiteHelper {
   private Context context;
   private static SQLiteDatabase db;

    public DB_Groups(Context context) {
        super(context);
        this.context = context;
        db = this.getWritableDatabase();
    }

    public boolean createGroup(String gpName, String gpSubTitle) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GROUPcol_2, gpName);
            contentValues.put(GROUPcol_3, gpSubTitle);
            long isInserted = db.insert(TAB_GROUP, null, contentValues);
            if (isInserted == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Group", e);
        }
        return false;
    }

    public boolean addMember(int gpId, String gpMemberNo) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GpMemberCol_2, gpMemberNo);
            contentValues.put(GpMemberCol_3, gpId);
            long isInserted = db.insert(TAB_GpMember, null, contentValues);
            if (isInserted == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB GP Member", e);
        }
        return false;
    }

    public boolean updateGroup(int gpId, String gpName, String gpSubTitle) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GROUPcol_1, gpId);
            contentValues.put(GROUPcol_2, gpName);
            contentValues.put(GROUPcol_3, gpSubTitle);
            long isUpdated = db.update(TAB_GROUP, contentValues, GROUPcol_1 + " = ?", new String[]{gpId + ""});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Update Group", e);
        }
        return false;
    }

    public Cursor getAllGroups() {
        Cursor res = db.rawQuery("SELECT " + GROUPcol_1 + "," + GROUPcol_2 + "," + GROUPcol_3 + " FROM " + TAB_GROUP + " order by Id desc", null);
        return res;
    }

    public Cursor getGroupById(int gpId) {
        String Command = "SELECT " + GROUPcol_1 + "," + GROUPcol_2 + "," + GROUPcol_3 + " FROM " + TAB_GROUP + " WHERE " + GROUPcol_1 + "=" + gpId;
        Cursor res = db.rawQuery(Command, null);
        return res;
    }

    public boolean isAlreadyMember(int gpId, String mNo) {
        String Command = "SELECT " + GpMemberCol_2 + "," + GpMemberCol_3 + " FROM " + TAB_GpMember + " WHERE " + GpMemberCol_2 + "='" + mNo + "' AND " + GpMemberCol_3 + "=" + gpId;
        print("CMD " + Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() != 0) {
            return true;
        } else {
            return false;

        }
    }

    public Cursor getAllMemberByGp(int gpId) {
        Cursor res = db.rawQuery("SELECT " + GpMemberCol_1 + "," + GpMemberCol_2 + " FROM " + TAB_GpMember + " WHERE " + GpMemberCol_3 + "=" + gpId, null);
        return res;
    }

    public boolean removeMember(int memberId) {
        if (db.delete(TAB_GpMember, GpMemberCol_1 + "=" + memberId, null) == -1) {
            print("removeMember", memberId + "");
            return false;
        } else {
            return true;
        }
    }

    public boolean isMemberRemove(int memberId) {
        String Command = "SELECT " + GpMemberCol_1 + " FROM " + TAB_GpMember + " WHERE " + GpMemberCol_1 + "='" + memberId + "'";
        print("isMemberRemove" + Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            return true;
        } else {
            return false;

        }
    }

    public boolean removeGroup(int gpId) {
        db.delete(TAB_GROUP, GROUPcol_1 + "=" + gpId, null);
        if (db.delete(TAB_GpMember, GpMemberCol_3 + "=" + gpId, null) == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean isGroupRemoved(int gpId) {
        String Command = "SELECT " + GROUPcol_1 + " FROM " + TAB_GROUP + " WHERE " + GROUPcol_1 + "='" + gpId + "'";
        print("isGroupRemoved" + Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            return true;
        } else {
            return false;

        }
    }
    public boolean isGroupMember(int gpId) {
        String Command = "SELECT " + GpMemberCol_1 + " FROM " + TAB_GpMember + " WHERE " + GpMemberCol_3 + "='" + gpId + "'";
        print("isGroupMember" + Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            return false;
        } else {
            return true;

        }
    }
private boolean isNoGMessage(){
    try {
        String Command = "SELECT " + GpMemberCol_1 + " FROM " + TAB_GpMember;
        print("isNoGMessage" + Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    } catch (Exception e) {
        excMess(context, "isNoGMessage", e);
    }
    return false;
}
    //Message
    public boolean addGMessage(String gMessage, String gDate, String gTime, String gpId,String simType,String repeatSMS) {
        try {
            ContentValues contentValues = new ContentValues();
       //     if(isNoGMessage()){
            contentValues.put(GpMessageCol_2, gMessage);
            contentValues.put(GpMessageCol_3, gDate);
            contentValues.put(GpMessageCol_4, gTime);
            contentValues.put(GpMessageCol_5, "1");
            contentValues.put(GpMessageCol_6, gpId);
            contentValues.put(GpMessageCol_7, simType);
            contentValues.put(GpMessageCol_8, repeatSMS);
            long isInserted = db.insert(TAB_GpMessage, null, contentValues);
            if (isInserted == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB GP gMessage", e);
        }
        return false;
    }

    public boolean updateGMessage(int gId, String gMessage, String gDate, String gTime, String gpId,String simType,String repeatType) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GpMessageCol_1, gId);
            contentValues.put(GpMessageCol_2, gMessage);
            contentValues.put(GpMessageCol_3, gDate);
            contentValues.put(GpMessageCol_4, gTime);
            contentValues.put(GpMessageCol_5, "0");
            contentValues.put(GpMessageCol_6, gpId);
            contentValues.put(GpMessageCol_7, simType);
            contentValues.put(GpMessageCol_8, repeatType);
            long isUpdated = db.update(TAB_GpMessage, contentValues, GpMessageCol_1 + " = ?", new String[]{gId + ""});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Update Status", e);
        }
        return false;
    }

    public boolean updateGStatus(int gId, int gStatus) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GpMessageCol_5, gStatus+"");
            long isUpdated = db.update(TAB_GpMessage, contentValues, GpMessageCol_1 + " = ?", new String[]{gId + ""});
            if (isUpdated == -1)
                return false;
            else
                return true;
        } catch (Exception e) {
            excMess(context, "DB Update Status", e);
        }
        return false;
    }

    public Cursor getGMessage(int gId) {
        Cursor res = db.rawQuery("SELECT " + GpMessageCol_1 + "," + GpMessageCol_2 + "," + GpMessageCol_3 + "," + GpMessageCol_4 + "," + GpMessageCol_5 + "," + GpMessageCol_7 + "," + GpMessageCol_8 + " FROM " + TAB_GpMessage + " WHERE " + GpMessageCol_1 + "=" + gId, null);
        return res;
    }

    public Cursor getAllGMessage(int gpId) {
        Cursor res = db.rawQuery("SELECT " + GpMessageCol_1 + "," + GpMessageCol_2 + "," + GpMessageCol_3 + "," + GpMessageCol_4 + "," + GpMessageCol_5 + " FROM " + TAB_GpMessage + " WHERE " + GpMessageCol_6 + "=" + gpId, null);
        return res;
    }

    public Cursor getAllGNumber(int gpId) {
        Cursor res = db.rawQuery("SELECT " + GpMemberCol_2 + " FROM " + TAB_GpMember + " WHERE " + GpMemberCol_3 + "=" + gpId, null);
        return res;
    }

    public boolean deleteGMessage(int gId) {
        if (db.delete(TAB_GpMessage, GpMessageCol_1 + "=" + gId, null) == -1)
            return false;
        else
            return true;
    }

    public int getGStatus(int gId) {
        String Command = "SELECT " + GpMessageCol_5 + " FROM " + TAB_GpMessage + " WHERE " + GpMessageCol_1 + "=" + gId;
        print("getGsmsStatus", Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            print("Empty", "No Record Found");
            return -1;
        } else {
            res.moveToLast();
            int status = Integer.parseInt(res.getString(0));
            return status;
        }
    }
    public int getGsmsRepeatTime(int gId) {
        String Command = "SELECT " + GpMessageCol_8 + " FROM " + TAB_GpMessage + " WHERE " + GpMessageCol_1 + "=" + gId;
        print("getGsmsRepeatTime", Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            print("Empty", "No Record Found");
            return -1;
        } else {
            res.moveToLast();
            int repeatTime = Integer.parseInt(res.getString(0));
            return repeatTime;
        }
    }
    public int getGsimType(int gId) {
        String Command = "SELECT " + GpMessageCol_7 + " FROM " + TAB_GpMessage + " WHERE " + GpMessageCol_1 + "=" + gId;
        print("getGsimType", Command);
        Cursor res = db.rawQuery(Command, null);
        if (res.getCount() == 0) {
            print("Empty", "No Record Found");
            return -1;
        } else {
            res.moveToLast();
            int simId = Integer.parseInt(res.getString(0));
            return simId;
        }
    }

    public int getMaxIdNo() {
        Cursor res = db.rawQuery("SELECT " + GpMessageCol_1 + " FROM " + TAB_GpMessage, null);
        if (res.getCount() == 0) {
            print("Error", "No Data Select In GetMaxIdNo");
            return -1;
        } else {
            res.moveToLast();
            int maxIdNo = Integer.parseInt(res.getString(0));
            return maxIdNo;
        }
    }
}
