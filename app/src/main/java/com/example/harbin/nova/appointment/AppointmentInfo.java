package com.example.harbin.nova.appointment;

/**
 * Created by Harbin on 6/15/17.
 */

public class AppointmentInfo {

    public int startTime;
    public String lastname;
    public String firstname;
    public String email;
    public int startYear;
    public int startMonth;
    public int startDay;

    public AppointmentInfo(){

    }

    public AppointmentInfo(int startTime, String firstname, String lastname, String email, int startYear, int startMonth, int startDay){
        this.startTime = startTime;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }
}
