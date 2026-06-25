package com.example.whd.Fragments.ScheduleActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whd.Connection.Models.ScheduleEntryResponse;
import com.example.whd.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick (ScheduleEntryResponse entry);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }

    // Private Attributes ==========================================================================
    private List<ScheduleEntryResponse> dataList;

    // Constructor =================================================================================
    public ScheduleAdapter (List<ScheduleEntryResponse> body) {
        this.dataList = body;
    }

    // Overridden Methods ==========================================================================
    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);

        View scheduleViewElement = lf.inflate(R.layout.item_schedule_entry, parent, false);
        ViewHolder viewHolder = new ViewHolder(scheduleViewElement);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        ScheduleEntryResponse item = this.dataList.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });


        TextView tv_ScheduleItem_ID = holder.gui_TextView_Schedule_ScheduleItem_ID;
        TextView tv_ScheduleItem_TimeDate = holder.gui_TextView_Schedule_ScheduleItem_TimeDate;
        TextView tv_ScheduleItem_Hours = holder.gui_TextView_Schedule_ScheduleItem_Hours;

        // Datum formatieren
        String isoDate = item.getValidFrom(); // Das Datum aus dem Backend (z.B. "2026-06-13")
        String niceDate = isoDate; // Fallback

        try {
            // 1. Definiere das Format, das vom Backend kommt
            SimpleDateFormat backendFormat = new SimpleDateFormat("yyyy-MM-dd");
            // 2. Definiere das Format, das der User sehen soll
            SimpleDateFormat userFormat = new SimpleDateFormat("dd.MM.yyyy");

            Date date = backendFormat.parse(isoDate);
            if (date != null) {
                niceDate = userFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Falls das Format mal nicht stimmt
        }

        tv_ScheduleItem_ID.setText("#" + item.getId());
        tv_ScheduleItem_TimeDate.setText(niceDate);
        tv_ScheduleItem_Hours.setText(item.getWeeklyHoursTarget() + "h");
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    // Public Class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Private GUI-Elements ====================================================================
        public TextView gui_TextView_Schedule_ScheduleItem_ID;
        public TextView gui_TextView_Schedule_ScheduleItem_TimeDate;
        public TextView gui_TextView_Schedule_ScheduleItem_Hours;


        public ViewHolder (@NonNull View itemView) {
            super (itemView);

            this.gui_TextView_Schedule_ScheduleItem_ID = (TextView) itemView.findViewById(R.id.gui_TextView_Schedule_ScheduleItem_ID);
            this.gui_TextView_Schedule_ScheduleItem_TimeDate = (TextView) itemView.findViewById(R.id.gui_TextView_Schedule_ScheduleItem_TimeDate);
            this.gui_TextView_Schedule_ScheduleItem_Hours = (TextView) itemView.findViewById(R.id.gui_TextView_Schedule_ScheduleItem_Hours);
        }
    }
}
