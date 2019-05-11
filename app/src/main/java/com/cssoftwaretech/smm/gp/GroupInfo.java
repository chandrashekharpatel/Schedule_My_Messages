package com.cssoftwaretech.smm.gp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.RecyclerItemClickListener;
import com.cssoftwaretech.smm.database.DB_Groups;

import java.util.ArrayList;
import java.util.List;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;
import static com.cssoftwaretech.smm.Settings.getContactName;
import static com.cssoftwaretech.smm.Settings.getNameTowChar;
import static com.cssoftwaretech.smm.Settings.getTruePhoneNumber;

public class GroupInfo extends AppCompatActivity {
    private Context context = this;
    private TextView tvGpId, tvGpName, tvGpSubTitle, imgText;
    private LinearLayout tvAddNewMemberInGP, tvDeleteGroup;
    private DB_Groups db_groups;
    private List<MemberItemInfo> listItems;
    private RecyclerView rvMemberList;
    private ImageView editGroupName, imgPhoto;
    private MemberListAdapter memberListAdapter;
    private int gpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.group_info_activity);
            initialization();
            onAction();
            setData();
        } catch (Exception e) {
            excMess(context, "GP Info ", e);
        }
    }

    private void setData() {
        try {
           if (listItems!=null) {
                listItems.clear();
            }
            Cursor res = db_groups.getAllMemberByGp(gpId);
            if (res.getCount() == 0) {
                print("No Member");
                tvAddNEwMemberVisibility(true);
                tvAddNewMemberInGP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addNewMember();
                    }
                });
            } else {
                String mId, mName;
                while (res.moveToNext()) {
                    tvAddNEwMemberVisibility(false);
                    mId = ("" + res.getInt(0));
                    mName = ("" + res.getString(1));
                    MemberItemInfo listItem = new MemberItemInfo(mId, mName);
                    listItems.add(listItem);
                }
                memberListAdapter = new MemberListAdapter(listItems, this);
                rvMemberList.setAdapter(memberListAdapter);
            }
        } catch (Exception e) {
            excMess(context, "GP Member list", e);
        }
    }

    private void onAction() {

        tvDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messSureGpDelete(context, gpId, "Are you sure ?", "don't recover after delete");
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addNewMemberInGP);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMember();
            }
        });


        editGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupCreateDialog(context, gpId);
            }
        });


        rvMemberList.addOnItemTouchListener(new RecyclerItemClickListener(context, rvMemberList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
               /* try {
                    ImageView imgDelete = (ImageView) view.findViewById(R.id.con_img_delete);
                    final TextView tvIdNo = (TextView) view.findViewById(R.id.con_tv_contact_id);
                    imgDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteMId = Integer.parseInt(tvIdNo.getText().toString());
                            messSureDelete(context, deleteMId, "Are you sure?", "Do you want to remove");
                        }
                    });

                } catch (Exception e) {
                    excMess(context, "Click S", e);
                }*/
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final TextView tvIdNo = (TextView) view.findViewById(R.id.con_tv_contact_id);
                final TextView tvName = (TextView) view.findViewById(R.id.con_tv_contact_name);
                messSureDelete(context, Integer.parseInt(tvIdNo.getText().toString()), tvName.getText().toString());
            }
        }));
    }


    public void groupCreateDialog(final Context context, final int gpId) {
        try {
            final String type = "Update";
            //Create GP dialog
            final EditText etGPname, etGPsubTitle;
            Button btnCreateGP, btnGpCreate_Cancel, btnDeleteGP;
            TextView tvTitle;
            String gpName, gpSubTitle;
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
            //Data Set
            if (type.equalsIgnoreCase("NEW")) {

                print("group new", "Create Group");
            } else if (type.equalsIgnoreCase("Update")) {
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
                        if (type.equalsIgnoreCase("Update")) {
                            if (db_groups.updateGroup(gpId, etGPname.getText().toString(), etGPsubTitle.getText().toString())) {
                                updateGroupDetail();
                                toastMess(context, "Group Update Successful", false);
                            } else {
                                toastMess(context, "Err Group Not Update", false);
                            }
                        } else if (type.equalsIgnoreCase("NEW")) {
                            if (db_groups.createGroup(etGPname.getText().toString(), etGPsubTitle.getText().toString())) {
                                updateGroupDetail();
                                toastMess(context, "New Group Created", false);
                            } else {
                                toastMess(context, "Err Group Not Create", false);
                            }
                        }
                        createGpDial.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            excMess(context, "getContact Image", e);
        }
    }

    private void addNewMember() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, 1);
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
                        phoneNo = getTruePhoneNumber(phoneNo);
                        print("Name and contact number is", name + "," + phoneNo);
                        addMember(Integer.parseInt(tvGpId.getText().toString()), phoneNo);
                    } catch (Exception e) {
                        excMess(context, "pickNumber", e);
                    }
                }
                break;
        }
    }


    private void addMember(int gpId, String memberNo) {
        memberNo = memberNo.replace(" ", "");
        if (!db_groups.isAlreadyMember(gpId, memberNo)) {
            if (db_groups.addMember(gpId, memberNo)) {
                setData();
                toastMess(context, "New member added", false);
            }
        } else {
            toastMess(context, "Already member", false);
        }
    }

    private void initialization() {
        tvGpId = (TextView) findViewById(R.id.gInfo_GpId);
        tvGpName = (TextView) findViewById(R.id.gInfo_GpName);
        tvGpSubTitle = (TextView) findViewById(R.id.gInfo_GpSubTitle);
        listItems = new ArrayList<>();
        rvMemberList = (RecyclerView) findViewById(R.id.gInfo_rcvMamberList);
        rvMemberList.setHasFixedSize(true);
        rvMemberList.setLayoutManager(new LinearLayoutManager(context));
        tvAddNewMemberInGP = (LinearLayout) findViewById(R.id.gInfo_addNewMemberInGP);
        editGroupName = (ImageView) findViewById(R.id.gInfo_editGroupName);
        imgPhoto = (ImageView) findViewById(R.id.gInfo_imgPhoto);
        imgText = (TextView) findViewById(R.id.gInfo_imgText);
        tvAddNEwMemberVisibility(false);
        tvDeleteGroup = (LinearLayout) findViewById(R.id.gInfo_deleteGroup);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 5);
        tvDeleteGroup.setLayoutParams(params);
        db_groups = new DB_Groups(context);

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            tvGpName.setText("Something Wrong");
            tvGpName.setTextColor(Color.RED);
        } else {
            updateGroupDetail();
        }
    }

    private void updateGroupDetail() {
        gpId = getIntent().getExtras().getInt("groupId");
        Cursor res = db_groups.getGroupById(gpId);
        if (res.getCount() != 0) {
            res.moveToFirst();
            tvGpId.setText(gpId + "");
            String gpName = res.getString(1);
            tvGpName.setText(gpName);
            tvGpSubTitle.setText(res.getString(2));
            String charName = getNameTowChar(getContactName(gpName, context));
            if (charName.equalsIgnoreCase("1")) {
                imgPhoto.setImageResource(R.drawable.ic_group_people);
            } else {
                if (charName.charAt(1) == '*') {
                    imgText.setTextSize(40);
                    imgText.setText(charName.charAt(0) + "");
                } else {
                    imgText.setText(charName);
                }
            }
        } else {
            tvGpId.setVisibility(View.INVISIBLE);
            tvGpName.setVisibility(View.INVISIBLE);
            tvGpSubTitle.setVisibility(View.INVISIBLE);
            tvDeleteGroup.setEnabled(false);
            tvAddNewMemberInGP.setEnabled(false);
        }
    }

    private void tvAddNEwMemberVisibility(boolean Visibility) {
        if (Visibility) {
            tvAddNewMemberInGP.setPadding(15, 15, 15, 15);
            tvAddNewMemberInGP.setVisibility(View.VISIBLE);
            tvAddNewMemberInGP.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        } else {
            tvAddNewMemberInGP.setPadding(0, 0, 0, 0);
            tvAddNewMemberInGP.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            tvAddNewMemberInGP.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        try {
            super.onRestart();

        } catch (Exception e) {
            excMess(context, "GP Info Reload", e);
        }
    }

    public void messSureDelete(final Context context, final int idNo, String name) {
        try {
            AlertDialog.Builder dialog;
            TextView tvHeader, tvMessage;
            LayoutInflater inflater;
            View layout;
            ImageView imgIcon;
            Button btnOK, btnCancel;
            final AlertDialog Dial;
            dialog = new AlertDialog.Builder(context);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.mess_alert, null);
            tvHeader = (TextView) layout.findViewById(R.id.tvMA_header);
            tvMessage = (TextView) layout.findViewById(R.id.tvMA_message);
            imgIcon = (ImageView) layout.findViewById(R.id.imgMA_icon);
            imgIcon.setImageResource(R.drawable.ic_delete);
            btnOK = (Button) layout.findViewById(R.id.btnMA_OK);
            btnCancel = (Button) layout.findViewById(R.id.btnMA_cancel);
            Dial = dialog.create();
            Dial.setCancelable(false);
            Dial.setView(layout);
            tvHeader.setText("Are you sure ?");
            tvMessage.setText("Do you want to remove " + name);
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
                    DB_Groups db_groups = new DB_Groups(context);
                    db_groups.removeMember(idNo);
                    setData();
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "GP Info Mess", e);

        }
    }

    public void messSureGpDelete(final Context context, final int gpdId, String title, String message) {
        try {
            AlertDialog.Builder dialog;
            TextView tvHeader, tvMessage;
            LayoutInflater inflater;
            View layout;
            ImageView imgIcon;
            Button btnOK, btnCancel;
            final AlertDialog Dial;
            dialog = new AlertDialog.Builder(context);
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
                        finish();
                    } else {
                        toastMess(context, "Group Not Delete", false);
                    }
                    Dial.dismiss();
                    finish();
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "GP Info Mess", e);

        }
    }

}
