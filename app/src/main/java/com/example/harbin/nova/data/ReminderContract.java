package com.example.harbin.nova.data;

import android.provider.BaseColumns;

/**
 * Created by Harbin on 5/10/17.
 */

public class ReminderContract {
    // BaseColumns here is for ID
    public static final class ReminderlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminders_table";
        public static final String COLUMN_MEDICINE = "medicine";
        public static final String COLUMN_DOSAGE = "dosage";
        public static final String COLUMN_NO_OF_DOSAGE = "NoOfDosage";
        public static final String COLUMN_DAYS = "days";
        public static final String COLUMN_PROGRESS = "progress";
        public static final String COLUMN_INFO = "info";
        public static final String COLUMN_REMIND = "remind";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
