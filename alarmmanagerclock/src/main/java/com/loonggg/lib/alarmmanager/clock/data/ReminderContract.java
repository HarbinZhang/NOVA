package com.loonggg.lib.alarmmanager.clock.data;

import android.provider.BaseColumns;

/**
 * Created by Harbin on 5/10/17.
 */

public class ReminderContract {
    // BaseColumns here is for ID
    public static final class ReminderlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminders_table";
        public static final String COLUMN_MEDICINE = "medicine";
        public static final String COLUMN_STRENGTH = "strength";
        public static final String COLUMN_INFO = "info";
        public static final String COLUMN_REMINDDAY = "remindDay";
        public static final String COLUMN_REMINDTIME= "remindTime";
        public static final String COLUMN_ALARMID = "alarmID";
    }

}
