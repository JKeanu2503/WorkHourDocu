package com.example.whd.Fragments.ListActivity;

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
import com.example.whd.Connection.Models.WorkEntryResponse;
import com.example.whd.Dialogs.DialogAddListItemFragment;
import com.example.whd.Dialogs.DialogUpdateListItemFragment;
import com.example.whd.Fragments.ScheduleActivity.FragmentSchedule;
import com.example.whd.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentList extends Fragment {

    // Public Enumeration ==========================================================================
    public enum SortDirection {
        UP,
        DOWN,
    }
    public enum ChronoStatus {
        CHRONOLOGICAL,
        HISTORICAL,
    }

    // Private Attributes ==========================================================================
    private FragmentList.SortDirection sort_Direction;
    private FragmentList.ChronoStatus chrono_Status;

    private List<WorkEntryResponse> allEntries;
    private ListAdapter listAdapter;

    // Private GUI-Elements ========================================================================
    private RecyclerView gui_RecyclerView_List;
    private ImageButton gui_ImageButton_List_TopToolBar_SortDirection;
    private ImageButton gui_ImageButton_List_TopToolBar_Time;
    private ImageButton gui_ImageButton_List_TopToolBar_Add;
    private EditText gui_EditText_List_TopToolBar_Search;

    // Constructor =================================================================================
    public FragmentList () {
        this.gui_RecyclerView_List = null;
        this.gui_ImageButton_List_TopToolBar_SortDirection = null;
        this.gui_ImageButton_List_TopToolBar_Time = null;
        this.gui_ImageButton_List_TopToolBar_Add = null;
        this.gui_EditText_List_TopToolBar_Search = null;

        this.sort_Direction = SortDirection.UP;
        this.chrono_Status = ChronoStatus.CHRONOLOGICAL;
        this.allEntries = null;
        this.listAdapter = null;
    }

    // Overridden Methods ==========================================================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        this.gui_RecyclerView_List = (RecyclerView) view.findViewById(R.id.gui_RecycleView_ListElements);
        this.gui_ImageButton_List_TopToolBar_SortDirection = (ImageButton) view.findViewById(R.id.gui_ImageButton_List_ToolBar_SortDirection);
        this.gui_ImageButton_List_TopToolBar_Add = (ImageButton) view.findViewById(R.id.gui_ImageButton_List_ToolBar_AddRecord);
        this.gui_EditText_List_TopToolBar_Search = (EditText) view.findViewById(R.id.gui_EditText_List_ToolBar_Search);

        this.add_Listener_to_GUIs();

        this.loadDataFromAPI();
        return view;
    }

    // Public Support-Methods ======================================================================

    // Private Support-Methods =====================================================================
    private void loadDataFromAPI() {
        ApiManager.getInstance().getService().getAllEntries().enqueue(new Callback<List<WorkEntryResponse>>() {
            @Override
            public void onResponse(Call<List<WorkEntryResponse>> call, Response<List<WorkEntryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEntries = response.body();
                    updateList(allEntries);
                }
            }

            @Override
            public void onFailure(Call<List<WorkEntryResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Laden fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void add_Listener_to_GUIs() {
        this.gui_ImageButton_List_TopToolBar_SortDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_Direction = (sort_Direction == SortDirection.UP) ? SortDirection.DOWN : SortDirection.UP;

                if (sort_Direction == SortDirection.UP) {
                    gui_ImageButton_List_TopToolBar_SortDirection.setImageResource(R.drawable.icon_arrow_up);
                } else {
                    gui_ImageButton_List_TopToolBar_SortDirection.setImageResource(R.drawable.icon_arrow_down);
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

                gui_RecyclerView_List.setLayoutManager(layoutManager);
            }
        });
        this.gui_ImageButton_List_TopToolBar_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_AddDialog();
            }
        });
        this.gui_EditText_List_TopToolBar_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void filterList (String query) {
        if (this.allEntries == null) {
            return;
        }

        if (query.length() == 0) {
            this.updateList(allEntries);
            return;
        }

        List<WorkEntryResponse> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        SimpleDateFormat backendsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat frontendsdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

        for (WorkEntryResponse item: allEntries) {
            Date start = null;
            Date end = null;
            try {
                start = backendsdf.parse(item.getStartTime());
                end = backendsdf.parse(item.getEndTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            String dateStartStr = frontendsdf.format(start);
            String dateEndStr = frontendsdf.format(end);

            String idStr = String.valueOf(item.getId());
            String breakStr = String.valueOf(item.getBreakMinutes());

            if (idStr.contains(lowerQuery) || dateStartStr.contains(lowerQuery) || dateEndStr.contains(lowerQuery) || breakStr.contains(lowerQuery)) {
                filteredList.add(item);
            }
        }

        this.updateList(filteredList);
    }

    private void updateList (List<WorkEntryResponse> data) {
        listAdapter = new ListAdapter(data);
        listAdapter.setOnItemClickListener(entry -> {
            openEditDialog(entry);
        });
        gui_RecyclerView_List.setAdapter(listAdapter);
        gui_RecyclerView_List.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void openEditDialog(WorkEntryResponse entry) {
        DialogUpdateListItemFragment dialog = new DialogUpdateListItemFragment(entry, new DialogUpdateListItemFragment.Callback() {
            @Override
            public void onSave() { loadDataFromAPI(); }
            @Override
            public void onDelete() { loadDataFromAPI(); }
        });
        dialog.show(getParentFragmentManager(), "EditListDialog");
    }

    private void open_AddDialog() {
        DialogAddListItemFragment dialog = new DialogAddListItemFragment(() -> {
            loadDataFromAPI();
            Toast.makeText(getContext(), "Gespeichert!", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getParentFragmentManager(), "AddDialog");
    }
}
