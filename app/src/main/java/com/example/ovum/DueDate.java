package com.example.ovum;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DueDate {
    private String dueDateString;
    private Date dueDate;

    public DueDate(String dueDateString) throws ParseException {
        this.dueDateString = dueDateString;
        // Define the format of the stored date
        SimpleDateFormat storedDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Parse the stored date
        this.dueDate = storedDateFormat.parse(dueDateString);
    }

    public String reportFormat() {
        // Define the desired output format
//        SimpleDateFormat reportDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        return reportDateFormat.format(dueDate);
        return dueDateString;
    }

    public String speechFormat() {
        // Define the desired output format
        DateUtils dateUtils = new DateUtils();
        return dateUtils.formatDateToSpeech(dueDateString);

    }

    // method that returns dates associated with the dueDate
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LocalDate> getDatesList() {
//        dueDateString = "8-8-2024";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dueDate = LocalDate.parse(dueDateString, formatter);

        ArrayList<LocalDate> datesList = new ArrayList<>();
        datesList.add(dueDate);
        datesList.add(dueDate.minusDays(1));
        datesList.add(dueDate.minusDays(2));
        datesList.add(dueDate.plusDays(1));
        datesList.add(dueDate.plusDays(2));

        return datesList;
    }


}
