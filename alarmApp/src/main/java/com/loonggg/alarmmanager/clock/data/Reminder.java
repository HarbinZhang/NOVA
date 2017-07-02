package com.loonggg.alarmmanager.clock.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harbin on 6/5/17.
 */



@IgnoreExtraProperties
public class Reminder {
    public String medicine;
    public String strength;
    public int period;
    public String time;
    public int duration;
    public String startTime;
    public List<Integer> alarmId = new ArrayList<>();

    public Reminder(){

    }

    public Reminder(String medicine, String strength, int period, String time, int duration, String date, int id){
        this.medicine = medicine;
        this.strength = strength;
        this.period = period;
        this.time = time;
        this.duration = duration;
        this.startTime = date;
        for (int i=0; i<6; i++){
            this.alarmId.add(id + i);
        }
    }



}
