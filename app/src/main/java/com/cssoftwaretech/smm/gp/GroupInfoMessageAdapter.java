package com.cssoftwaretech.smm.gp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.database.DB_Groups;
import com.cssoftwaretech.smm.database.DB_Settings;

import java.util.List;

import static com.cssoftwaretech.smm.Settings.getDateForUser;
import static com.cssoftwaretech.smm.Settings.getTimeIn12hour;

public class GroupInfoMessageAdapter extends RecyclerView.Adapter<GroupInfoMessageAdapter.ViewHolder> {
    private static int selectedItem = -1;
    private final OnItemClickListener listener;
    private List<GroupInfo_Message_info> listItems;
    private Context context;
    private DB_Groups db_groups;
    private DB_Settings db_settings;

    public GroupInfoMessageAdapter(List<GroupInfo_Message_info> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        db_groups = new DB_Groups(context);
        db_settings = new DB_Settings(context);
        listener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_info_message_itemview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroupInfo_Message_info listItem = listItems.get(position);
        holder.tvGinfo_id.setText(listItem.getgId());
        holder.tvGinfo_message.setText(listItem.getgMessage());
        holder.tvGinfo_date.setText(getDateForUser(listItem.getgDate()));
        holder.tvGinfo_time.setText(db_settings.is24HourView() ? listItem.getgTime() : getTimeIn12hour(listItem.getgTime()));
        switch (listItem.getgStatus()) {
            case "1":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_stop);
                break;
            case "2":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_save);
                break;
            case "3":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_active);
                break;
            case "4":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_send);
                break;
            case "5":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_failure);
                break;
            case "6":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_error_message);
                break;
            case "7":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_exception_message);
                break;
            case "8":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_radio_off);
                break;
            case "9":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_all);
                break;
            case "10":
                holder.imgGinfo_status.setImageResource(R.drawable.ic_done_notdelivered);
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
        void onItemClick(GroupInfo_Message_info item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGinfo_id, tvGinfo_message, tvGinfo_date, tvGinfo_time;
        private ImageView imgGinfo_status;

        public ViewHolder(View itemView) {
            super(itemView);
            tvGinfo_id = (TextView) itemView.findViewById(R.id.gInfo_Message_item_id);
            tvGinfo_message = (TextView) itemView.findViewById(R.id.gInfo_Message_item_message);
            tvGinfo_date = (TextView) itemView.findViewById(R.id.gInfo_Message_item_date);
            tvGinfo_time = (TextView) itemView.findViewById(R.id.gInfo_Message_item_time);
            imgGinfo_status = (ImageView) itemView.findViewById(R.id.gInfo_Message_item_status);
        }
    }
}

class GroupInfo_Message_info {
    private String gId, gMessage, gDate, gTime, gStatus;

    public GroupInfo_Message_info(String gId, String gMessage, String gDate, String gTime, String gStatus) {
        this.gId = gId;
        this.gMessage = gMessage;
        this.gDate = gDate;
        this.gTime = gTime;
        this.gStatus = gStatus;
    }

    public String getgId() {
        return gId;
    }

    public String getgMessage() {
        return gMessage;
    }

    public String getgDate() {
        return gDate;
    }

    public String getgTime() {
        return gTime;
    }

    public String getgStatus() {
        return gStatus;
    }
}
