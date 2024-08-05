package com.example.ovum;

import android.provider.BaseColumns;

public class OvumContract {

    private OvumContract() {}

    /* Inner class that defines the table contents of the Patients table */
    public static class PatientEntry implements BaseColumns {
        public static final String TABLE_NAME = "patients";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_PROFILE_PICTURE = "profile_picture";
        public static final String COLUMN_DOB = "DOB";
        public static final String COLUMN_CYCLE_LENGTH = "cycle_length";
        public static final String COLUMN_PERIOD_LENGTH = "period_length";
        public static final String COLUMN_LAST_PERIOD_DATE = "last_period_date";
        public static final String COLUMN_LAST_PERIOD_END_DATE = "last_period_end_date";
        public static final String COLUMN_AVERAGE_CYCLE_LENGTH = "average_cycle_length";
        public static final String COLUMN_AVERAGE_PERIOD_LENGTH = "average_period_length";
        public static final String COLUMN_NEXT_PROBABLE_DATE_OF_OVULATION = "next_probable_date_of_ovulation";
        public static final String COLUMN_NEXT_PROBABLE_DATE_OF_PERIOD = "next_probable_date_of_period";
    }

    /* Inner class that defines the table contents of the Symptoms table */
    public static class SymptomEntry implements BaseColumns {
        public static final String TABLE_NAME = "symptoms";
        public static final String COLUMN_PATIENT_ID = "patient_id";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE_RECORDED = "date_recorded";
        public static final String COLUMN_DATE_OCCURRED = "date_occurred";
        public static final String COLUMN_SEVERITY = "severity";
    }
    /*  Another inner class that defines the table contents of the Events table */
    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_PATIENT_ID = "patient_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE_RECORDED = "date_recorded";
        public static final String COLUMN_DATE_OCCURRED = "date_occurred";
    }
}
