package com.example.harbin.nova;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.loonggg.alarmmanager.clock.AlarmMainActivity;
import com.loonggg.alarmmanager.clock.data.Reminder;
import com.loonggg.alarmmanager.clock.data.ReminderContract;
import com.loonggg.alarmmanager.clock.data.ReminderDbHelper;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;

import java.text.SimpleDateFormat;
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

    private Context mContext = this;


    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;

    String today_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        RecyclerView reminderRecyclerView;

        reminderRecyclerView = (RecyclerView) this.findViewById(R.id.reminder_all_reminders_list_view);

        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));









        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            today_date =  dateFormat.format(cal.getTime());
        }catch (Exception e){

        }

        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = getAllReminders(today_date);
        reminderAdapter = new ReminderListAdapter(this, cursor);
        reminderRecyclerView.setAdapter(reminderAdapter);




        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Query remindersQuery = mDatabase.child("users").child(mUserId).child("reminders");
        remindersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Reminder reminder = dataSnapshot.getValue(Reminder.class);

                if(isExist(reminder)){
                    return ;
                }

                try {
                    String newDate = reminder.startTime;
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    cal.setTime(dateFormat.parse(newDate));

                    int i = 0;
                    while (i * reminder.period <= reminder.duration * 7){

                        for(String time : reminder.time.split(",")){

                            ContentValues cv = new ContentValues();
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE, reminder.medicine);
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_STRENGTH, reminder.strength);
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMINDTIME, Integer.valueOf(time));
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMINDDAY, newDate);
                            mDb.insert(ReminderContract.ReminderlistEntry.TABLE_NAME, null, cv);

                        }

                        cal.add(Calendar.DATE, reminder.period);
                        i++;
                        newDate = dateFormat.format(cal.getTime());

                    }

                }catch (Exception e){

                }

                reminderAdapter.swapCursor(getAllReminders(
                        today_date
                ));


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Reminder reminder = dataSnapshot.getValue(Reminder.class);


                if(!isExist(reminder)){
                    return ;
                }


                String medicine = reminder.medicine;

                mDb.execSQL("delete from " + ReminderContract.ReminderlistEntry.TABLE_NAME +
                        " where " + ReminderContract.ReminderlistEntry.COLUMN_MEDICINE + " = '" + medicine + "'");


                if(isExist(reminder)){
                    return ;
                }

                try {
                    String newDate = reminder.startTime;
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    cal.setTime(dateFormat.parse(newDate));

                    int i = 0;
                    while (i * reminder.period <= reminder.duration * 7){

                        for(String time : reminder.time.split(",")){

                            ContentValues cv = new ContentValues();
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE, reminder.medicine);
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_STRENGTH, reminder.strength);
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMINDTIME, Integer.valueOf(time));
                            cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMINDDAY, newDate);
                            mDb.insert(ReminderContract.ReminderlistEntry.TABLE_NAME, null, cv);

                        }

                        cal.add(Calendar.DATE, reminder.period);
                        i++;
                        newDate = dateFormat.format(cal.getTime());

                    }

                }catch (Exception e){

                }

                reminderAdapter.swapCursor(getAllReminders(
                        today_date
                ));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Reminder reminder = dataSnapshot.getValue(Reminder.class);

                String medicine = reminder.medicine;

                mDb.execSQL("delete from " + ReminderContract.ReminderlistEntry.TABLE_NAME +
                        " where " + ReminderContract.ReminderlistEntry.COLUMN_MEDICINE + " = '" + medicine + "'");


                reminderAdapter.swapCursor(getAllReminders(
                        today_date
                ));

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeReminder(id);
                AlarmManagerUtil.cancelAlarm(mContext, AlarmManagerUtil.ALARM_ACTION, safeLongToInt(id));
                reminderAdapter.swapCursor(getAllReminders(
                        today_date
                ));
            }
        }).attachToRecyclerView(reminderRecyclerView);

        boolean result=isMyServiceRunning(MyService.class);



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();



        reminderAdapter.swapCursor(getAllReminders(
                today_date
        ));
    }

    private boolean isExist(Reminder reminder){

        String whereClause = ReminderContract.ReminderlistEntry.COLUMN_MEDICINE + " = '" + reminder.medicine +"' ";
        Cursor cursor = mDb.query(
                ReminderContract.ReminderlistEntry.TABLE_NAME,
                null,
                whereClause,
                null,
                null,
                null,
                null
        );

        if(cursor.getCount() == 0){
            return false;
        }else{
            return true;
        }

    }

    // read from db
    private Cursor getAllReminders(String date){


        String whereClause = ReminderContract.ReminderlistEntry.COLUMN_REMINDDAY + " = '" + date + "' ";
//        String whereClause = ReminderContract.ReminderlistEntry.COLUMN_REMINDDAY + " = 2017-06-18 ";
//        String whereClause = ReminderContract.ReminderlistEntry.COLUMN_REMINDDAY + " = '2017-06-18' ";

        return mDb.query(
                ReminderContract.ReminderlistEntry.TABLE_NAME,
                null,
                whereClause,
//                null,
                null,
                null,
                null,
                ReminderContract.ReminderlistEntry.COLUMN_REMINDTIME
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


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
