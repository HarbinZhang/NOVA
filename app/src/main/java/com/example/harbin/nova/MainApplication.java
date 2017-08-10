package com.example.harbin.nova;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.loonggg.lib.alarmmanager.clock.SetReminder;

/**
 * Created by Harbin on 7/24/17.
 */

public class MainApplication extends Application {

    public static final String LOG_TAG = "AppAuthSample";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void startNewActivity(Context context, String action){
        if("goReminder".equals(action)){
            Intent intent = new Intent(context, SetReminder.class);
            startActivity(intent);
        }
    }
}
