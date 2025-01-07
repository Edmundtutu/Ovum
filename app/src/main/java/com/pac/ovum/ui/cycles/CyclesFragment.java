package com.pac.ovum.ui.cycles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pac.ovum.databinding.FragmentCyclesBinding;

public class CyclesFragment extends Fragment {

    private FragmentCyclesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CyclesViewModel cyclesViewModel =
                new ViewModelProvider(this).get(CyclesViewModel.class);

        binding = FragmentCyclesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textSlideshow;
//        cyclesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}