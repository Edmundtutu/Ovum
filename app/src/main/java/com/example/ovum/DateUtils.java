package com.example.ovum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // method for fomarting a date in spoken i.e. if given 2002-2-21 it returns 21st Feb
    public String formatDateToSpeech(String dateStr) {
        // Define input formats (add as many as needed)
        String[] inputFormats = {
                "yyyy-MM-dd", "dd-MM-yyyy", "MM-dd-yyyy", "d-M-yyyy", "M-d-yyyy"
        };

        // Define output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("d'th' MMMM", Locale.getDefault());

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


}
