package com.example.ovum;

import android.content.ContentValues;
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

        // create the Events Table
        String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + OvumContract.EventEntry.TABLE_NAME + " (" +
                OvumContract.EventEntry._ID + " INTEGER PRIMARY KEY," +
                OvumContract.EventEntry.COLUMN_PATIENT_ID + " INTEGER," +
                OvumContract.EventEntry.COLUMN_NAME + " TEXT," +
                OvumContract.EventEntry.COLUMN_DESCRIPTION + " TEXT," +
                OvumContract.EventEntry.COLUMN_DATE_RECORDED + " TEXT," +
                OvumContract.EventEntry.COLUMN_DATE_OCCURRED + " TEXT," +
                "FOREIGN KEY (" + OvumContract.EventEntry.COLUMN_PATIENT_ID + ") REFERENCES " +
                OvumContract.PatientEntry.TABLE_NAME + "(" + OvumContract.PatientEntry._ID + "))";

        db.execSQL(SQL_CREATE_PATIENTS_TABLE);
        db.execSQL(SQL_CREATE_SYMPTOMS_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + OvumContract.PatientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OvumContract.SymptomEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OvumContract.EventEntry.TABLE_NAME);
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

    // method to update the patient's data in the database
    public void updatePatient(int id, String name, String phoneNumber, String location, String email, String password, String profilePicture, String dob, int cycleLength, int periodLength, String lastPeriodDate, String lastPeriodEndDate, int averageCycleLength, int averagePeriodLength, String nextProbableDateOfOvulation, String nextProbableDateOfPeriod) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (name != null) values.put(OvumContract.PatientEntry.COLUMN_NAME, name);
        if (phoneNumber != null) values.put(OvumContract.PatientEntry.COLUMN_PHONE_NUMBER, phoneNumber);
        if (location != null) values.put(OvumContract.PatientEntry.COLUMN_LOCATION, location);
        if (email != null) values.put(OvumContract.PatientEntry.COLUMN_EMAIL, email);
        if (password != null) values.put(OvumContract.PatientEntry.COLUMN_PASSWORD, password);
        if (profilePicture != null) values.put(OvumContract.PatientEntry.COLUMN_PROFILE_PICTURE, profilePicture);
        if (dob != null) values.put(OvumContract.PatientEntry.COLUMN_DOB, dob);
        if (cycleLength != -1) values.put(OvumContract.PatientEntry.COLUMN_CYCLE_LENGTH, cycleLength); // Assuming -1 is a placeholder for not updating this field
        if (periodLength != -1) values.put(OvumContract.PatientEntry.COLUMN_PERIOD_LENGTH, periodLength);
        if (lastPeriodDate != null) values.put(OvumContract.PatientEntry.COLUMN_LAST_PERIOD_DATE, lastPeriodDate);
        if (lastPeriodEndDate != null) values.put(OvumContract.PatientEntry.COLUMN_LAST_PERIOD_END_DATE, lastPeriodEndDate);
        if (averageCycleLength != -1) values.put(OvumContract.PatientEntry.COLUMN_AVERAGE_CYCLE_LENGTH, averageCycleLength);
        if (averagePeriodLength != -1) values.put(OvumContract.PatientEntry.COLUMN_AVERAGE_PERIOD_LENGTH, averagePeriodLength);
        if (nextProbableDateOfOvulation != null) values.put(OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION, nextProbableDateOfOvulation);
        if (nextProbableDateOfPeriod != null) values.put(OvumContract.PatientEntry.COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD, nextProbableDateOfPeriod);

        db.update(OvumContract.PatientEntry.TABLE_NAME, values, OvumContract.PatientEntry._ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }


    // Method to update the symptoms table
    public void updateSymptoms(int patientId, String category, String name, String description, String dateRecorded, String dateOccurred, int severity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL_INSERT_SYMPTOMS = "INSERT INTO " + OvumContract.SymptomEntry.TABLE_NAME + " (" +
                OvumContract.SymptomEntry.COLUMN_PATIENT_ID + ", " +
                OvumContract.SymptomEntry.COLUMN_CATEGORY + ", " +
                OvumContract.SymptomEntry.COLUMN_NAME + ", " +
                OvumContract.SymptomEntry.COLUMN_DESCRIPTION + ", " +
                OvumContract.SymptomEntry.COLUMN_DATE_RECORDED + ", " +
                OvumContract.SymptomEntry.COLUMN_DATE_OCCURRED + ", " +
                OvumContract.SymptomEntry.COLUMN_SEVERITY + ") VALUES (" +
                patientId + ", '" + category + "', '" + name + "', '" + description + "', '" + dateRecorded + "', '" + dateOccurred + "', " + severity + ")";
        db.execSQL(SQL_INSERT_SYMPTOMS);
    }

    // method to add events into the database
    public void addEvent(int patientId, String name, String description, String dateRecorded, String dateOccurred) {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL_INSERT_EVENTS = "INSERT INTO " + OvumContract.EventEntry.TABLE_NAME + " (" +
                OvumContract.EventEntry.COLUMN_PATIENT_ID + ", " +
                OvumContract.EventEntry.COLUMN_NAME + ", " +
                OvumContract.EventEntry.COLUMN_DESCRIPTION + ", " +
                OvumContract.EventEntry.COLUMN_DATE_RECORDED + ", " +
                OvumContract.EventEntry.COLUMN_DATE_OCCURRED + ") VALUES (" +
                patientId + ", '" + name + "', '" + description + "', '" + dateRecorded + "', '" + dateOccurred + "')";
        db.execSQL(SQL_INSERT_EVENTS);
    }
    // meth to get and event from the database
    public Cursor getEvent(int patientId, String name, String dateOccurred) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                OvumContract.EventEntry._ID,
                OvumContract.EventEntry.COLUMN_PATIENT_ID,
                OvumContract.EventEntry.COLUMN_NAME,
                OvumContract.EventEntry.COLUMN_DESCRIPTION,
                OvumContract.EventEntry.COLUMN_DATE_RECORDED,
                OvumContract.EventEntry.COLUMN_DATE_OCCURRED
        };
        String selection = OvumContract.EventEntry.COLUMN_PATIENT_ID + " = ? AND " + OvumContract.EventEntry.COLUMN_NAME + " = ? AND " + OvumContract.EventEntry.COLUMN_DATE_OCCURRED + " = ?";
        String[] selectionArgs = {String.valueOf(patientId), name, dateOccurred};
        Cursor cursor = db.query(
                OvumContract.EventEntry.TABLE_NAME,
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
