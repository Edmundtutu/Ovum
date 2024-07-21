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

