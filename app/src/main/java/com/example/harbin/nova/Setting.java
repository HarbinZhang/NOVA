package com.example.harbin.nova;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loonggg.lib.alarmmanager.clock.data.ReminderContract;
import com.loonggg.lib.alarmmanager.clock.data.ReminderDbHelper;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Setting extends AppCompatActivity implements View.OnClickListener {


    private FirebaseUser mFirebaseUser;
    private static DatabaseReference mDatabase;
    private static String mUserId;
    private FirebaseAuth mFirebaseAuth;

    private static SharedPreferences pres;
    private static SharedPreferences.Editor editor;

    private TextView tv_bbfT, tv_abfT, tv_blT, tv_alT, tv_bdT, tv_adT;

    private Button btn_bbfT, btn_abfT, btn_blT, btn_alT, btn_bdT, btn_adT;

    final static private String[] remindTimeName = {"beforeBreakfastTime", "afterBreakfastTime", "beforeLunchTime",
                            "afterLunchTime", "beforeDinnerTime", "afterDinnerTime"};

    private Map<Integer, TextView> tvs_map = new HashMap<>();

    static private int curtTv;

    private ArrayList<TextView> tvs_array = new ArrayList<TextView>();

    static private Context mContext;

    private static SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;

//        AlarmManagerUtil.setAlarm(mContext, 0, 12, 38, 247, 0, "haha ceshi", 1, 0);

        pres = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pres.edit();


        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        mDb = dbHelper.getReadableDatabase();


        tv_bbfT = (TextView) findViewById(R.id.tv_setting_before_breakfast);
        btn_bbfT = (Button) findViewById(R.id.btn_setting_before_breakfast);
        btn_bbfT.setOnClickListener(this);

        tv_abfT = (TextView) findViewById(R.id.tv_setting_after_breakfast);
        btn_abfT = (Button) findViewById(R.id.btn_setting_after_breakfast);
        btn_abfT.setOnClickListener(this);

        tv_blT = (TextView) findViewById(R.id.tv_setting_before_lunch);
        btn_blT = (Button) findViewById(R.id.btn_setting_before_lunch);
        btn_blT.setOnClickListener(this);

        tv_alT = (TextView) findViewById(R.id.tv_setting_after_lunch);
        btn_alT = (Button) findViewById(R.id.btn_setting_after_lunch);
        btn_alT.setOnClickListener(this);

        tv_bdT = (TextView) findViewById(R.id.tv_setting_before_dinner);
        btn_bdT = (Button) findViewById(R.id.btn_setting_before_dinner);
        btn_bdT.setOnClickListener(this);

        tv_adT = (TextView) findViewById(R.id.tv_setting_after_dinner);
        btn_adT = (Button) findViewById(R.id.btn_setting_after_dinner);
        btn_adT.setOnClickListener(this);


        tvs_map.put(0, tv_bbfT);
        tvs_map.put(1, tv_abfT);
        tvs_map.put(2, tv_blT);
        tvs_map.put(3, tv_alT);
        tvs_map.put(4, tv_bdT);
        tvs_map.put(5, tv_adT);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Query remindTimeQuery = mDatabase.child("users").child(mUserId).child("remindTime");
        remindTimeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String time = ds.getValue(String.class);
                    String originTime = pres.getString(remindTimeName[i], null);
                    if (!time.equals(originTime)){
                        editor.putString(remindTimeName[i], time);
                        editor.apply();
                    }
//                    ((TextView)tvs_array[i]).setText("hi");
                    tvs_map.get(i).setText(time);
                    i++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_setting_before_breakfast:
                curtTv = 0;
                showTimePicker();
                break;
            case R.id.btn_setting_after_breakfast:
                curtTv = 1;
                showTimePicker();
                break;
            case R.id.btn_setting_before_lunch:
                curtTv = 2;
                showTimePicker();
                break;
            case R.id.btn_setting_after_lunch:
                curtTv = 3;
                showTimePicker();
                break;
            case R.id.btn_setting_before_dinner:
                curtTv = 4;
                showTimePicker();
                break;
            case R.id.btn_setting_after_dinner:
                curtTv = 5;
                showTimePicker();
                break;

        }
    }



    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String time;
            if(minute < 10){
                time = String.valueOf(hourOfDay)+":"+ "0" + String.valueOf(minute);
            }else {
                time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
            }
            editor.putString(remindTimeName[curtTv], time);
            editor.apply();
            mDatabase.child("users").child(mUserId).child("remindTime").child(String.valueOf(curtTv)).setValue(time);

            Cursor cursor = getIndexReminder(curtTv);
            Set<Integer> IDs = new HashSet<>();
            HashMap<Integer, String> id2name= new HashMap<>();
            HashMap<Integer, String> id2strength = new HashMap<>();
            if(cursor.moveToFirst()){
                do{
                    int id = cursor.getInt(cursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_ALARMID));
                    String name = cursor.getString(cursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE));
                    String strength = cursor.getString(cursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_STRENGTH));
                    IDs.add(id);
                    if(!id2name.containsKey(id)){
                        id2name.put(id, name);
                    }
                    if(!id2strength.containsKey(id)){
                        id2strength.put(id, strength);
                    }
                }while(cursor.moveToNext());
            }
            for(int id : IDs){
                AlarmManagerUtil.cancelAlarm(mContext, AlarmManagerUtil.ALARM_ACTION, id);
                AlarmManagerUtil.setAlarm(mContext, 1, hourOfDay, minute, id, 0,
                        "Medicine: " + id2name.get(id) + "\n" +
                        "Strength: " + id2strength.get(id),
                        1, 7);
            }


//            AlarmManagerUtil.cancelAlarm(mContext, AlarmManagerUtil.ALARM_ACTION, 247);
//            AlarmManagerUtil.setAlarm(mContext, 0, 12, 44, 247, 0, "haha cesh", 1, 0);

        }
    }


    public void showTimePicker(){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    private static Cursor getIndexReminder(int index){

        return mDb.query(ReminderContract.ReminderlistEntry.TABLE_NAME,
                null,
                ReminderContract.ReminderlistEntry.COLUMN_REMINDTIME + " = " + String.valueOf(index+1),
                null,
                null,
                null,
                null);
    }


}
