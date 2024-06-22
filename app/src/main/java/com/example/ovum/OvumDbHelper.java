package com.example.ovum;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OvumDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Ovum.db";

    public OvumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Patients table
        String SQL_CREATE_PATIENTS_TABLE = "CREATE TABLE " + OvumContract.PatientEntry.TABLE_NAME + " (" +
                OvumContract.PatientEntry._ID + " INTEGER PRIMARY KEY," +
                OvumContract.PatientEntry.COLUMN_NAME + " TEXT," +
                OvumContract.PatientEntry.COLUMN_PHONE_NUMBER + " TEXT," +
                OvumContract.PatientEntry.COLUMN_LOCATION + " TEXT," +
                OvumContract.PatientEntry.COLUMN_EMAIL + " TEXT," +
                OvumContract.PatientEntry.COLUMN_PASSWORD + " TEXT," +
                OvumContract.PatientEntry.COLUMN_PROFILE_PICTURE + " TEXT," +
                OvumContract.PatientEntry.COLUMN_DOB + " TEXT," +
                OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH + " INTEGER," +
                OvumContract.PatientEntry.COLUMN_PERIOD_LENGTH + " INTEGER," +
                OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE + " TEXT," +
                OvumContract.PatientEntry.COLUMN_LAST_PERIOD_END_DATE + " TEXT," +
                OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH + " INTEGER," +
                OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH + " INTEGER," +
                OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION + " TEXT," +
                OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD + " TEXT)";

        // Create Symptoms table
        String SQL_CREATE_SYMPTOMS_TABLE = "CREATE TABLE " + OvumContract.SymptomEntry.TABLE_NAME + " (" +
                OvumContract.SymptomEntry._ID + " INTEGER PRIMARY KEY," +
                OvumContract.SymptomEntry.COLUMN_PATIENT_ID + " INTEGER," +
                OvumContract.SymptomEntry.COLUMN_CATEGORY + " TEXT," +
                OvumContract.SymptomEntry.COLUMN_NAME + " TEXT," +
                OvumContract.SymptomEntry.COLUMN_DESCRIPTION + " TEXT," +
                OvumContract.SymptomEntry.COLUMN_DATE_RECORDED + " TEXT," +
                OvumContract.SymptomEntry.COLUMN_DATE_OCCURRED + " TEXT," +
                OvumContract.SymptomEntry.COLUMN_SEVERITY + " INTEGER," +
                "FOREIGN KEY (" + OvumContract.SymptomEntry.COLUMN_PATIENT_ID + ") REFERENCES " +
                OvumContract.PatientEntry.TABLE_NAME + "(" + OvumContract.PatientEntry._ID + "))";

        db.execSQL(SQL_CREATE_PATIENTS_TABLE);
        db.execSQL(SQL_CREATE_SYMPTOMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + OvumContract.PatientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OvumContract.SymptomEntry.TABLE_NAME);
        onCreate(db);
    }

    // method that fetches the patient's data from the database according to their id and email
    public Cursor getPatient(int id, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                OvumContract.PatientEntry._ID,
                OvumContract.PatientEntry.COLUMN_NAME,
                OvumContract.PatientEntry.COLUMN_PHONE_NUMBER,
                OvumContract.PatientEntry.COLUMN_LOCATION,
                OvumContract.PatientEntry.COLUMN_EMAIL,
                OvumContract.PatientEntry.COLUMN_PASSWORD,
                OvumContract.PatientEntry.COLUMN_PROFILE_PICTURE,
                OvumContract.PatientEntry.COLUMN_DOB,
                OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH,
                OvumContract.PatientEntry.COLUMN_PERIOD_LENGTH,
                OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE,
                OvumContract.PatientEntry.COLUMN_LAST_PERIOD_END_DATE,
                OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH,
                OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH,
                OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION,
                OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD
        };
        String selection = OvumContract.PatientEntry._ID + " = ? AND " + OvumContract.PatientEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {String.valueOf(id), email};
        Cursor cursor = db.query(
                OvumContract.PatientEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }


}
