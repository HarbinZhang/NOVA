package com.loonggg.alarmmanager.clock.data;

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
        public static final String COLUMN_PROGRESS = "progress";
        public static final String COLUMN_INFO = "info";
        public static final String COLUMN_REMIND = "remind";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_MONDAY_REMIND = "weekday_1";
        public static final String COLUMN_TUESDAY_REMIND = "weekday_2";
        public static final String COLUMN_WEDNESDAY_REMIND = "weekday_3";
        public static final String COLUMN_THURSDAY_REMIND = "weekday_4";
        public static final String COLUMN_FRIDAY_REMIND = "weekday_5";
        public static final String COLUMN_SATURDAY_REMIND = "weekday_6";
        public static final String COLUMN_SUNDAY_REMIND = "weekday_7";
        public static final String COLUMN_TIME_REMIND = "time_remind";
    }

}
