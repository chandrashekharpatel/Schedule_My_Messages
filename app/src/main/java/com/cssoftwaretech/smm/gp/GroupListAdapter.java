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

import java.util.List;

import static com.cssoftwaretech.smm.Settings.getContactName;
import static com.cssoftwaretech.smm.Settings.getNameTowChar;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private List<GroupItemInfo> listItems;
    private Context context;
    private final OnItemClickListener listener;
    private DB_Groups db_groups;
    private static int selectedItem = -1;

    public interface OnItemClickListener {
        void onItemClick(GroupItemInfo item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grouplist_itemview, parent, false);
        return new ViewHolder(v);
    }

    public GroupListAdapter(List<GroupItemInfo> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        db_groups = new DB_Groups(context);
        listener = null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroupItemInfo listItem = listItems.get(position);
        holder.tvGpId.setText(listItem.getGpId());
        String gpName = listItem.getGpName();
        holder.tvGpName.setText(listItem.getGpName());
        holder.tvGpSubTitle.setText(listItem.getGpSubTitle());
        String charName = getNameTowChar(getContactName(gpName, context));
        if (charName.equalsIgnoreCase("1")) {
            holder.imgGpPhoto.setImageResource(R.drawable.ic_group_people);
        } else {
            if(charName.charAt(1)=='*'){
                holder.imgGpText.setText(charName.charAt(0)+"");
            }else {
                holder.imgGpText.setText(charName);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGpId;
        private TextView tvGpName;
        private TextView tvGpSubTitle;
        private TextView imgGpText;
        private ImageView imgGpPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            tvGpId = (TextView) itemView.findViewById(R.id.gl_item_gpId);
            tvGpName = (TextView) itemView.findViewById(R.id.gl_item_gpName);
            tvGpSubTitle = (TextView) itemView.findViewById(R.id.gl_item_gpSubTitle);
            imgGpPhoto = (ImageView) itemView.findViewById(R.id.gl_item_gpimage);
            imgGpText = (TextView) itemView.findViewById(R.id.gl_item_gpimage_text);
        }

        public void bind(final GroupItemInfo item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

