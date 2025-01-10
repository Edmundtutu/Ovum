package com.pac.ovum.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocalDateTimeConverter {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @TypeConverter
    public static String localdateTimeToString(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate stringToLocaldate(String date) {
        return date == null ? null : LocalDate.parse(date);
    }

    @TypeConverter
    public static String localTimeToString(LocalTime time) {
        return time == null ? null : time.format(TIME_FORMATTER);
    }

    @TypeConverter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalTime stringToLocalTime(String time) {
        return time == null ? null : LocalTime.parse(time, TIME_FORMATTER);
    }
}
