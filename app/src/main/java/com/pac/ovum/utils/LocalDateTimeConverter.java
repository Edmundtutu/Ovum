package com.pac.ovum.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDate;

public class LocalDateTimeConverter {
    @TypeConverter
    public static String localdateTimeToSTring(LocalDate date){
        return date.toString();
    }
    @TypeConverter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate stringToLocaldate(String date){
        return LocalDate.parse(date);
    }
}
