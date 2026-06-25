package com.example.whd.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.Models.WorkCreate;
import com.example.whd.Connection.Models.WorkEntryResponse;
import com.example.whd.Fragments.ListActivity.OnDialogWorkEntrySavedListener;
import com.example.whd.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.slider.Slider;

public class DialogAddListItemFragment extends DialogFragment {

    private OnDialogWorkEntrySavedListener listener;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    // Konstruktor für den Listener
    public DialogAddListItemFragment(OnDialogWorkEntrySavedListener listener) {
        this.listener = listener;
    }

    public DialogAddListItemFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_list_item, null);

        TextInputEditText startDate = view.findViewById(R.id.gui_TextInputEditText_DateStart);
        TextInputEditText startTime = view.findViewById(R.id.gui_TextInputEditText_TimeStart);

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

        TextInputEditText endDate = view.findViewById(R.id.gui_TextInputEditText_DateEnd);
        TextInputEditText endTime = view.findViewById(R.id.gui_TextInputEditText_TimeEnd);

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

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Arbeitsschicht")
                .setView(view)
                .setPositiveButton("Speichern", (dialog, which) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    String startISO = sdf.format(startCalendar.getTime());
                    String endISO = sdf.format(endCalendar.getTime());
                    EditText editText = (EditText) view.findViewById(R.id.gui_EditText_addListItem_BreakMinutes);
                    int breakMins = Integer.parseInt(editText.getText().toString());

                    WorkCreate entry = new WorkCreate(startISO, endISO, breakMins);
                    ApiManager.getInstance().getService().createEntry(entry).enqueue(new Callback<WorkEntryResponse>() {
                        @Override
                        public void onResponse(Call<WorkEntryResponse> call, Response<WorkEntryResponse> response) {
                            if (response.isSuccessful() && listener != null) {
                                listener.onSave();
                            }
                            dismiss();
                        }
                        @Override
                        public void onFailure(Call<WorkEntryResponse> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Abbrechen", null)
                .create();
    }
}
