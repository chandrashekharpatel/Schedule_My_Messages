package com.cssoftwaretech.smm.gp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.NewMessages;
import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.RecyclerItemClickListener;
import com.cssoftwaretech.smm.database.DB_Groups;

import java.util.ArrayList;
import java.util.List;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;

public class GroupInfoMessages extends AppCompatActivity {
    private Context context = this;
    private List<GroupInfo_Message_info> listItems;
    private DB_Groups db_groups;
    private LinearLayout tvMessNoMessageSent;
    private GroupInfoMessageAdapter groupInfoMessageAdapter;
    private RecyclerView rvGPinfoMessage;
    private boolean isItemOpen = false;
    private int position = 0, gpId;
    private Intent newGpMessageSet;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.group_info_messages);
            initialization();
            setData();
            onAction();
            Cursor memList = db_groups.getAllMemberByGp(gpId);
            if (memList.getCount() == 0) {
                groupInfoView();
            }
        } catch (Exception e) {
            excMess(context, "GPInfo Message List", e);
        }
    }

    private void onAction() {
        FloatingActionButton newGMessage = (FloatingActionButton) findViewById(R.id.fab_addNewGMessage);
        newGMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMessageSet(gpId);
            }
        });
        rvGPinfoMessage.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), rvGPinfoMessage, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                TextView tvIdNo = (TextView) view.findViewById(R.id.gInfo_Message_item_id);
                final int gId = Integer.parseInt(tvIdNo.getText().toString());
                print("Gid", " is " + gId);

                    newMessageSet(gpId, gId);
                    detailOpened(position);
                    isItemOpen = true;

            }

            @Override
            public void onItemLongClick(View view, int position) {
                TextView tvIdNo = (TextView) view.findViewById(R.id.gInfo_Message_item_id);
                final int gId = Integer.parseInt(tvIdNo.getText().toString());

            }
        }));

    }

    private void detailOpened(int position) {
        this.position = position;
    }

    private void initialization() {
        db_groups = new DB_Groups(context);
        tvMessNoMessageSent = (LinearLayout) findViewById(R.id.tvRLV_addNewGMessage);
        rvGPinfoMessage = (RecyclerView) findViewById(R.id.gpInfoRCV_messages);
        rvGPinfoMessage.setHasFixedSize(true);
        newGpMessageSet = new Intent(context, NewMessages.class);
        rvGPinfoMessage.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar = (Toolbar) findViewById(R.id.gpInfoToolbar);
            setSupportActionBar(toolbar);
        }
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            toastMess(context, "Something wrong", false);
        } else {
            gpId = getIntent().getExtras().getInt("groupId");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ginfo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_groupInfo:
                groupInfoView();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void groupInfoView() {
        Intent groupInfo = new Intent(context, GroupInfo.class);
        groupInfo.putExtra("groupId", gpId);
        startActivity(groupInfo);
    }

    private void setData() {
        try {

            if (db_groups.isGroupRemoved(gpId)) {
                finish();
            }
            if (listItems != null) {
                listItems.clear();
            }
            Cursor curGpInfo = db_groups.getGroupById(gpId);
            if (curGpInfo != null) {
                if (curGpInfo.getCount() > 0) {
                    curGpInfo.moveToFirst();
                    android.support.v7.app.ActionBar ab = getSupportActionBar();
                    ab.setTitle(curGpInfo.getString(1));
                    ab.setSubtitle(curGpInfo.getString(2));
                    ab.setDisplayHomeAsUpEnabled(true);
                }
            }

            db_groups.getAllMemberByGp(gpId);
            String gId, gMessage, gDate, gTime, gStatus;
            Cursor res = db_groups.getAllGMessage(gpId);
            if (res.getCount() == 0) {
                print("No GMessage");
                tvMessNoMessageSent.setVisibility(View.VISIBLE);
                tvMessNoMessageSent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (db_groups.isGroupMember(gpId)) {
                            newMessageSet(gpId);
                        } else {
                            Intent groupInfo = new Intent(context, GroupInfo.class);
                            groupInfo.putExtra("groupId", gpId);
                            startActivity(groupInfo);
                        }
                    }

                });
                return;
            } else {
                while (res.moveToNext()) {
                    tvMessNoMessageSent.setVisibility(View.INVISIBLE);
                    gId = ("" + res.getString(0));
                    gMessage = ("" + res.getString(1));
                    gDate = ("" + res.getString(2));
                    gTime = ("" + res.getString(3));
                    gStatus = ("" + res.getString(4));
                    GroupInfo_Message_info listItem = new GroupInfo_Message_info(gId, gMessage, gDate, gTime, gStatus);
                    listItems.add(listItem);
                }
                groupInfoMessageAdapter = new GroupInfoMessageAdapter(listItems, this);
                rvGPinfoMessage.setAdapter(groupInfoMessageAdapter);
            }
        } catch (Exception e) {
            excMess(context, "GPInfo Message", e);
        }
    }

    private void newMessageSet(int gpId, int gId) {
        newGpMessageSet.putExtra("groupMessUpdategId", gId);
        newMessageSet(gpId);
    }

    private void newMessageSet(int gpId) {
        newGpMessageSet.putExtra("groupMessSetgpId", gpId);
        startActivity(newGpMessageSet);
    }

    @Override
    protected void onRestart() {
        try {
            super.onRestart();
            Cursor memListA = db_groups.getAllMemberByGp(gpId);
            if (memListA.getCount() == 0) {
                finish();
            } else {
                setData();
                if (isItemOpen) {
                    isItemOpen = false;
                    groupInfoMessageAdapter.setSelectedItem(position);
                    rvGPinfoMessage.scrollToPosition(position);
                }
            }
        } catch (Exception e) {
            excMess(context, "GP Reload", e);
        }
    }


}
