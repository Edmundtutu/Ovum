package com.example.ovum;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // method for fomarting a date in spoken i.e. if given 2002-2-21 it returns 21st Feb
    public String formatDateToSpeech(String dateStr) {
        String[] inputFormats = {
                "yyyy-MM-dd", "dd-MM-yyyy", "MM-dd-yyyy", "d-M-yyyy", "M-d-yyyy"
        };

        // Define output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("d'th' MMMM yyyy", Locale.getDefault());

        for (String format : inputFormats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                Date date = inputFormat.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException ignored) {
                // Ignore and try next format
            }
        }

        return "Invalid date format";
    }

    // method to calculate the Age given the DOB
    public String calculateAge(String dob) {
        if (dob == null || dob.isEmpty()) {
            return dob;
        }

        // Define the expected date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
        try {
            // Parse the date of birth string to a Date object
            Date dateOfBirth = dateFormat.parse(dob);

            // Get the current year
            Calendar currentDate = Calendar.getInstance();
            int currentYear = currentDate.get(Calendar.YEAR);

            // Get the year from the date of birth
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dateOfBirth);
            int birthYear = dobCalendar.get(Calendar.YEAR);

            // Calculate the age
            return String.valueOf(currentYear - birthYear);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }

    // method to convert date in millisecods
    public long convertToMilliseconds(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long milliseconds = 0;
        try {
            Date date = dateFormat.parse(dateString);
            milliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    // method to convert  date from speech format to LocalDate format
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate formatDateToLocalDate(String dateInSpeech) {
        String[] spokenFormats = {
                // for a fewer comparisons, lets remove some of the formats
                "dd-MM-YYYY",
//                "d'st' MMMM yyyy",   // 1st January 2024
//                "d'nd' MMMM yyyy",   // 2nd January 2024
//                "d'rd' MMMM yyyy",   // 3rd January 2024
                "d'th' MMMM yyyy",   // 4th January 2024
//                "MMMM d'st', yyyy",  // January 1st, 2024
//                "MMMM d'nd', yyyy",  // January 2nd, 2024
//                "MMMM d'rd', yyyy",  // January 3rd, 2024
                "MMMM d'th', yyyy",  // January 4th, 2024
//                "d'st' MMMM",        // 1st January
//                "d'nd' MMMM",        // 2nd January
//                "d'rd' MMMM",        // 3rd January
                "d'th' MMMM",        // 4th January
//                "MMMM d'st'",        // January 1st
//                "MMMM d'nd'",        // January 2nd
//                "MMMM d'rd'",        // January 3rd
                "MMMM d'th'"         // January 4th
        };


        for (String format : spokenFormats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                Date date = inputFormat.parse(dateInSpeech);
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException ignored) {
                // Ignore and try next format
            }
        }

        return null;
    }
}
