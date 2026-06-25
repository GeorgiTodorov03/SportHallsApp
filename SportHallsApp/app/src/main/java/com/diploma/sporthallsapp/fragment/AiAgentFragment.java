package com.diploma.sporthallsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diploma.sporthallsapp.R;

public class AiAgentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // За момента връщаме прост изглед, после ще сложим реалните XML дизайни
        return inflater.inflate(R.layout.fragment_ai, container, false);
    }
}
