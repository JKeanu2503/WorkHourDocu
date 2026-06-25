package com.example.whd.Dialogs;

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
import com.example.whd.Connection.Models.ScheduleCreate;
import com.example.whd.Connection.Models.ScheduleEntryResponse;
import com.example.whd.Fragments.ScheduleActivity.OnDialogScheduleEntrySavedListener;
import com.example.whd.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogAddScheduleItemFragment extends DialogFragment {

    private OnDialogScheduleEntrySavedListener listener;

    private Calendar dateCalendar = Calendar.getInstance();


    // Constructors
    public DialogAddScheduleItemFragment (OnDialogScheduleEntrySavedListener listener) {
        this.listener = listener;
    }

    public DialogAddScheduleItemFragment() {}

    // Overridden Methods


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_schedule_item, null);

        TextInputEditText timeDate = view.findViewById(R.id.gui_TextInputEditText_AddSchedule_DateStart);

        // Date Picker - TimeDate
        timeDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                dateCalendar.setTimeInMillis(selection);
                timeDate.setText(datePicker.getHeaderText());
            });
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Schichtplan")
                .setView(view)
                .setPositiveButton("Speichern", (dialog, which) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateISO = sdf.format(dateCalendar.getTime());
                    EditText et_targetHours = (EditText) view.findViewById(R.id.gui_EditText_AddSchedule_TargetHours);
                    float hours = (float) Float.parseFloat(et_targetHours.getText().toString());

                    ScheduleCreate entry = new ScheduleCreate(hours, dateISO);
                    ApiManager.getInstance().getService().createSchedule(entry).enqueue(new Callback<ScheduleEntryResponse>() {
                        @Override
                        public void onResponse(Call<ScheduleEntryResponse> call, Response<ScheduleEntryResponse> response) {
                            try {
                                String error = response.errorBody() != null ? response.errorBody().string() : "Kein Fehler-Body";
                                Log.e("API_DEBUG", "FEHLER " + response.code() + ": " + error);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (response.isSuccessful() && listener != null) {
                                listener.onSave();
                            }
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<ScheduleEntryResponse> call, Throwable t) {

                        }
                    });
                })
                .setNegativeButton("Abbrechen", null)
                .create();
    }
}
