package com.cssoftwaretech.smm.gp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cssoftwaretech.smm.R;
import com.cssoftwaretech.smm.database.DB_Groups;
import java.util.List;

import static com.cssoftwaretech.smm.Settings.getContactImage;
import static com.cssoftwaretech.smm.Settings.getContactName;
import static com.cssoftwaretech.smm.Settings.getNameTowChar;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
    private List<MemberItemInfo> listItems;
    private Context context;
    private final OnItemClickListener listener;
    private DB_Groups db_groups;
    private static int selectedItem = -1;

    public MemberListAdapter(List<MemberItemInfo> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        db_groups = new DB_Groups(context);
        listener = null;
    }
    public interface OnItemClickListener {
        void onItemClick(MemberItemInfo item);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_itemview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MemberItemInfo listItem = listItems.get(position);
        holder.tvMemId.setText(listItem.getId());
        String name =  listItem.getName();
        holder.tvMemName.setText(getContactName(name,context));
        holder.tvMemNumber.setText(name);
        Bitmap photo = getContactImage(context, name);
        if(photo==null){
            String charName = getNameTowChar(getContactName(name, context));
            if(charName.equalsIgnoreCase("1")) {
                holder.imgMemPhoto.setImageResource(R.drawable.ic_person);
            }else{
                if(charName.charAt(1)=='*'){
                    holder.imgMemText.setText(charName.charAt(0)+"");
                }else {
                    holder.imgMemText.setText(charName);
                }
            }
        }else {
            holder.imgMemPhoto.setImageBitmap(photo);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMemId;
        private TextView tvMemName;
        private TextView tvMemNumber;
        private ImageView imgMemPhoto;
        private TextView imgMemText;
        public ViewHolder(View itemView) {
            super(itemView);
            tvMemId = (TextView) itemView.findViewById(R.id.con_tv_contact_id);
            tvMemName = (TextView) itemView.findViewById(R.id.con_tv_contact_name);
            tvMemNumber = (TextView) itemView.findViewById(R.id.con_tv_contact_number);
            imgMemPhoto = (ImageView)itemView.findViewById(R.id.con_img_contact);
            imgMemText = (TextView) itemView.findViewById(R.id.con_img_text);
        }
        public void bind(final MemberItemInfo item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }


}
class MemberItemInfo{
    private String id;
    private String name;

    public MemberItemInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}