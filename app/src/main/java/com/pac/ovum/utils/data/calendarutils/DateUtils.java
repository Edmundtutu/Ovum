package com.pac.ovum.utils.data.calendarutils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public Date parseDate(String dateStr) throws ParseException {
        return inputFormat.parse(dateStr);
    }

    public String formatDate(Date date) {
        return outputFormat.format(date);
    }

    /**
     * Formats a date string into a spoken format (e.g., "21st Feb").
     *
     * @param dateStr the date string to format
     * @return the formatted date string or an error message if the format is invalid
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String formatDateToSpeech(String dateStr) {
        String[] inputFormats = {
                "yyyy-MM-dd", "dd-MM-yyyy", "MM-dd-yyyy", "d-M-yyyy", "M-d-yyyy"
        };

        // Define output format
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("d'th' MMMM yyyy", Locale.getDefault());

        for (String format : inputFormats) {
            try {
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(format, Locale.getDefault());
                LocalDate date = LocalDate.parse(dateStr, inputFormat);
                return outputFormat.format(date);
            } catch (DateTimeParseException ignored) {
                // Ignore and try next format
            }
        }

        return "Invalid date format";
    }

    /**
     * Calculates the age based on the date of birth.
     *
     * @param dob the date of birth in "d-M-yyyy" format
     * @return the age as a string or an error message if the format is invalid
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String calculateAge(String dob) {
        if (dob == null || dob.isEmpty()) {
            return "Date of birth cannot be null or empty";
        }

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d-M-yyyy", Locale.getDefault());
        try {
            LocalDate dateOfBirth = LocalDate.parse(dob, dateFormat);
            LocalDate currentDate = LocalDate.now();
            return String.valueOf(currentDate.getYear() - dateOfBirth.getYear());
        } catch (DateTimeParseException e) {
            return "Invalid date format";
        }
    }

    /**
     * Converts a date string to milliseconds since epoch.
     *
     * @param dateString the date string in "yyyy-MM-dd" format
     * @return the milliseconds since epoch or 0 if the format is invalid
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long convertToMilliseconds(String dateString) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(dateString, dateFormat);
            return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            return 0; // Return 0 for invalid format
        }
    }

    /**
     * Converts a date from speech format to LocalDate format.
     *
     * @param dateInSpeech the date in speech format
     * @return the LocalDate or null if the format is invalid
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate formatDateToLocalDate(String dateInSpeech) {
        String[] spokenFormats = {
                "dd-MM-yyyy",
                "d'th' MMMM yyyy",
                "MMMM d'th', yyyy",
                "d'th' MMMM",
                "MMMM d'th'"
        };

        for (String format : spokenFormats) {
            try {
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(format, Locale.getDefault());
                return LocalDate.parse(dateInSpeech, inputFormat);
            } catch (DateTimeParseException ignored) {
                // Ignore and try next format
            }
        }

        return null;
    }
}
