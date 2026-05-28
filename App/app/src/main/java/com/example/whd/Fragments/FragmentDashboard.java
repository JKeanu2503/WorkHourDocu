package com.example.whd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whd.R;

public class FragmentDashboard extends Fragment {

    //==============================================================================================

    // Constructor
    public FragmentDashboard () {

    }

    // Overridden Methods
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
