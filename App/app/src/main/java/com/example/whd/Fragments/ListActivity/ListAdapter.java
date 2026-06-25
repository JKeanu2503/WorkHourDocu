package com.example.whd.Fragments.ListActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whd.Connection.Models.WorkEntryResponse;
import com.example.whd.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<WorkEntryResponse> data;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick (WorkEntryResponse entry);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }

    // Constructor
    public ListAdapter(List<WorkEntryResponse> body) {
        this.data = body;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);

        View listViewElement = lf.inflate(R.layout.item_work_entry, parent, false);
        ViewHolder viewHolder = new ViewHolder(listViewElement);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkEntryResponse item = this.data.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(data.get(position));
        });

        TextView tv_ListItem_ID = holder.gui_TextView_List_ListItem_ID;
        TextView tv_ListItem_TimeStart = holder.gui_TextView_List_ListItem_TimeStart;
        TextView tv_ListItem_TimeEnd = holder.gui_TextView_List_ListItem_TimeEnd;
        TextView tv_ListItem_BreakMinutes = holder.gui_TextView_List_ListItem_BreakMinutes;

        String isoDateStart = item.getStartTime();
        String isoDateEnd = item.getEndTime();
        String niceDateStart = isoDateStart;
        String niceDateEnd = isoDateEnd;

        try {
            SimpleDateFormat backendsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat frontendsdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

            Date startDate = backendsdf.parse(isoDateStart);
            Date endDate = backendsdf.parse(isoDateEnd);
            if (startDate != null || endDate != null) {
                niceDateStart = frontendsdf.format(startDate);
                niceDateEnd = frontendsdf.format(endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_ListItem_ID.setText("#" + item.getId());
        tv_ListItem_TimeStart.setText(niceDateStart);
        tv_ListItem_TimeEnd.setText(niceDateEnd);
        tv_ListItem_BreakMinutes.setText(item.getBreakMinutes() + "min");

    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Private GUI-Elements ====================================================================
        public TextView gui_TextView_List_ListItem_ID;
        public TextView gui_TextView_List_ListItem_TimeStart;
        public TextView gui_TextView_List_ListItem_TimeEnd;
        public TextView gui_TextView_List_ListItem_BreakMinutes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.gui_TextView_List_ListItem_ID = (TextView) itemView.findViewById(R.id.gui_TextView_List_ListItem_ID);
            this.gui_TextView_List_ListItem_TimeStart = (TextView) itemView.findViewById(R.id.gui_TextView_List_ListItem_TimeStart);
            this.gui_TextView_List_ListItem_TimeEnd = (TextView) itemView.findViewById(R.id.gui_TextView_List_ListItem_TimeEnd);
            this.gui_TextView_List_ListItem_BreakMinutes = (TextView) itemView.findViewById(R.id.gui_TextView_List_ListItem_BreakMinutes);
        }
    }
}
