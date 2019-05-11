package com.cssoftwaretech.smm.gp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.RecyclerItemClickListener;
import com.cssoftwaretech.smm.database.DB_Groups;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;

public class GroupList extends Fragment {
    private static final String TAG = "MainActivity";
    private List<GroupItemInfo> groupItemInfo;
    private DB_Groups db_groups;
    private LinearLayout tvCreateNewGP;
    private TextView tvCreateNewGPMessage;
    private GroupListAdapter groupListAdapter;
    private RecyclerView rvGPlist;
    private boolean isGpItemOpen = false;
    private int gpPos = 0;
    private int gpId;
    private Context context;
    private View rootView;
    private AdView mAdView;
    private FloatingActionButton fab_addNewGroup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.group_list_activity, container, false);
            context = getContext();
            initializaGroup();
            setGpData();
            onGpAction();
            return rootView;
        } catch (Exception e) {
            excMess(context, "GroupListMain", e);
        }
        return null;
    }

    private void initializaGroup() {
        db_groups = new DB_Groups(context);
        tvCreateNewGP = (LinearLayout) rootView.findViewById(R.id.tvRLV_createNewGP);
        rvGPlist = (RecyclerView) rootView.findViewById(R.id.gl_mamberList);
        rvGPlist.setHasFixedSize(true);
        rvGPlist.setLayoutManager(new LinearLayoutManager(context));
        groupItemInfo = new ArrayList<>();
        fab_addNewGroup = (FloatingActionButton) rootView.findViewById(R.id.fab_createNewGroup);
        tvCreateNewGPMessage = (TextView) rootView.findViewById(R.id.tvRLV_createNewGP_message);
    }

    public void setGpData() {
        try {
            mAdView = rootView.findViewById(R.id.adViewGroupList);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            if (groupItemInfo != null) {
                groupItemInfo.clear();
            }
            if (db_groups == null) {
                db_groups = new DB_Groups(context);
            }
            String gpId, gpName, gpSubTitle;
            Cursor res = db_groups.getAllGroups();
            if (res.getCount() == 0) {
                print("No Groups");
                tvCreateNewGP.setVisibility(View.VISIBLE);
                tvCreateNewGP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupCreateDialog(context, "NEW", 0);
                    }
                });
                return;
            } else {
                while (res.moveToNext()) {
                    tvCreateNewGP.setVisibility(View.INVISIBLE);
                    gpId = ("" + res.getString(0));
                    gpName = ("" + res.getString(1));
                    gpSubTitle = ("" + res.getString(2));
                    GroupItemInfo listItem = new GroupItemInfo(gpId, gpName, gpSubTitle);
                    groupItemInfo.add(listItem);
                }
                groupListAdapter = new GroupListAdapter(groupItemInfo, context);
                rvGPlist.setAdapter(groupListAdapter);
            }
        } catch (Exception e) {
            excMess(context, "GP List", e);
        }
    }


    private void detailGPOpened(int gpPos) {
        this.gpPos = gpPos;
    }

    @Override
    public void onResume() {
        super.onResume();
        setGpData();
    }

    private void onGpAction() {
        try {

            fab_addNewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupCreateDialog(context, "NEW", 0);
                }
            });
            rvGPlist.addOnItemTouchListener(new RecyclerItemClickListener(context, rvGPlist, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int gpPos) {
                    LinearLayout layItemView = (LinearLayout) view.findViewById(R.id.gl_item_layout);
                    layItemView.setBackgroundResource(R.drawable.itemview_bg_press);
                    TextView tvIdNo = (TextView) view.findViewById(R.id.gl_item_gpId);
                    gpId = Integer.parseInt(tvIdNo.getText().toString());
                    print("GP id", " is " + gpId);
                    Intent editMessage = new Intent(context, GroupInfoMessages.class);
                    editMessage.putExtra("groupId", gpId);
                    startActivity(editMessage);
                    detailGPOpened(gpPos);
                    isGpItemOpen = true;
                }

                @Override
                public void onItemLongClick(View view, int gpPos) {
                    // LinearLayout layItemView = (LinearLayout) view.findViewById(R.id.gl_item_layout);
                    //layItemView.setBackgroundResource(R.drawable.itemview_bg_longpress);
                    TextView tvIdNo = (TextView) view.findViewById(R.id.gl_item_gpId);
                    gpId = Integer.parseInt(tvIdNo.getText().toString());
                    groupCreateDialog(context, "UPDATE_GROUP", gpId);
                }
            }));
        } catch (Exception e) {
            excMess(context, "GP On Action", e);
        }
    }

    public void groupCreateDialog(final Context context, final String type, final int gpId) {
        try {
            //Create GP dialog
            this.context =context;
            final EditText etGPname, etGPsubTitle;
            Button btnCreateGP, btnGpCreate_Cancel, btnDeleteGP;
            TextView tvTitle;
            final android.support.v7.app.AlertDialog createGpDial;
            android.support.v7.app.AlertDialog.Builder createGroupDialog;
            LayoutInflater inflater;
            View layout;
            createGroupDialog = new android.support.v7.app.AlertDialog.Builder(context);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.group_create_activity, null);
            etGPname = (EditText) layout.findViewById(R.id.gc_gpName);
            tvTitle = (TextView) layout.findViewById(R.id.gc_tvTitle);
            etGPsubTitle = (EditText) layout.findViewById(R.id.gc_gpSubTitle);
            btnGpCreate_Cancel = (Button) layout.findViewById(R.id.gc_btnGpCreate_Cancel);
            btnCreateGP = (Button) layout.findViewById(R.id.gc_btnCreate);
            btnDeleteGP = (Button) layout.findViewById(R.id.gc_btnDelete);
            db_groups = new DB_Groups(context);
            tvTitle.setText("Update Group Information");
            btnCreateGP.setText("Update");
            btnGpCreate_Cancel.setText("Cancel");
            btnDeleteGP.setVisibility(View.VISIBLE);
            createGpDial = createGroupDialog.create();
            createGpDial.setCancelable(false);
            createGpDial.setView(layout);
            createGpDial.show();
            etGPname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        etGPname.setBackgroundResource(R.drawable.bg_edit_text_focus);
                    } else {
                        etGPname.setBackgroundResource(R.drawable.bg_edit_text);
                    }
                }
            });
            etGPsubTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        etGPsubTitle.setBackgroundResource(R.drawable.bg_edit_text_focus);
                    } else {
                        etGPsubTitle.setBackgroundResource(R.drawable.bg_edit_text);
                    }
                }
            });
            //Data Set
            if (type.equalsIgnoreCase("NEW")) {
                btnCreateGP.setText("Create");
                btnDeleteGP.setVisibility(View.INVISIBLE);
                tvTitle.setText("New Group Create");
            } else if (type.equalsIgnoreCase("UPDATE_GROUP")) {
                btnDeleteGP.setVisibility(View.VISIBLE);
                btnCreateGP.setText("Update");
                print("group ID", "IS - " + gpId);
                Cursor cur = db_groups.getGroupById(gpId);
                if (cur.getCount() > 0) {
                    cur.moveToFirst();
                    etGPname.setText(cur.getString(1));
                    etGPsubTitle.setText(cur.getString(2));
                }
            }
            btnDeleteGP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    messSureGpDelete(context, gpId, "Are you sure ?", "Don't recover after delete");
                    createGpDial.dismiss();
                }
            });
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
                        etGPsubTitle.requestFocus();
                        toastMess(context, "Enter Group Sub Title", true);
                        return;
                    } else {
                        if (type.equalsIgnoreCase("UPDATE_GROUP")) {
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
                        setGpData();
                        createGpDial.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            excMess(context, "getContact Image", e);
        }
    }

    public void messSureGpDelete(final Context context, final int gpdId, String title, String message) {
        try {
            android.app.AlertDialog.Builder dialog;
            TextView tvHeader, tvMessage;
            LayoutInflater inflater;
            View layout;
            ImageView imgIcon;
            Button btnOK, btnCancel;
            final android.app.AlertDialog Dial;
            dialog = new android.app.AlertDialog.Builder(context);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.mess_alert, null);
            tvHeader = (TextView) layout.findViewById(R.id.tvMA_header);
            tvMessage = (TextView) layout.findViewById(R.id.tvMA_message);
            imgIcon = (ImageView) layout.findViewById(R.id.imgMA_icon);
            btnOK = (Button) layout.findViewById(R.id.btnMA_OK);
            imgIcon.setImageResource(R.drawable.ic_delete);
            btnCancel = (Button) layout.findViewById(R.id.btnMA_cancel);
            Dial = dialog.create();
            Dial.setCancelable(false);
            Dial.setView(layout);
            tvHeader.setText(title);
            tvMessage.setText(message);
            btnCancel.setText("Cancel");
            btnOK.setText("Delete");
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dial.dismiss();
                }

            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (db_groups.removeGroup(gpdId)) {
                        toastMess(context, "Group Successfully Delete", false);
                    } else {
                        toastMess(context, "Group Not Delete", false);
                    }
                    setGpData();
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "GP Info Mess", e);

        }
    }

}
