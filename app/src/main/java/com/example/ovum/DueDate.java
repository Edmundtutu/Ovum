package com.example.ovum;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat reportDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return reportDateFormat.format(dueDate);
    }

    public String speechFormat() {
        // Define the desired output format
        DateUtils dateUtils = new DateUtils();
        return dateUtils.formatDateToSpeech(dueDateString);

    }
}
