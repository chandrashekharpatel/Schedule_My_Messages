package com.cssoftwaretech.smm.gp;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.database.DB_Groups;

import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;

public class GroupCreate extends AppCompatActivity {
EditText etGPname,etGPsubTitle;
Button btnCreateGP;
private Context context = this;
String gpName,gpSubTitle;
DB_Groups db_groups;
private int gpId=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create_activity);
            initialization();
            setData();
            onAction();
        }catch (Exception e){
            excMess(context,"GP Create",e);
        }
    }

    private void onAction() {
        btnCreateGP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etGPname.length()<1){
                    etGPname.requestFocus();
                    toastMess(context,"Enter Group Name",true);
                    return;
                }else if(etGPsubTitle.length()<1){
                    etGPname.requestFocus();
                    toastMess(context,"Enter Group Name",true);
                    return;
                }else{
                    gpName = etGPname.getText().toString();
                    gpSubTitle = etGPsubTitle.getText().toString();
                    if(gpId!=-1) {
                        if (db_groups.updateGroup(gpId,gpName, gpSubTitle)) {
                            toastMess(context, "Group Update Successful", false);
                            finish();
                        } else {
                            toastMess(context, "Err Group Not Update", false);
                        }
                    }else{
                        if (db_groups.createGroup(gpName, gpSubTitle)) {
                            toastMess(context, "New Group Created", false);
                            finish();
                        } else {
                            toastMess(context, "Err Group Not Create", false);
                        }
                    }
                }
            }
        });
    }

    private void setData() {
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            etGPname.requestFocus();
            gpId= -1;
        } else {
            gpId = getIntent().getExtras().getInt("groupId");
            print("group ID", "IS - " + gpId);
            Cursor cur = db_groups.getGroupById(gpId);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                etGPname.setText(cur.getString(1));
                etGPsubTitle.setText(cur.getString(2));
            }
        }
    }

    private void initialization() {
        etGPname = (EditText) findViewById(R.id.gc_gpName);
        etGPsubTitle = (EditText) findViewById(R.id.gc_gpSubTitle);
        btnCreateGP = (Button) findViewById(R.id.gc_btnCreate);
        db_groups = new DB_Groups(context);
    }
}
