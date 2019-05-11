package com.cssoftwaretech.smm.sentMessages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.database.DB_Settings;

import java.util.List;

import static com.cssoftwaretech.smm.Settings.getTimeIn12hour;
import static com.cssoftwaretech.smm.Settings.replacePhone2Name;

public class SentSMSadapter extends RecyclerView.Adapter<SentSMSadapter.ViewHolder> {
    private static int selectedItem = -1;
    private final OnItemClickListener listener;
    private List<SentSMS_itemview> listItems;
    private Context context;
    private DB_Settings db_settings;
    private boolean isGroupSMS = false;

    public SentSMSadapter(List<SentSMS_itemview> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        db_settings = new DB_Settings(context);
        listener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_sms_itemview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SentSMS_itemview listItem = listItems.get(position);
        if (listItem.getSentGid().equalsIgnoreCase("null")) {
            isGroupSMS = false;
            holder.ll_messBackground.setBackgroundResource(R.drawable.bg_sent_message_group);
        } else {
            isGroupSMS = true;
            holder.ll_messBackground.setBackgroundResource(R.drawable.bg_sent_message);
        }
        holder.tvSentSMS_id.setText(listItem.getSentId());
        holder.tvSentSMS_phNumber.setText(Html.fromHtml(replacePhone2Name(context, listItem.getSentPhNumber())));
        holder.tvSentSMS_message.setText(listItem.getSentMessage());
        holder.tvSentSMS_date.setText(listItem.getSentDate());
        holder.tvSentSMS_time.setText(db_settings.is24HourView() ? listItem.getSentTime() : getTimeIn12hour(listItem.getSentTime()));
        holder.tvSentSMS_GId.setText(listItem.getSentGid() + "");
        holder.tvSentSMS_Mid.setText(listItem.getSentMId() + "");
        switch (listItem.getSentSIM()) {
            case "0":
                holder.tvSentSMS_SIM.setText("");
                break;
            case "1":
                holder.tvSentSMS_SIM.setText("1");
                break;
            case "2":
                holder.tvSentSMS_SIM.setText("2");
                break;
        }
        switch (listItem.getSentStatus()) {
            case "0":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_person);
                break;
            case "1":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_stop);
                break;
            case "2":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_save);
                break;
            case "3":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_active);
                break;
            case "4":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_send);
                break;
            case "5":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_failure);
                break;
            case "6":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_error_message);
                break;
            case "7":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_exception_message);
                break;
            case "8":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_radio_off);
                break;
            case "9":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_all);
                break;
            case "10":
                holder.imgSentSMS_status.setImageResource(R.drawable.ic_done_notdelivered);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    public interface OnItemClickListener {
        void onItemClick(SentSMS_itemview item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_messBackground, ll_messLayout;
        private TextView tvSentSMS_id, tvSentSMS_phNumber, tvSentSMS_message, tvSentSMS_date, tvSentSMS_time, tvSentSMS_GId, tvSentSMS_Mid, tvSentSMS_SIM;
        private ImageView imgSentSMS_status;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSentSMS_id = (TextView) itemView.findViewById(R.id.sentSMS_id);
            tvSentSMS_phNumber = (TextView) itemView.findViewById(R.id.sentSMS_phNumber);
            tvSentSMS_message = (TextView) itemView.findViewById(R.id.sentSMS_message);
            tvSentSMS_date = (TextView) itemView.findViewById(R.id.sentSMS_date);
            tvSentSMS_time = (TextView) itemView.findViewById(R.id.sentSMS_time);
            tvSentSMS_GId = (TextView) itemView.findViewById(R.id.sentSMS_gId);
            tvSentSMS_Mid = (TextView) itemView.findViewById(R.id.sentSMS_mId);
            tvSentSMS_SIM = (TextView) itemView.findViewById(R.id.sentSMS_SIM);
            imgSentSMS_status = (ImageView) itemView.findViewById(R.id.sentSMS_status);
            ll_messBackground = (LinearLayout) itemView.findViewById(R.id.sentSMS_ll_messageBG);
            ll_messLayout = (LinearLayout) itemView.findViewById(R.id.sentSMS_ll_message);
        }
    }
}

class SentSMS_itemview {
    private String sentId, sentPhNumber, sentMessage, sentDate, sentTime, sentStatus, sentGid, sentMId, sentSIM;

    public SentSMS_itemview(String sentId, String sentPhNumber, String sentMessage, String sentDate, String sentTime, String sentStatus, String sentGid, String sentMId, String sentSIM) {
        this.sentId = sentId;
        this.sentPhNumber = sentPhNumber;
        this.sentMessage = sentMessage;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
        this.sentStatus = sentStatus;
        this.sentGid = sentGid;
        this.sentMId = sentMId;
        this.sentSIM = sentSIM;
    }

    public String getSentId() {
        return sentId;
    }

    public String getSentPhNumber() {
        return sentPhNumber;
    }

    public String getSentMessage() {
        return sentMessage;
    }

    public String getSentDate() {
        return sentDate;
    }

    public String getSentTime() {
        return sentTime;
    }

    public String getSentStatus() {
        return sentStatus;
    }

    public String getSentGid() {
        return sentGid;
    }

    public String getSentMId() {
        return sentMId;
    }

    public String getSentSIM() {
        return sentSIM;
    }
}
