package com.cssoftwaretech.smm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.database.DB_SetMessages;
import com.cssoftwaretech.smm.database.DB_Settings;

import java.util.List;

import static com.cssoftwaretech.smm.NewMessages.cancelSendingSMS;
import static com.cssoftwaretech.smm.Settings.getContactImage;
import static com.cssoftwaretech.smm.Settings.getContactName;
import static com.cssoftwaretech.smm.Settings.getDateForUser;
import static com.cssoftwaretech.smm.Settings.getDateTimeIntoMillis;
import static com.cssoftwaretech.smm.Settings.getNameTowChar;
import static com.cssoftwaretech.smm.Settings.getSystemCurrentTime;
import static com.cssoftwaretech.smm.Settings.getTimeDefference;
import static com.cssoftwaretech.smm.Settings.getTimeDifference;
import static com.cssoftwaretech.smm.Settings.getTimeIn12hour;
import static com.cssoftwaretech.smm.Settings.isSendSMSWorking;
import static com.cssoftwaretech.smm.Settings.replacePhone2Name;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message_itemView> listItems;
    private Context context;
    private final OnItemClickListener listener;
    private DB_SetMessages db_setMessages;
    private DB_Settings db_settings;


    public interface OnItemClickListener {
        void onItemClick(Message_itemView item);
    }

    public MessageAdapter(List<Message_itemView> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        db_settings = new DB_Settings(context);
        db_setMessages = new DB_SetMessages(context);
        listener = null;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_itemview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.ViewHolder holder, int position) {
        final Message_itemView listItem = listItems.get(position);
        int idNo = Integer.parseInt(listItem.getIdNo());
        holder.tvIdNo.setText(idNo + "");
        if (db_settings.isShowFrontMessage()) {
            holder.tvMessage.setText(listItem.getMessage());
            holder.tvMessage.setTextSize(15);
            holder.tvMessage.setPadding(5, 0, 5, 0);
        } else {
            holder.tvMessage.setVisibility(View.INVISIBLE);
        }
        if (db_settings.isShowRemainingTime()) {
            holder.tvRemainTime.setText(getTimeDifference(listItem.getTime(), getSystemCurrentTime()));
        } else {
            holder.tvRemainTime.setVisibility(View.INVISIBLE);
        }
        if (!db_settings.isShowFrontMessage()) {

            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(listItem.getName());
            holder.tvMessage.setPadding(5, 1, 5, 1);
        }
        String name = listItem.getName();
        holder.tvName.setText(Html.fromHtml(replacePhone2Name(context, name)));
        holder.imgText.setText("");
        holder.imgIcon.setImageResource(R.drawable.bg_round_shape);
        if (listItem.getName().toString().indexOf(",") > 0) {
            holder.imgIcon.setImageResource(R.drawable.ic_group_people);
        } else {
            Bitmap photo = getContactImage(context, listItem.getName());
            if (photo == null) {
                String charName = getNameTowChar(getContactName(name, context));
                if (charName.equalsIgnoreCase("1")) {
                    holder.imgIcon.setImageResource(R.drawable.ic_person);
                } else if (charName.charAt(1) == '*') {
                    holder.imgText.setText(charName.charAt(0) + "");

                } else {
                    holder.imgText.setText(charName);
                }
            } else {
                holder.imgIcon.setImageBitmap(photo);
            }
        }

        holder.tvDate.setText(getDateForUser(listItem.getDate()));
        holder.tvTime.setText(db_settings.is24HourView() ? listItem.getTime() : getTimeIn12hour(listItem.getTime()));

        if (isSendSMSWorking(context, idNo)) {
            if (db_setMessages.getRepeatTimeId(idNo) != 0) {
                if (getTimeDefference(getDateTimeIntoMillis(listItem.getDate(), listItem.getTime())) < -30000) {
                    holder.tvDate.setTextColor(Color.RED);
                    holder.tvTime.setTextColor(Color.RED);
                    cancelSendingSMS(context, idNo);
                }
            } else {
                holder.tvDate.setTextColor(Color.GREEN);
                holder.tvTime.setTextColor(Color.GREEN);
            }
        }
        switch (listItem.getSmsRepeat()) {
            case "1":
                holder.tvRptStatus.setText("D");
                break;
            case "2":
                holder.tvRptStatus.setText("W");
                break;
            case "3":
                holder.tvRptStatus.setText("M");
                break;
            case "4":
                holder.tvRptStatus.setText("Y");
                break;
        }
        switch (listItem.getSendStatus()) {
            case "0":
                holder.swiStatus.setImageResource(R.drawable.ic_person);
                break;
            case "1":
                holder.swiStatus.setImageResource(R.drawable.ic_done_stop);
                break;
            case "2":
                holder.swiStatus.setImageResource(R.drawable.ic_done_save);
                break;
            case "3":
                holder.swiStatus.setImageResource(R.drawable.ic_done_active);
                break;
            case "4":
                holder.swiStatus.setImageResource(R.drawable.ic_done_send);
                break;
            case "5":
                holder.swiStatus.setImageResource(R.drawable.ic_done_failure);
                break;
            case "6":
                holder.swiStatus.setImageResource(R.drawable.ic_error_message);
                break;
            case "7":
                holder.swiStatus.setImageResource(R.drawable.ic_exception_message);
                break;
            case "8":
                holder.swiStatus.setImageResource(R.drawable.ic_done_radio_off);
                break;
            case "9":
                holder.swiStatus.setImageResource(R.drawable.ic_done_all);
                break;
            case "10":
                holder.swiStatus.setImageResource(R.drawable.ic_done_notdelivered);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgIcon;
        private TextView imgText;
        private TextView tvIdNo;
        public TextView tvName;
        private TextView tvMessage;
        private TextView tvDate;
        private TextView tvRemainDate;
        private TextView tvTime;
        private TextView tvRemainTime;
        private ImageView swiStatus;
        private TextView tvRptStatus;
        private LinearLayout laySMSItemList;


        public ViewHolder(View itemView) {
            super(itemView);

            imgIcon = (ImageView) itemView.findViewById(R.id.imgMIV_icon);
            imgText = (TextView) itemView.findViewById(R.id.imgText_icon);
            tvIdNo = (TextView) itemView.findViewById(R.id.tvMIV_idNo);
            tvName = (TextView) itemView.findViewById(R.id.tvMIV_name);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMIV_message);
            tvDate = (TextView) itemView.findViewById(R.id.tvMIV_date);
            tvRemainDate = (TextView) itemView.findViewById(R.id.tvMIV_dayRemain);
            tvTime = (TextView) itemView.findViewById(R.id.tvMIV_time);
            tvRemainTime = (TextView) itemView.findViewById(R.id.tvMIV_timeRemain);
            swiStatus = (ImageView) itemView.findViewById(R.id.swiMIV_status);
            tvRptStatus = (TextView) itemView.findViewById(R.id.tvMIV_status);
            laySMSItemList = (LinearLayout) itemView.findViewById(R.id.layMIV_MainListItem);
        }

        public void bind(final Message_itemView item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

}

class Message_itemView {
    private String idNo;
    private String name;
    private String message;
    private String date;
    private String time;
    private String simType;
    private String sendStatus;
    private String smsRepeat;

    public Message_itemView(String idNo, String name, String message, String date, String time, String simType, String sendStatus, String smsRepeat) {
        this.idNo = idNo;
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
        this.simType = simType;
        this.sendStatus = sendStatus;
        this.smsRepeat = smsRepeat;
    }

    public String getIdNo() {
        return idNo;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSimType() {
        return simType;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public String getSmsRepeat() {
        return smsRepeat;
    }
}