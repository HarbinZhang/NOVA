package com.loonggg.alarmmanager.clock;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loonggg.alarmmanager.clock.data.Reminder;
import com.loonggg.alarmmanager.clock.data.ReminderContract;
import com.loonggg.alarmmanager.clock.data.ReminderDbHelper;
import com.loonggg.alarmmanager.clock.view.SelectRemindCyclePopup;
import com.loonggg.alarmmanager.clock.view.SelectRemindPeriodPopup;
import com.loonggg.alarmmanager.clock.view.SelectRemindTimePopup;
import com.loonggg.alarmmanager.clock.view.SelectRemindWayPopup;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class AlarmMainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView date_tv;
    private TimePickerView pvTime;
    private RelativeLayout repeat_rl, ring_rl, period_rl, time_rl;
    private TextView tv_repeat_value, tv_ring_value;
    private LinearLayout allLayout;
    private Button set_btn;
    private String time;
    private int cycle;
    private int ring;
    private int remindTime;
    private List<String> remindTimeList = new ArrayList<>();
    private int alarmID;

    private int period_value;
    private TextView tv_period_value;
    private TextView tv_time_value;

    private TextView debug;
    private SQLiteDatabase mDb;
    private EditText mNewMedicineEditText, mNewDosageEditText,
            mNewNOofDosageEditText, mNewinfoEditText, mNewProgressEditText;
    private final static String LOG_TAG = AlarmMainActivity.class.getSimpleName();
    private String mDays_string;


    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;

    private EditText et_medincineName, et_strength, et_duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity_main);


        debug = (TextView) findViewById(R.id.tv_alarm_debug);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        debug.setText(mUserId);



        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // Edit
        et_medincineName = (EditText) findViewById(R.id.et_reminder_medicine);
        et_strength = (EditText) findViewById(R.id.et_reminder_strength);
        et_duration = (EditText) findViewById(R.id.et_reminder_duration);



//
//        mNewMedicineEditText = (EditText) this.findViewById(R.id.reminder_medicine_edit_text);
//        mNewDosageEditText = (EditText) this.findViewById(R.id.reminder_dosage_edit_text);
//        mNewNOofDosageEditText = (EditText) this.findViewById(R.id.reminder_NOofDosage_edit_text);
//        mNewinfoEditText = (EditText) this.findViewById(R.id.reminder_info_edit_text);
//        mNewProgressEditText = (EditText) this.findViewById(R.id.reminder_progress_edit_text);
//

        allLayout = (LinearLayout) findViewById(R.id.alarm_all_layout);
        set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
//        date_tv = (TextView) findViewById(R.id.date_tv);
//        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
//        repeat_rl.setOnClickListener(this);
        period_rl = (RelativeLayout) findViewById(R.id.period_rl);
        period_rl.setOnClickListener(this);
        ring_rl = (RelativeLayout) findViewById(R.id.ring_rl);
        ring_rl.setOnClickListener(this);
        time_rl = (RelativeLayout) findViewById(R.id.rl_time);
        time_rl.setOnClickListener(this);

        tv_period_value = (TextView) findViewById(R.id.tv_period_value);
//        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        tv_ring_value = (TextView) findViewById(R.id.tv_ring_value);
        tv_time_value = (TextView) findViewById(R.id.tv_time_value);


        final Query remindTimeListQuery = mDatabase.child("users").child(mUserId).child("remindTime");
        remindTimeListQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                remindTimeList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String time = ds.getValue(String.class);
                    remindTimeList.add(time);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Query alarmIDQuery = mDatabase.child("users").child(mUserId).child("curtID");
        alarmIDQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alarmID = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        pvTime = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
////        pvTime = new TimePickerView(this, new TimePickerView.OnTimeSelectListener(){
////
////        })
////
////        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
////            @Override
////            public void onTimeSelect(Date date,View v) {
////                date_tv.setText(getTime(date));
////            }
////        })
////                .build();
////        pvTime.setDate(Calendar.getInstance());
//
//        pvTime.setTime(new Date());
//        pvTime.setCyclic(false);
//        pvTime.setCancelable(true);
//
//        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//
//            @Override
//            public void onTimeSelect(Date date) {
//                time = getTime(date);
//                date_tv.setText(time);
//            }
//        });
//
//        date_tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pvTime.show();
//            }
//        });
//



    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.ring_rl){
            selectRingWay();
        }else if(id == R.id.period_rl){
            selectPeriod();
        }
        else if(id == R.id.rl_time){
            selectRemindTime();
        }
        else if(id == R.id.set_btn){
            setClock();
        }

    }




    private void setClock() {

        // sql
        if(et_medincineName.getText().length()==0 ||
                et_medincineName.getText().length()==0){
            return;
        }

        if(et_strength.getText().length()==0 ||
                et_strength.getText().length()==0){
            return;
        }


//        int period = 0;
//        try{
//            period = parseInt(et_period.getText().toString());
//        }catch (NumberFormatException e){
//            Log.e(LOG_TAG, "Failed to parse dosage into number: " + e.getMessage());
//        }


        int duration = 0;
        try{
            duration = parseInt(et_duration.getText().toString());
        }catch (NumberFormatException e){
            Log.e(LOG_TAG, "Failed to parse duration into number: " + e.getMessage());
        }

        final String medicineName = et_medincineName.getText().toString();
        final String strength = et_strength.getText().toString();





//        if(mNewMedicineEditText.getText().length()==0 ||
//                mNewDosageEditText.getText().length()==0){
//            return;
//        }
//
//        int dosage = 0;
//        try{
//            dosage = parseInt(mNewDosageEditText.getText().toString());
//        }catch (NumberFormatException e){
//            Log.e(LOG_TAG, "Failed to parse dosage into number: " + e.getMessage());
//        }
//
//        int NOofDosage = 0;
//        try{
//            NOofDosage = parseInt(mNewNOofDosageEditText.getText().toString());
//        }catch (NumberFormatException e){
//            Log.e(LOG_TAG, "Failed to parse NO. of Dosage into number: " + e.getMessage());
//        }
//
//        int progress = 0;
//        try{
//            progress = parseInt(mNewProgressEditText.getText().toString());
//        }catch (NumberFormatException e){
//            Log.e(LOG_TAG, "Failed to parse progress into number: " + e.getMessage());
//        }



//        ContentValues cv = new ContentValues();
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE, medicineName);
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_DOSAGE, dosage);
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE, NOofDosage);
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_INFO, info);
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_PROGRESS, progress);
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND, time);
//        cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMIND, 1);

//        mNewDosageEditText.clearFocus();
//        mNewMedicineEditText.clearFocus();
//        mNewMedicineEditText.getText().clear();
//        mNewDosageEditText.getText().clear();
//        mNewNOofDosageEditText.getText().clear();
//        mNewinfoEditText.getText().clear();
//        mNewProgressEditText.getText().clear();

//        // cycle in cv
//        if (time != null && time.length() > 0) {
//            String[] times = time.split(":");
//            if (cycle == 0) {
//                // need to consider cv
//                for (int i = 1; i <= 7; i++){
//                    cv.put("weekday_"+i, "1");
//                }
//            } if(cycle == -1){
//
//            }else {
//                String weeksStr = parseRepeat(cycle, 1);
//                String[] weeks = weeksStr.split(",");
//                for (int i = 0; i < weeks.length; i++) {
//                    cv.put("weekday_"+weeks[i], "1");
//                }
//            }
//        }


//        mDb.insert(ReminderContract.ReminderlistEntry.TABLE_NAME, null, cv);






//        Cursor cursor = getNewestCursor();
//
//
//        if(cursor == null || !cursor.moveToFirst()){
//            Toast.makeText(this, "Cursor Error", Toast.LENGTH_LONG).show();
//        }
//
//
//        long idL =  cursor.getLong(cursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));
//
//        int id = safeLongToInt(idL);
//
//        int res = cursor.getInt(cursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_THURSDAY_REMIND));
//

        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i<6; i++){
            idList.add(alarmID + i);
        }
        int curtID = alarmID;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(calendar.getTime());

        Reminder reminder = new Reminder(medicineName, strength, period_value, parseTime(remindTime),
                duration, date, curtID);
        mDatabase.child("users").child(mUserId).child("curtID").setValue(curtID+10);
        mDatabase.child("users").child(mUserId).child("reminders").child(medicineName).setValue(reminder);
        if (period_value != 0 && duration != 0 && remindTime != 0){
            String [] remindTimes = parseTime(remindTime).split(",");
            for (String it: remindTimes){
                int index = Integer.valueOf(it);
                String[] times = remindTimeList.get(index-1).split(":");
                AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]), Integer.parseInt(times[1]),
                        idList.get(index-1), 0, medicineName, ring, period_value);
            }
            Toast.makeText(this, "Reminder Added Successfully", Toast.LENGTH_LONG).show();
        }

//        // set schedule
//        if (time != null && time.length() > 0) {
//
//            String[] times = time.split(":");
//            if (cycle == 0) {
//                // need to consider cv
//                AlarmManagerUtil.setAlarm(this, 0, parseInt(times[0]), parseInt
//                        (times[1]), id, 0, "Reminder Alarm", ring, period_value);
//            } if(cycle == -1){
//                AlarmManagerUtil.setAlarm(this, 1, parseInt(times[0]), parseInt
//                        (times[1]), id, 0, "Reminder Alarm", ring, period_value);
//            }else {
//                String remindTime_str = parseTime(remindTime);
//                String[] remindTimes = remindTime_str.split(",");
//                for (int i = 0; i < remindTimes.length; i++) {
////                    AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]), Integer
////                            .parseInt(times[1]), id, parseInt(remindTimes[i]), medicineName, ring, period_value);
//                }
//            }
//            Toast.makeText(this, "Reminder Added Successfully", Toast.LENGTH_LONG).show();
//        }

//        if (time != null && time.length() > 0) {
//
//            String[] times = time.split(":");
//            if (cycle == 0) {
//                // need to consider cv
//                AlarmManagerUtil.setAlarm(this, 0, Integer.parseInt(times[0]), Integer.parseInt
//                        (times[1]), id, 0, "Reminder Alarm", ring, period_value);
//            } if(cycle == -1){//是只响一次的闹钟
//                AlarmManagerUtil.setAlarm(this, 1, Integer.parseInt(times[0]), Integer.parseInt
//                        (times[1]), id, 0, "Reminder Alarm", ring, period_value);
//            }else {//多选，周几的闹钟
//                String weeksStr = parseRepeat(cycle, 1);
//                String[] weeks = weeksStr.split(",");
//                for (int i = 0; i < weeks.length; i++) {
//                    AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]), Integer
//                            .parseInt(times[1]), id, Integer.parseInt(weeks[i]), medicineName, ring, period_value);
//                }
//            }
//            Toast.makeText(this, "Reminder Added Successfully", Toast.LENGTH_LONG).show();
//        }
    }

    private Cursor getNewestCursor(){
//        String whereClause = ReminderContract.ReminderlistEntry.COLUMN_MEDICINE + " = '"
//                + name + "' ";
//        String whereClause = ReminderContract.ReminderlistEntry.COLUMN_MEDICINE + " = 'qwe' ";
//        String whereClause = ReminderContract.ReminderlistEntry._ID + " = 1 ";
        Cursor cursor = mDb.query(
                ReminderContract.ReminderlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ReminderContract.ReminderlistEntry._ID + " DESC",
                "1"
        );

//        return cursor.getLong(cursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));
        return cursor;
    }


    public void selectRemindCycle() {
        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
                switch (flag) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:
                        int repeat = Integer.valueOf(ret);
                        tv_repeat_value.setText(parseRepeat(repeat, 0));
                        cycle = repeat;
                        fp.dismiss();
                        break;
                    case 8:
                        tv_repeat_value.setText("Everyday");
                        cycle = 0;
                        fp.dismiss();
                        break;
                    case 9:
                        tv_repeat_value.setText("Just once");
                        cycle = -1;
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public void selectRemindTime() {
        final SelectRemindTimePopup fp = new SelectRemindTimePopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindTimePopupListener(new SelectRemindTimePopup
                .SelectRemindTimePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
                switch (flag) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:
                        int time = Integer.valueOf(ret);
                        tv_time_value.setText(parseTime(time));
                        remindTime = time;
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public void selectPeriod(){
        final SelectRemindPeriodPopup fp = new SelectRemindPeriodPopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindPeriodPopupListener(new SelectRemindPeriodPopup.SelectRemindPeriodPopupOnClickListener() {
            @Override
            public void obtainMessage(int flag) {
                switch (flag){
                    case 1:
                        tv_period_value.setText("Everyday");
                        period_value = 1;
                        break;
                    case 2:
                        tv_period_value.setText("Every 2 days");
                        period_value = 2;
                        break;
                    case 3:
                        tv_period_value.setText("Every 3 days");
                        period_value = 3;
                        break;
                    case 4:
                        tv_period_value.setText("Every 4 days");
                        period_value = 4;
                        break;
                    case 5:
                        tv_period_value.setText("Every 5 days");
                        period_value = 5;
                        break;
                    case 6:
                        tv_period_value.setText("Every 6 days");
                        period_value = 6;
                        break;
                    case 7:
                        tv_period_value.setText("Every week");
                        period_value = 7;
                        break;
                }
            }
        });
    }

    public void selectRingWay() {
        SelectRemindWayPopup fp = new SelectRemindWayPopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindWayPopupListener(new SelectRemindWayPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    case 0:
                        tv_ring_value.setText("Vibrate");
                        ring = 0;
                        break;
                    case 1:
                        tv_ring_value.setText("Ring");
                        ring = 1;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * @param repeat
     * @param flag
     */
    public static String parseRepeat(int repeat, int flag) {
        String cycle = "";
        String weeks = "";
        if (repeat == 0) {
            repeat = 127;
        }
        if (repeat % 2 == 1) {
            cycle = "Monday";
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                cycle = "Tuesday";
                weeks = "2";
            } else {
                cycle = cycle + "," + "Tuesday";
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                cycle = "Wednesday";
                weeks = "3";
            } else {
                cycle = cycle + "," + "Wednesday";
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                cycle = "Thursday";
                weeks = "4";
            } else {
                cycle = cycle + "," + "Thursday";
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                cycle = "Friday";
                weeks = "5";
            } else {
                cycle = cycle + "," + "Friday";
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                cycle = "Saturday";
                weeks = "6";
            } else {
                cycle = cycle + "," + "Saturday";
                weeks = weeks + "," + "6";
            }
        }
        if (repeat / 64 == 1) {
            if ("".equals(cycle)) {
                cycle = "Sunday";
                weeks = "7";
            } else {
                cycle = cycle + "," + "Sunday";
                weeks = weeks + "," + "7";
            }
        }

        return flag == 0 ? cycle : weeks;
    }



    public static String parseTime(int repeat) {
        String cycle = "";
        String weeks = "";

        if (repeat % 2 == 1) {
            cycle = "Monday";
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                cycle = "Tuesday";
                weeks = "2";
            } else {
                cycle = cycle + "," + "Tuesday";
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                cycle = "Wednesday";
                weeks = "3";
            } else {
                cycle = cycle + "," + "Wednesday";
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                cycle = "Thursday";
                weeks = "4";
            } else {
                cycle = cycle + "," + "Thursday";
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                cycle = "Friday";
                weeks = "5";
            } else {
                cycle = cycle + "," + "Friday";
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                cycle = "Saturday";
                weeks = "6";
            } else {
                cycle = cycle + "," + "Saturday";
                weeks = weeks + "," + "6";
            }
        }


        return weeks;
    }



    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
