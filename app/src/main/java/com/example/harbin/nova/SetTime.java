package com.example.harbin.nova;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.example.harbin.nova.Alarm.AlarmManagerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SetTime extends AppCompatActivity implements OnClickListener {

    private LinearLayout allLayout;
    private Button set_btn;
    private TextView date_tv;
    private RelativeLayout repeat_rl;
    private RelativeLayout ring_rl;
    private TextView repeat_value_tv;
    private TextView ring_value_tv;
    private TimePickerView pvTime;
    private String time;
    private int cycle;
    private int ring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        allLayout = (LinearLayout) findViewById(R.id.all_layout);
        set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
        date_tv = (TextView) findViewById(R.id.date_tv);
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        repeat_rl.setOnClickListener(this);
        ring_rl = (RelativeLayout) findViewById(R.id.ring_rl);
        repeat_value_tv = (TextView) findViewById(R.id.tv_repeat_value);
        ring_value_tv = (TextView) findViewById(R.id.tv_ring_value);
//        pvTime = new TimePickerView(this, TimePickerView.Type.);
        pvTime = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                time = getTime(date);
                date_tv.setText(time);
            }
        });

        date_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();

            }
        });

    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
            case R.id.ring_rl:
                selectRingWay();
                break;
            case R.id.set_btn:
                setClock();
                break;
            default:
                break;
        }
    }

    private void setClock(){
        if(time != null && time.length() > 0){
            String[] times = time.split(":");
            if(cycle == 0){
                AlarmManagerUtil.setAlarm(this, 0, Integer.parseInt(times[0]),
                        Integer.parseInt(times[1]), 0, 0, "Alarm! for every day", ring);
            }else if(cycle == -1){
                AlarmManagerUtil.setAlarm(this, 1, Integer.parseInt(times[0]),
                        Integer.parseInt(times[1]), 0, 0, "Alarm! for one time", ring);
            }else{
                String weeksStr = parseRepeat(cycle, 1);
                String[] weeks = weeksStr.split(",");
                for(int i=0; i<weeks.length; i++);{
                    AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]),
                            Integer.parseInt(times[1]), i, Integer.parseInt(weeks[i]), "闹钟响了", ring);
                }
            }
            Toast.makeText(this, "Timer Created", Toast.LENGTH_LONG).show();
        }
    }

    private void selectRemindCycle() {
        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(this);
        fp.showPopup(allLayout);
    }
}
