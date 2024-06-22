package com.example.ovum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ovum.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private String DOB;
    private String DueDate;
    private String userName;
    private String userEmail;

    public static ProfileFragment newInstance(String param1, String param2, String param3, String param4) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve arguments
        if (getArguments() != null) {
            DOB = getArguments().getString(ARG_PARAM1);
            DueDate = getArguments().getString(ARG_PARAM2);
            userName = getArguments().getString(ARG_PARAM3);
            userEmail = getArguments().getString(ARG_PARAM4);
        }

        // Set values to TextViews
        setValues(DOB, DueDate, userName, userEmail);
    }

    private void setValues(String dob, String dueDate, String userName, String userEmail) {
        if (binding != null) {
            // Calculate the age from the DOB first (For debugging, returning DOB as age)
            String age = calculateAge(dob);
            // Get the account type from the database (For now, returning a default value)
            String accountType = getAccountType();

            // Set the text views with the values
            binding.userDueDate.setText(dueDate);
            binding.userAge.setText(age);
            binding.accountType.setText(accountType);
            binding.userUsername.setText(userName);
            binding.userEmail.setText(userEmail);
        }
    }

    private String calculateAge(String dob) {
        // to ensure everything works correctly lets add conditions
        if (dob == null || dob.isEmpty()) {
            return dob;
        }else{
             // get current year
             int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
             // get the year from the dob
             int year = Integer.parseInt(dob.substring(0, 4));
             // calculate the age
             return String.valueOf(currentYear - year);
        }
    }

    private String getAccountType() {
        // For now, returning a default value (not yet implemented in DB)
        return "Period Tracking";
    }

}
