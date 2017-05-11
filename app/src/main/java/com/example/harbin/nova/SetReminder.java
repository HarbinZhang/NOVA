package com.example.harbin.nova;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.harbin.nova.data.ReminderContract;
import com.example.harbin.nova.data.ReminderDbHelper;


public class SetReminder extends AppCompatActivity {

    private ReminderListAdapter reminderAdapter;

    private SQLiteDatabase mDb;

    private EditText mNewMedicineEditText, mNewDosageEditText,
            mNewNOofDosageEditText, mNewinfoEditText, mNewProgressEditText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        RecyclerView reminderRecyclerView;

        reminderRecyclerView = (RecyclerView) this.findViewById(R.id.reminder_all_reminders_list_view);

        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ReminderDbHelper dbHelper = new ReminderDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllReminders();

        reminderAdapter = new ReminderListAdapter(this, cursor);

        reminderRecyclerView.setAdapter(reminderAdapter);


        // Edit
        mNewMedicineEditText = (EditText) this.findViewById(R.id.reminder_medicine_edit_text);
        mNewDosageEditText = (EditText) this.findViewById(R.id.reminder_dosage_edit_text);
        mNewNOofDosageEditText = (EditText) this.findViewById(R.id.reminder_NOofDosage_edit_text);
        mNewinfoEditText = (EditText) this.findViewById(R.id.reminder_info_edit_text);
        mNewProgressEditText = (EditText) this.findViewById(R.id.reminder_progress_edit_text);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeReminder(id);
                reminderAdapter.swapCursor(getAllReminders());
            }
        }).attachToRecyclerView(reminderRecyclerView);


    }


    private Cursor getAllReminders(){
        return mDb.query(
                ReminderContract.ReminderlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ReminderContract.ReminderlistEntry.COLUMN_TIMESTAMP
        );
    }


    private long addNewReminder(String medicine, int dosage,
                                int NOofDosage, int progress, String info){
        ContentValues cv = new ContentValues();
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE, medicine);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_DOSAGE, dosage);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE, NOofDosage);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_INFO, info);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_PROGRESS, progress);
        return mDb.insert(ReminderContract.ReminderlistEntry.TABLE_NAME, null, cv);
    }


    public void addToReminder(View view){
        if(mNewMedicineEditText.getText().length()==0 ||
                mNewDosageEditText.getText().length()==0){
            return;
        }

        int dosage = 0;
        try{
            dosage = Integer.parseInt(mNewDosageEditText.getText().toString());
        }catch (NumberFormatException e){
            Log.e(LOG_TAG, "Failed to parse dosage into number: " + e.getMessage());
        }

        int NOofDosage = 0;
        try{
            NOofDosage = Integer.parseInt(mNewNOofDosageEditText.getText().toString());
        }catch (NumberFormatException e){
            Log.e(LOG_TAG, "Failed to parse NO. of Dosage into number: " + e.getMessage());
        }

        int progress = 0;
        try{
            progress = Integer.parseInt(mNewProgressEditText.getText().toString());
        }catch (NumberFormatException e){
            Log.e(LOG_TAG, "Failed to parse progress into number: " + e.getMessage());
        }


        addNewReminder(mNewMedicineEditText.getText().toString(), dosage, NOofDosage,
                progress, mNewinfoEditText.getText().toString());

        reminderAdapter.swapCursor(getAllReminders());

        mNewDosageEditText.clearFocus();
        mNewMedicineEditText.clearFocus();
        mNewMedicineEditText.getText().clear();
        mNewDosageEditText.getText().clear();
        mNewNOofDosageEditText.getText().clear();
        mNewinfoEditText.getText().clear();
        mNewProgressEditText.getText().clear();
    }




    private boolean removeReminder(long id){
        return mDb.delete(ReminderContract.ReminderlistEntry.TABLE_NAME,
                ReminderContract.ReminderlistEntry._ID + "="+id,null) > 0;
    }

}
