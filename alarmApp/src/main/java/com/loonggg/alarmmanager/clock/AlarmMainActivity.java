package com.loonggg.alarmmanager.clock;

import android.content.ContentValues;
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
import com.loonggg.alarmmanager.clock.data.ReminderContract;
import com.loonggg.alarmmanager.clock.data.ReminderDbHelper;
import com.loonggg.alarmmanager.clock.view.SelectRemindCyclePopup;
import com.loonggg.alarmmanager.clock.view.SelectRemindWayPopup;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmMainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView date_tv;
    private TimePickerView pvTime;
    private RelativeLayout repeat_rl, ring_rl;
    private TextView tv_repeat_value, tv_ring_value;
    private LinearLayout allLayout;
    private Button set_btn;
    private String time;
    private int cycle;
    private int ring;

    private TextView debug;
    private SQLiteDatabase mDb;
    private EditText mNewMedicineEditText, mNewDosageEditText,
            mNewNOofDosageEditText, mNewinfoEditText, mNewProgressEditText;
    private final static String LOG_TAG = AlarmMainActivity.class.getSimpleName();
    private String mDays_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity_main);


        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // Edit
        mNewMedicineEditText = (EditText) this.findViewById(R.id.reminder_medicine_edit_text);
        mNewDosageEditText = (EditText) this.findViewById(R.id.reminder_dosage_edit_text);
        mNewNOofDosageEditText = (EditText) this.findViewById(R.id.reminder_NOofDosage_edit_text);
        mNewinfoEditText = (EditText) this.findViewById(R.id.reminder_info_edit_text);
        mNewProgressEditText = (EditText) this.findViewById(R.id.reminder_progress_edit_text);


        allLayout = (LinearLayout) findViewById(R.id.alarm_all_layout);
        set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
        date_tv = (TextView) findViewById(R.id.date_tv);
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        repeat_rl.setOnClickListener(this);
        ring_rl = (RelativeLayout) findViewById(R.id.ring_rl);
        ring_rl.setOnClickListener(this);
        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        tv_ring_value = (TextView) findViewById(R.id.tv_ring_value);
        pvTime = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
//        pvTime = new TimePickerView(this, new TimePickerView.OnTimeSelectListener(){
//
//        })
//
//        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date,View v) {//选中事件回调
//                date_tv.setText(getTime(date));
//            }
//        })
//                .build();
//        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。

        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                time = getTime(date);
                date_tv.setText(time);
            }
        });

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show();
            }
        });




    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.repeat_rl){
            selectRemindCycle();
        }else if(id == R.id.ring_rl){
            selectRingWay();
        }else if(id == R.id.set_btn){
            setClock();
        }

    }




    private void setClock() {

        // sql
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

        final String medicineName = mNewMedicineEditText.getText().toString();
        final String info = mNewinfoEditText.getText().toString();


        ContentValues cv = new ContentValues();
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE, medicineName);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_DOSAGE, dosage);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE, NOofDosage);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_INFO, info);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_PROGRESS, progress);
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND, time);


        mNewDosageEditText.clearFocus();
        mNewMedicineEditText.clearFocus();
        mNewMedicineEditText.getText().clear();
        mNewDosageEditText.getText().clear();
        mNewNOofDosageEditText.getText().clear();
        mNewinfoEditText.getText().clear();
        mNewProgressEditText.getText().clear();

        // cycle in cv
        if (time != null && time.length() > 0) {
            String[] times = time.split(":");
            if (cycle == 0) {//是每天的闹钟
                // need to consider cv
                for (int i = 1; i <= 7; i++){
                    cv.put("weekday_"+i, "1");
                }
            } if(cycle == -1){//是只响一次的闹钟

            }else {//多选，周几的闹钟
                String weeksStr = parseRepeat(cycle, 1);
                String[] weeks = weeksStr.split(",");
                for (int i = 0; i < weeks.length; i++) {
                    cv.put("weekday_"+weeks[i], "1");
                }
            }
        }


        mDb.insert(ReminderContract.ReminderlistEntry.TABLE_NAME, null, cv);




        Cursor cursor = getNewestCursor();


        if(cursor == null || !cursor.moveToFirst()){
            Toast.makeText(this, "Cursor Error", Toast.LENGTH_LONG).show();
        }


        long idL =  cursor.getLong(cursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));

        int id = safeLongToInt(idL);

        int res = cursor.getInt(cursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_THURSDAY_REMIND));


        // set schedule
        if (time != null && time.length() > 0) {

            String[] times = time.split(":");
            if (cycle == 0) {//是每天的闹钟
                // need to consider cv
                AlarmManagerUtil.setAlarm(this, 0, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), id, 0, "Reminder Alarm", ring);
            } if(cycle == -1){//是只响一次的闹钟
                AlarmManagerUtil.setAlarm(this, 1, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), id, 0, "Reminder Alarm", ring);
            }else {//多选，周几的闹钟
                String weeksStr = parseRepeat(cycle, 1);
                String[] weeks = weeksStr.split(",");
                for (int i = 0; i < weeks.length; i++) {
                    AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]), Integer
                            .parseInt(times[1]), id, Integer.parseInt(weeks[i]), "Reminder Alarm", ring);
                }
            }
            Toast.makeText(this, "Reminder Added Successfully", Toast.LENGTH_LONG).show();
        }





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
                    // 星期一
                    case 0:

                        break;
                    // 星期二
                    case 1:

                        break;
                    // 星期三
                    case 2:

                        break;
                    // 星期四
                    case 3:

                        break;
                    // 星期五
                    case 4:

                        break;
                    // 星期六
                    case 5:

                        break;
                    // 星期日
                    case 6:

                        break;
                    // 确定
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


    public void selectRingWay() {
        SelectRemindWayPopup fp = new SelectRemindWayPopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindWayPopupListener(new SelectRemindWayPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    // 震动
                    case 0:
                        tv_ring_value.setText("Vibrate");
                        ring = 0;
                        break;
                    // 铃声
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
     * @param repeat 解析二进制闹钟周期
     * @param flag   flag=0返回带有汉字的周一，周二cycle等，flag=1,返回weeks(1,2,3)
     * @return
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


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
