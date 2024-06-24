package com.example.ovum;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/*
 * This file is part of the Ovum project.
 *
 *
 *  This java class is meant to be used as a fragment to show a date picker dialog for all input fields that require a date
 *
 * For more light see the comment in UserIntention.java class where it has been used as an inner class
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int textViewId;
    private OnDateSetListener callback;

    public DatePickerFragment(int textViewId, OnDateSetListener callback) {
        this.textViewId = textViewId;
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (callback != null) {
            callback.onDateSet(textViewId, year, month, day);
        } else {
            Log.e("DatePickerFragment", "Callback is null.");
        }
    }

    public interface OnDateSetListener {
        void onDateSet(int textViewId, int year, int month, int day);
    }

}




//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.widget.DatePicker;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.DialogFragment;
//
//import java.util.Calendar;
//
//public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//    private OnDateSelectedListener listener;
//
//    public interface OnDateSelectedListener {
//        void onDateSelected(int year, int month, int dayOfMonth);
//    }
//
//    private static final String ARG_EDIT_TEXT_ID = "edit_text_id";
//
//    public static DatePickerFragment newInstance(int editTextId) {
//        DatePickerFragment fragment = new DatePickerFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_EDIT_TEXT_ID, editTextId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        // Create a new instance of DatePickerDialog and return it
//        return new DatePickerDialog(requireActivity(), this, year, month, day);
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            listener = (OnDateSelectedListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnDateSelectedListener");
//        }
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        if (listener != null) {
//            listener.onDateSelected(year, month, dayOfMonth);
//        }
//    }
//}
