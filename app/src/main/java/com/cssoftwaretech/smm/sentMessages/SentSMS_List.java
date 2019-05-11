package com.cssoftwaretech.smm.sentMessages;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.RecyclerItemClickListener;
import com.cssoftwaretech.smm.database.DB_sentSMS;

import java.util.ArrayList;
import java.util.List;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;

public class SentSMS_List extends android.support.v4.app.Fragment {
    final StringBuilder ssId = new StringBuilder("");
    DB_sentSMS db_sentSMS;
    private View rootView;
    private Context context;
    private List<SentSMS_itemview> listItems;
    private SentSMSadapter SentSMSadapter;
    private RecyclerView rvSnetSMSlist;
    private FloatingActionButton fab_addNewAutoMess;
    private LinearLayout noSMSsent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.activity_sentsms_list, container, false);
            context = getActivity().getBaseContext();
            initialized();
            setSentSMSData();
            onSentSMSAction();
            return rootView;
        } catch (Exception e) {
            excMess(context, "SentSMS_List", e);
        }
        return null;
    }

    private void delete_SentSMS(String sId) {

        int i, num = 1;
        for (i = 0; i < sId.length(); i++) {
            if (sId.charAt(i) == ',') {
                num++;
            }
        }
        print("sId", "" + num);
        String id[] = new String[num];

        for (i = 0; i < num; i++) {
            int pos = sId.indexOf(",");
            print("pos " + pos);
            if (pos == -1) {
                if (sId.length() > 9) {
                    if (sId.substring(0, sId.length()).length() > 0) {
                        id[i] = sId.substring(0, sId.length());
                        print("End Now");
                    } else {
                        return;
                    }
                }
            } else {
                if (sId.substring(0, pos).length() > 0) {
                    id[i] = sId.substring(0, pos);
                    print(i + " No is " + id[i]);
                    sId = sId.substring(++pos, sId.length());
                } else {
                    return;
                }
            }
        }
        id[num - 1] = sId;
        for (i = 0; i < num; i++) {
            print("Id", "" + id[i]);
            db_sentSMS.deleteSentSMS(id[i]);
        }
        ssId.delete(0, ssId.length());
        setSentSMSData();
    }

    private void onSentSMSAction() {
        fab_addNewAutoMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_SentSMS(ssId.toString());
            }
        });
        rvSnetSMSlist.addOnItemTouchListener(new RecyclerItemClickListener(context, rvSnetSMSlist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                LinearLayout layItemView = (LinearLayout) view.findViewById(R.id.sentSMS_ll_messageBG);
                layItemView.setBackgroundResource(R.drawable.itemview_bg_longpress);
                TextView tvIdNo = (TextView) view.findViewById(R.id.sentSMS_id);
                final int idNo = Integer.parseInt(tvIdNo.getText().toString());
                if (ssId.length() == 0) {
                    ssId.append(idNo + "");
                } else {
                    ssId.append("," + idNo);
                }
                print("ss", "Selected-" + ssId);
                fab_addNewAutoMess.setVisibility(View.VISIBLE);
            }
        }));
    }

    private void setSentSMSData() {
        try {
            fab_addNewAutoMess.setVisibility(View.INVISIBLE);
            if (listItems != null) {
                listItems.clear();
            }
            if (db_sentSMS == null) {
                db_sentSMS = new DB_sentSMS(context);
            }
            Cursor res = db_sentSMS.getAllSentMessages();
            if (res.getCount() == 0) {
                noSMSsent.setVisibility(View.VISIBLE);
                return;
            } else {
                noSMSsent.setVisibility(View.INVISIBLE);
                String ssId, ssPhNumber, ssMessage, ssDate, ssTime, ssStatus, ssmId, ssgId, ssSIM;
                while (res.moveToNext()) {
                    ssId = ("" + res.getString(0));
                    ssPhNumber = ("" + res.getString(1));
                    ssMessage = ("" + res.getString(2));
                    ssDate = ("" + res.getString(3));
                    ssTime = ("" + res.getString(4));
                    ssStatus = ("" + res.getString(5));
                    ssgId = ("" + res.getString(6));
                    ssmId = ("" + res.getString(7));
                    ssSIM = ("" + res.getString(8));
                    SentSMS_itemview listItem = new SentSMS_itemview(ssId, ssPhNumber, ssMessage, ssDate, ssTime, ssStatus, ssmId, ssgId, ssSIM);
                    listItems.add(listItem);
                }
                SentSMSadapter = new SentSMSadapter(listItems, context);
                rvSnetSMSlist.setAdapter(SentSMSadapter);
            }
        } catch (Exception e) {
            excMess(context, "setSentSMSData", e);
        }
    }

    private void initialized() {
        db_sentSMS = new DB_sentSMS(context);
        rvSnetSMSlist = (RecyclerView) rootView.findViewById(R.id.rcvSentSMS_messageList);
        rvSnetSMSlist.setHasFixedSize(true);
        rvSnetSMSlist.setLayoutManager(new LinearLayoutManager(context));
        listItems = new ArrayList<>();
        noSMSsent = (LinearLayout) rootView.findViewById(R.id.sentSMS_NoSMSsent);
        noSMSsent.setVisibility(View.INVISIBLE);
        fab_addNewAutoMess = (FloatingActionButton) rootView.findViewById(R.id.fab_SentMessageDelete);
    }

}
