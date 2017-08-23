package com.example.harbin.nova.login;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Harbin on 6/12/17.
 */


@IgnoreExtraProperties
public class Doctor {

    public String firstname, lastname;
    public String email;

    public Doctor(){

    }

    public Doctor(String firstname, String lastname, String email){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

}
