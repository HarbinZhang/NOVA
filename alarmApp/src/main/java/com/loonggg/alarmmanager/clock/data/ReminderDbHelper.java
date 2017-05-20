package com.loonggg.alarmmanager.clock.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Harbin on 5/10/17.
 */

public class ReminderDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Reminderlist.db";

    private static final int DATABASE_VERSION = 1;

    public ReminderDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " +
                ReminderContract.ReminderlistEntry.TABLE_NAME + " (" +
                ReminderContract.ReminderlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReminderContract.ReminderlistEntry.COLUMN_MEDICINE + " TEXT NOT NULL unique, " +
                ReminderContract.ReminderlistEntry.COLUMN_DOSAGE + " INTEGER, " +
                ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE + " INTEGER, " +
                ReminderContract.ReminderlistEntry.COLUMN_PROGRESS + " TEXT , " +
                ReminderContract.ReminderlistEntry.COLUMN_INFO + " TEXT, " +
                ReminderContract.ReminderlistEntry.COLUMN_REMIND + "  INTEGER DEFAULT 1, " +
                ReminderContract.ReminderlistEntry.COLUMN_MONDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_TUESDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_WEDNESDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_THURSDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_FRIDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_SATURDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_SUNDAY_REMIND + " INTEGER DEFAULT 0, " +
                ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND + " TIME NOT NULL , " +
                ReminderContract.ReminderlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                " );";


        db.execSQL(SQL_CREATE_REMINDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReminderContract.ReminderlistEntry.TABLE_NAME);
        onCreate(db);
    }
}