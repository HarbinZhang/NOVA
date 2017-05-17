package com.example.harbin.nova;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void set_reminder_click(View view){
        Intent start_set_reminder = new Intent(this, SetReminder.class);
        startActivity(start_set_reminder);
    }
}
