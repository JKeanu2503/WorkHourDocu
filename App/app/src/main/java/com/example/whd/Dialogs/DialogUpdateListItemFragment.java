package com.example.whd.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.Models.EntryUpdate;
import com.example.whd.Connection.Models.WorkEntryResponse;
import com.example.whd.Connection.Models.WorkEntryUpdateResponse;
import com.example.whd.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogUpdateListItemFragment extends DialogFragment {
    private WorkEntryResponse entry;
    private Callback callback;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    public interface Callback {
        void onSave();
        void onDelete();
    }

    public DialogUpdateListItemFragment(WorkEntryResponse entry, Callback callback) {
        this.entry = entry;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.initCalendarWithEntryData();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_list, null);

        SimpleDateFormat dateFmt = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");

        ((TextInputEditText) view.findViewById(R.id.gui_TextInputEditText_UpdateDateStart)).setText(dateFmt.format(startCalendar.getTime()));
        ((TextInputEditText) view.findViewById(R.id.gui_TextInputEditText_UpdateTimeStart)).setText(timeFmt.format(startCalendar.getTime()));
        ((TextInputEditText) view.findViewById(R.id.gui_TextInputEditText_UpdateDateEnd)).setText(dateFmt.format(endCalendar.getTime()));
        ((TextInputEditText) view.findViewById(R.id.gui_TextInputEditText_UpdateTimeEnd)).setText(timeFmt.format(endCalendar.getTime()));
        ((EditText) view.findViewById(R.id.gui_EditText_addListItem_UpdateBreakMinutes)).setText(String.valueOf(entry.getBreakMinutes()));

        TextInputEditText startDate = view.findViewById(R.id.gui_TextInputEditText_UpdateDateStart);
        TextInputEditText startTime = view.findViewById(R.id.gui_TextInputEditText_UpdateTimeStart);

        // Date Picker - Start
        startDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                startCalendar.setTimeInMillis(selection);
                startDate.setText(datePicker.getHeaderText());
            });
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        // Time Picker - Start
        startTime.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                startCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                startCalendar.set(Calendar.MINUTE, timePicker.getMinute());
                startTime.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()));
            });
            timePicker.show(getParentFragmentManager(), "TIME_PICKER");
        });

        TextInputEditText endDate = view.findViewById(R.id.gui_TextInputEditText_UpdateDateEnd);
        TextInputEditText endTime = view.findViewById(R.id.gui_TextInputEditText_UpdateTimeEnd);

        // Date Picker - End
        endDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                endCalendar.setTimeInMillis(selection);
                endDate.setText(datePicker.getHeaderText());
            });
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        // Time Picker - End
        endTime.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                endCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                endCalendar.set(Calendar.MINUTE, timePicker.getMinute());
                endTime.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()));
            });
            timePicker.show(getParentFragmentManager(), "TIME_PICKER");
        });

        builder.setView(view)
                .setPositiveButton("Speichern", (d, w) -> performUpdate(view))
                .setNegativeButton("Löschen", (d, w) -> performDelete())
                .setNeutralButton("Abbrechen", null);
        return builder.create();
    }

    private void performUpdate(View view) {
        // 1. Pause auslesen
        EditText etBreak = view.findViewById(R.id.gui_EditText_addListItem_UpdateBreakMinutes);
        if (etBreak.getText().toString().length() == 0) {
            etBreak.setText("0");
        }
        int breakMins = Integer.parseInt(etBreak.getText().toString());

        // 2. Datum/Zeit in ISO-Format wandeln
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String startISO = isoFormat.format(startCalendar.getTime());
        String endISO = isoFormat.format(endCalendar.getTime());

        // 3. API Call
        EntryUpdate updateObj = new EntryUpdate(startISO, endISO, breakMins);
        ApiManager.getInstance().getService().updateEntry(entry.getId(), updateObj).enqueue(new retrofit2.Callback<WorkEntryUpdateResponse>() {
            @Override
            public void onResponse(Call<WorkEntryUpdateResponse> call, Response<WorkEntryUpdateResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSave();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<WorkEntryUpdateResponse> call, Throwable t) {

            }
        });
    }

    private void performDelete() {
        ApiManager.getInstance().getService().deleteEntry(entry.getId()).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onDelete();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    // In DialogUpdateListItemFragment ergänzen
    private void initCalendarWithEntryData() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date start = isoFormat.parse(entry.getStartTime());
            Date end = isoFormat.parse(entry.getEndTime());
            if (start != null) startCalendar.setTime(start);
            if (end != null) endCalendar.setTime(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}