package com.example.whd.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.Models.ScheduleEntryResponse;
import com.example.whd.Connection.Models.ScheduleUpdate;
import com.example.whd.Connection.Models.ScheduleUpdateResponse;
import com.example.whd.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogUpdateScheduleItemFragment extends DialogFragment {
    private ScheduleEntryResponse entry;
    private Callback callback;

    private Calendar dateCalendar = Calendar.getInstance();

    public interface Callback {
        void onSave();
        void onDelete();
    }

    public DialogUpdateScheduleItemFragment(ScheduleEntryResponse entry, Callback callback) {
        this.entry = entry;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.initCalendarWithEntryDate();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_schedule, null);

        EditText etTargetHours = view.findViewById(R.id.gui_EditText_UpdateSchedule_TargetHours);
        etTargetHours.setText(String.valueOf(entry.getWeeklyHoursTarget()));

        TextInputEditText timeDate = view.findViewById(R.id.gui_TextInputEditText_UpdateSchedule_DateStart);
        timeDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                dateCalendar.setTimeInMillis(selection);
                timeDate.setText(datePicker.getHeaderText());
            });
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });
        timeDate.setText(entry.getValidFrom());

        builder.setView(view)
                .setPositiveButton("Speichern", (d, w) -> {
                    performUpdate(etTargetHours.getText().toString());
                })
                .setNegativeButton("Löschen", (d, w) -> performDelete())
                .setNeutralButton("Abbrechen", null);

        return builder.create();
    }

    private void performUpdate(String hours) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateISO = sdf.format(dateCalendar.getTime());

        ApiManager.getInstance().getService().updateSchedule(entry.getId(), new ScheduleUpdate(Float.parseFloat(hours), dateISO)).enqueue(new retrofit2.Callback<ScheduleUpdateResponse>() {
            @Override
            public void onResponse(Call<ScheduleUpdateResponse> call, Response<ScheduleUpdateResponse> response) {
                if (response.isSuccessful() && callback != null) {
                    callback.onSave();
                    dismiss();
                } else {
                    try {
                        Log.e("API_ERROR", "Code: " + response.code() + " Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ScheduleUpdateResponse> call, Throwable t) {

            }
        });
    }

    private void performDelete() {
        ApiManager.getInstance().getService().deleteSchedule(entry.getId()).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && callback != null) {
                    callback.onDelete();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void initCalendarWithEntryDate() {
        SimpleDateFormat backendFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = backendFormat.parse(entry.getValidFrom());
            if (date != null) {
                dateCalendar.setTime(date); // Kalender auf das Datum des Eintrags setzen
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
