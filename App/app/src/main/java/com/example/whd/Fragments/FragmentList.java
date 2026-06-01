package com.example.whd.Fragments;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.whd.R;

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
    }

    // Overridden Methods ==========================================================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        this.gui_RecyclerView_List = (RecyclerView) view.findViewById(R.id.gui_RecycleView_ListElements);
        this.gui_ImageButton_List_TopToolBar_SortDirection = (ImageButton) view.findViewById(R.id.gui_ImageButton_List_ToolBar_SortDirection);
        this.gui_ImageButton_List_TopToolBar_Time = (ImageButton) view.findViewById(R.id.gui_ImageButton_List_ToolBar_SortTime);
        this.gui_ImageButton_List_TopToolBar_Add = (ImageButton) view.findViewById(R.id.gui_ImageButton_List_ToolBar_AddRecord);
        this.gui_EditText_List_TopToolBar_Search = (EditText) view.findViewById(R.id.gui_EditText_List_ToolBar_Search);

        this.add_OnClickListener_to_GUIs();
        return view;
    }


    // Public Support-Methods ======================================================================

    // Private Support-Methods =====================================================================
    private void add_OnClickListener_to_GUIs () {
        this.gui_ImageButton_List_TopToolBar_SortDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "SortDirection", Toast.LENGTH_SHORT).show();
            }
        });
        this.gui_ImageButton_List_TopToolBar_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Time", Toast.LENGTH_SHORT).show();
            }
        });
        this.gui_ImageButton_List_TopToolBar_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Add", Toast.LENGTH_SHORT).show();
            }
        });
        this.gui_EditText_List_TopToolBar_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Toast.makeText(getContext(), charSequence.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
