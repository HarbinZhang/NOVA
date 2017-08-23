package com.example.harbin.nova;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.harbin.nova.appointment.BasicAppointments;
import com.example.harbin.nova.login.LoginActivity;
import com.example.harbin.nova.login.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loonggg.lib.alarmmanager.clock.SetReminder;
import com.loonggg.lib.alarmmanager.clock.data.ReminderContract;
import com.loonggg.lib.alarmmanager.clock.data.ReminderDbHelper;

public class MainActivity extends AppCompatActivity {

    private TextView debug;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;

    private SQLiteDatabase mDb;
    public String output;
    public boolean firstServiceStart=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        debug = (TextView) findViewById(R.id.tv_main_debug);


        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
    }





    @Override
    protected void onStart() {
        super.onStart();
        boolean result = isMyServiceRunning(MyService.class);
        if (!result) {
            if (firstServiceStart) {
                System.gc();
                Intent serviceIntent = new Intent(getBaseContext(), MyService.class);
                serviceIntent.putExtra("uid", mUserId);
                final SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
                String doctorId = prefs.getString("doctorID", "");
                serviceIntent.putExtra("doctorId", doctorId);
                startService(serviceIntent);
                firstServiceStart = true;
            }
        }



        if(mFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else {
            mUserId = mFirebaseUser.getUid();

            final SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
            String userID = prefs.getString("userID", "hi");
//            debug.setText(userID);


            Query doctorIDQuery = mDatabase.child("users").child(mUserId).child("doctorID");
            doctorIDQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("doctorID", id);
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Query patientFirstnameQuery = mDatabase.child("users").child(mUserId).child("firstname");
            patientFirstnameQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.getValue(String.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("firstname", firstName);
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            Query patientLastnameQuery = mDatabase.child("users").child(mUserId).child("lastname");
            patientLastnameQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.getValue(String.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastname", firstName);
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            Query doctorFirstnameQuery = mDatabase.child("users").child(mUserId).child("doctorFirstname");
            doctorFirstnameQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.getValue(String.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("doctorFirstname", firstName);
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            Query doctorLastnameQuery = mDatabase.child("users").child(mUserId).child("doctorLastname");
            doctorLastnameQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String lastName = dataSnapshot.getValue(String.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("doctorLastname", lastName);
                    editor.commit();
                    boolean result = isMyServiceRunning(MyService.class);
                    if (!result) {
                        System.gc();
                        Intent serviceIntent = new Intent(getBaseContext(), MyService.class);
                        serviceIntent.putExtra("uid", mUserId);
                        String doctorId = prefs.getString("doctorID", "");
                        serviceIntent.putExtra("doctorId", doctorId);
                        startService(serviceIntent);
                        firstServiceStart=true;
                    }
                    //startService(new Intent(getBaseContext(), MyService.class));
                    change_button_text();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mFirebaseUser == null){
            getMenuInflater().inflate(R.menu.menu_signin, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu, menu);
        }

//        menu.findItem(R.id.menu_home).setEnabled(false);
//        MenuItem item = menu.findItem(R.id.menu_home);

//        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_urgentcall:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "911", null));
                startActivity(intent);
                break;
            case R.id.menu_logout:
                mFirebaseUser = null;
                mFirebaseAuth.signOut();
                mDb.execSQL("delete from " + ReminderContract.ReminderlistEntry.TABLE_NAME);
                finish();
                startActivity(getIntent());
                break;
            case R.id.menu_signIn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.menu_signUp:
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
                break;

            case R.id.menu_timechange:
                startActivity(new Intent(MainActivity.this, Setting.class));

        }
        return super.onOptionsItemSelected(item);
    }

    public void set_reminder_click(View view){
        Intent start_set_reminder = new Intent(this, SetReminder.class);
        startActivity(start_set_reminder);
    }

    public void sign_in_click(View view){
        Intent sign_in_click = new Intent(this, SignupActivity.class);
        startActivity(sign_in_click);
    }

    public void goto_appointment(View view){
        Intent goto_appointment = new Intent(this, BasicAppointments.class);
        startActivity(goto_appointment);
    }

    public void goto_setting(View view){
        Intent goto_setting = new Intent(this, Setting.class);
        startActivity(goto_setting);
    }

    public void goto_vitals(View view){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        PackageManager pm = getPackageManager();
        intent = pm.getLaunchIntentForPackage("iHealthMyVitals.V2");
//        intent.setClassName("iHealthMyVitals","com.ihealth.main.MainActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);

    }

    public void Btn_call_doctor_click(View view){
        Intent call_doctor_click = new Intent(this, RtcActivity.class);
        call_doctor_click.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        call_doctor_click.putExtra("isCalling", "yes");
        startActivity(call_doctor_click);
//        finish();
    }

    public void change_button_text(){
//        final SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
//        String firstName=prefs.getString("doctorFirstname", "not");
//        String lastName=prefs.getString("doctorLastname", "available");
//        output="Call "+firstName+" "+lastName;
//        Button button_call_doctor = (Button)findViewById(R.id.button_call_doctor);
//        button_call_doctor.setText(output);
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

    public void goto_check_symp(View view){
        Intent goto_check = new Intent(this, CheckSympActivity.class);
        startActivity(goto_check);
    }

    public void goto_patient_history(View view){
        Intent goto_patient = new Intent(this, PatientHistoryActivity.class);
        startActivity(goto_patient);
    }



}
