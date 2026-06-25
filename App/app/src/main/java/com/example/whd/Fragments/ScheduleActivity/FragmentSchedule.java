package com.example.whd.Fragments.ScheduleActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whd.Connection.ApiManager;
import com.example.whd.Connection.Models.ScheduleEntryResponse;
import com.example.whd.Dialogs.DialogAddScheduleItemFragment;
import com.example.whd.Dialogs.DialogUpdateScheduleItemFragment;
import com.example.whd.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSchedule extends Fragment {

    enum SortDirection {
        UP,
        DOWN
    }

    // Private Attributes ==========================================================================
    private FragmentSchedule.SortDirection sort_Direction;

    private List<ScheduleEntryResponse> allEntries;

    // Private GUI-Elements ========================================================================
    private ImageButton gui_ImageButton_Schedule_SortDirection;
    private ImageButton gui_ImageButton_Schedule_Add;
    private EditText gui_EditText_Schedule_Search;
    private RecyclerView gui_RecyclerView_Schedule_ScheduleList;

    // Constructor =================================================================================
    public FragmentSchedule() {
        this.gui_ImageButton_Schedule_SortDirection = null;
        this.gui_ImageButton_Schedule_Add = null;
        this.gui_EditText_Schedule_Search = null;
        this.gui_RecyclerView_Schedule_ScheduleList = null;

        this.sort_Direction = FragmentSchedule.SortDirection.UP;
        this.allEntries = null;
    }

    // Overridden Methods ==========================================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        this.gui_ImageButton_Schedule_SortDirection = (ImageButton) view.findViewById(R.id.gui_ImageButton_Schedule_ToolBar_SortDirection);
        this.gui_ImageButton_Schedule_Add = (ImageButton) view.findViewById(R.id.gui_ImageButton_Schedule_ToolBar_AddRecord);
        this.gui_EditText_Schedule_Search = (EditText) view.findViewById(R.id.gui_EditText_Schedule_ToolBar_Search);
        this.gui_RecyclerView_Schedule_ScheduleList = (RecyclerView) view.findViewById(R.id.gui_RecycleView_ScheduleElements);

        this.add_Listener_to_GUIs();

        this.loadDataFromAPI();
        return view;
    }

    // Private Support-Methods =====================================================================
    private void loadDataFromAPI() {
        ApiManager.getInstance().getService().getAllSchedule().enqueue(new Callback<List<ScheduleEntryResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduleEntryResponse>> call, Response<List<ScheduleEntryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEntries = response.body();
                    updateScheduleList(allEntries);
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleEntryResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Laden fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void add_Listener_to_GUIs() {
        this.gui_ImageButton_Schedule_SortDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_Direction = (sort_Direction == SortDirection.UP) ? SortDirection.DOWN : SortDirection.UP;

                if (sort_Direction == SortDirection.UP) {
                    gui_ImageButton_Schedule_SortDirection.setImageResource(R.drawable.icon_arrow_up);
                } else {
                    gui_ImageButton_Schedule_SortDirection.setImageResource(R.drawable.icon_arrow_down);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

                // Wenn DOWN, dann umkehren
                if (sort_Direction == SortDirection.DOWN) {
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true); // Wichtig: Damit die Liste unten anfängt
                    Toast.makeText(getContext(), "Sortierung: Absteigend", Toast.LENGTH_SHORT).show();
                } else {
                    layoutManager.setReverseLayout(false);
                    layoutManager.setStackFromEnd(false);
                    Toast.makeText(getContext(), "Sortierung: Aufsteigend", Toast.LENGTH_SHORT).show();
                }

                gui_RecyclerView_Schedule_ScheduleList.setLayoutManager(layoutManager);
            }
        });

        this.gui_ImageButton_Schedule_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_AddDialog();
            }
        });

        this.gui_EditText_Schedule_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterScheduleList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void updateScheduleList(List<ScheduleEntryResponse> data) {
        ScheduleAdapter adapter = new ScheduleAdapter(data);
        adapter.setOnItemClickListener(entry -> {
            openEditDialog(entry);
        });
        gui_RecyclerView_Schedule_ScheduleList.setAdapter(adapter);
        gui_RecyclerView_Schedule_ScheduleList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void filterScheduleList(String query) {
        if (this.allEntries == null) {
            return;
        }

        if (query.length() == 0) {
            this.updateScheduleList(this.allEntries);
            return;
        }

        List<ScheduleEntryResponse> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        SimpleDateFormat backendsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat frontendsdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

        for (ScheduleEntryResponse item : this.allEntries) {
            Date date = null;
            try {
                date = backendsdf.parse(item.getValidFrom());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String idStr = String.valueOf(item.getId());
            String dateStr = frontendsdf.format(date);
            String targetHour = String.valueOf(item.getWeeklyHoursTarget());

            if (idStr.contains(lowerQuery) || dateStr.contains(lowerQuery) || targetHour.contains(lowerQuery)) {
                filteredList.add(item);
            }
        }

        this.updateScheduleList(filteredList);
    }

    private void openEditDialog(ScheduleEntryResponse entry) {
        // Übergib das Objekt an den Dialog
        DialogUpdateScheduleItemFragment dialog = new DialogUpdateScheduleItemFragment(entry, new DialogUpdateScheduleItemFragment.Callback() {
            @Override
            public void onSave() {
                loadDataFromAPI(); // Liste aktualisieren
            }
            @Override
            public void onDelete() {
                loadDataFromAPI();
            }
        });
        dialog.show(getParentFragmentManager(), "EditDialog");
    }

    private void open_AddDialog() {
        DialogAddScheduleItemFragment dialog = new DialogAddScheduleItemFragment(() -> {
            loadDataFromAPI();
            Toast.makeText(getContext(), "Gespeichert!", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getParentFragmentManager(), "AddDialog");
    }
}