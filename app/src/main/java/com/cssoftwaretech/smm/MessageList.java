package com.cssoftwaretech.smm;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.database.DB_SetMessages;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.cssoftwaretech.smm.MessNotice.excMess;
import static com.cssoftwaretech.smm.MessNotice.print;
import static com.cssoftwaretech.smm.MessNotice.toastMess;

public class MessageList extends Fragment {
    private RecyclerView rvMessagesSet;
    private LinearLayout layItemView;
    private TextView tvIdNo, tvAddNewMessage_mess;
    private LinearLayout tvAddNewMessage;
    private MessageAdapter adapter;
    private List<Message_itemView> listItems;
    private DB_SetMessages db_setMessages;
    private Context context;
    private View rootView;
    private FloatingActionButton fab_addNewAutoMess;
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        try {
            context = getContext();
            rootView = inflater.inflate(R.layout.activity_message_list, container, false);
            initialization();
            setData();
            onAction();
        } catch (Exception e) {
            excMess(context, "Message List", e);
        }
        return rootView;
    }

    private void onAction() {

        fab_addNewAutoMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, NewMessages.class));

            }
        });
        rvMessagesSet.addOnItemTouchListener(new RecyclerItemClickListener(context, rvMessagesSet, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                layItemView = (LinearLayout) view.findViewById(R.id.layMIV_MainListItem);
                layItemView.setBackgroundResource(R.drawable.itemview_bg_press);
                tvIdNo = (TextView) view.findViewById(R.id.tvMIV_idNo);
                final int idNo = Integer.parseInt(tvIdNo.getText().toString());
                print("ID", "ID iS " + idNo);
                Intent editMessage = new Intent(context, NewMessages.class);
                editMessage.putExtra("messageIdNo", idNo);
                startActivity(editMessage);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                layItemView = (LinearLayout) view.findViewById(R.id.layMIV_MainListItem);
                layItemView.setBackgroundResource(R.drawable.itemview_bg_longpress);
                TextView tvName = (TextView) view.findViewById(R.id.tvMIV_name);

                tvIdNo = (TextView) view.findViewById(R.id.tvMIV_idNo);
                final int idNo = Integer.parseInt(tvIdNo.getText().toString());
                deleteMessage(idNo, tvName.getText().toString());
            }
        }));
    }

    public void deleteMessage(final int vNo, String name) {
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
            btnCancel = (Button) layout.findViewById(R.id.btnMA_cancel);
            Dial = dialog.create();
            Dial.setView(layout);
            imgIcon.setImageResource(R.drawable.ic_delete);
            tvHeader.setText("Delete - Are you sure ?");
            tvMessage.setText("Do you want to delete " + name + " Message");
            btnCancel.setText("NO");
            btnOK.setText("Delete");
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setData();
                    Dial.dismiss();
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (db_setMessages.deleteMessage(vNo)) {
                        toastMess(context, "Successfully Deleted", false);
                    }
                    setData();
                    Dial.dismiss();
                }
            });
            Dial.show();
        } catch (Exception e) {
            excMess(context, "Err In MSG", e);
        }
    }

    private void initialization() {
        db_setMessages = new DB_SetMessages(context);
        rvMessagesSet = (RecyclerView) rootView.findViewById(R.id.rcvMessageList);
        rvMessagesSet.setHasFixedSize(true);
        rvMessagesSet.setLayoutManager(new LinearLayoutManager(context));
        tvAddNewMessage = (LinearLayout) rootView.findViewById(R.id.tvRLV_addNewMessage);
        tvAddNewMessage_mess = (TextView) rootView.findViewById(R.id.tvRLV_addNewMessageShow);
        tvAddNewMessage.setVisibility(View.INVISIBLE);
        listItems = new ArrayList<>();
        fab_addNewAutoMess = (FloatingActionButton) rootView.findViewById(R.id.fab_addNewMessage);
    }

    private void setData() {
        try {
            mAdView = rootView.findViewById(R.id.adViewMessageList);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            if (listItems != null) {
                listItems.clear();
            }
            String idNo, name, message, date, time, simType, sendStatus, smsRepeat;
            Cursor res = db_setMessages.getAllMessages();
            if (res.getCount() == 0) {
                print("No Message");
                tvAddNewMessage.setVisibility(View.VISIBLE);
                tvAddNewMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(context, NewMessages.class));
                        tvAddNewMessage.setVisibility(View.INVISIBLE);
                    }
                });
                return;
            } else {
                tvAddNewMessage.setVisibility(View.INVISIBLE);
                while (res.moveToNext()) {
                    idNo = ("" + res.getString(0));
                    name = ("" + res.getString(1));
                    message = ("" + res.getString(2));
                    date = ("" + res.getString(3));
                    time = ("" + res.getString(4));
                    simType = ("" + res.getString(5));
                    sendStatus = ("" + res.getString(6));
                    smsRepeat = ("" + res.getString(7));
                    Message_itemView listItem = new Message_itemView(idNo, name, message, date, time, simType, sendStatus, smsRepeat);
                    listItems.add(listItem);
                }
                adapter = new MessageAdapter(listItems, context);
                rvMessagesSet.setAdapter(adapter);
            }

        } catch (Exception e) {
            excMess(context, "M List", e);
        }
    }


    @Override
    public void onResume() {
        try {
            super.onResume();
            setData();
         /*   if (isItemOpen) {
                isItemOpen = false;
                // setData();
                if (adapter != null) {
                    int last_pos = adapter.getItemCount() - 1;
                    adapter.setSelectedItem(openIntent);
                    rvMessagesSet.scrollToPosition(openIntent);
                    RecyclerView.ViewHolder nn = rvMessagesSet.findViewHolderForAdapterPosition(openIntent);
                    nn.itemView.setBackgroundResource(R.drawable.btn_message_item_onclick);
                    nn.itemView.setFocusable(true);
                    nn.itemView.requestFocus();
                }
            } else if (isNewIntentOpen) {
                if (adapter != null) {
                    int last_pos = adapter.getItemCount() - 1;
                    adapter.setSelectedItem(last_pos);
                    rvMessagesSet.scrollToPosition(last_pos);
                }
            }*/
        } catch (Exception e) {
            excMess(context, "ReloadML", e);
        }
    }
}
