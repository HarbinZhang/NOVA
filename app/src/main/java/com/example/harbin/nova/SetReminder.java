package com.example.harbin.nova;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.loonggg.alarmmanager.clock.AlarmMainActivity;
import com.loonggg.alarmmanager.clock.data.ReminderContract;
import com.loonggg.alarmmanager.clock.data.ReminderDbHelper;

import java.util.ArrayList;
import java.util.Calendar;


public class SetReminder extends AppCompatActivity {

    private ReminderListAdapter reminderAdapter;

    private SQLiteDatabase mDb;


    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private final String[] DAYS = {"Monday", "Tuesday","Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};


    private final ArrayList<String> itemSelected = new ArrayList();

    private String mDays_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        RecyclerView reminderRecyclerView;

        reminderRecyclerView = (RecyclerView) this.findViewById(R.id.reminder_all_reminders_list_view);

        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ReminderDbHelper dbHelper = new ReminderDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllReminders(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        reminderAdapter = new ReminderListAdapter(this, cursor);


        reminderRecyclerView.setAdapter(reminderAdapter);



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeReminder(id);
                reminderAdapter.swapCursor(getAllReminders(
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                ));
            }
        }).attachToRecyclerView(reminderRecyclerView);




    }

    @Override
    protected void onStart() {
        super.onStart();

        reminderAdapter.swapCursor(getAllReminders(
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        ));
    }

    // read from db
    private Cursor getAllReminders(int weekday){

        String day;

        if(weekday == Calendar.MONDAY){
            day = "1";
        }else if(weekday == Calendar.TUESDAY){
            day = "2";
        }else if(weekday == Calendar.WEDNESDAY){
            day = "3";
        }else if(weekday == Calendar.THURSDAY){
            day = "4";
        }else if(weekday == Calendar.FRIDAY){
            day = "5";
        }else if(weekday == Calendar.SATURDAY){
            day = "6";
        }else{
            day = "7";
        }

        String whereClause = "weekday_" + day + " = 1 ";

        return mDb.query(
                ReminderContract.ReminderlistEntry.TABLE_NAME,
                null,
                whereClause,
                null,
                null,
                null,
                ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND
        );
    }




    private boolean removeReminder(long id){
        return mDb.delete(ReminderContract.ReminderlistEntry.TABLE_NAME,
                ReminderContract.ReminderlistEntry._ID + "="+id,null) > 0;
    }

    public void select_days(View view) {

        Intent startAlarmSetting = new Intent(this, AlarmMainActivity.class);
        startActivity(startAlarmSetting);



//        Dialog dialog;
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Select days you want reminder:");
//
//        builder.setMultiChoiceItems(DAYS, null, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                if(isChecked){
//                    itemSelected.add(Integer.toString(which));
//                }else if(itemSelected.contains(Integer.toString(which))){
////                    itemSelected.remove(Integer.valueOf(which));
//                    itemSelected.remove(Integer.toString(which));
//                }
//            }
//        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                debug_window.setText("");
//                mDays_string = "";
//                for(String it:itemSelected){
//                    mDays_string += it;
//                }
//                debug_window.setText(mDays_string);
//                itemSelected.clear();
//            }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemSelected.clear();
//            }
//        });
//
//        dialog = builder.create();
//
//        dialog.show();



    }
}
