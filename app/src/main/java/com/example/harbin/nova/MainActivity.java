package com.example.harbin.nova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        debug = (TextView) findViewById(R.id.tv_main_debug);


        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        mDb = dbHelper.getWritableDatabase();


    }




    @Override
    protected void onStart() {
        super.onStart();



        if(mFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else {
            mUserId = mFirebaseUser.getUid();

            final SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
            String userID = prefs.getString("userID", "hi");
            debug.setText(userID);


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

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mFirebaseUser == null){
            getMenuInflater().inflate(R.menu.menu_signin, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        Intent goto_vitals = new Intent(this, OAuthActivity.class);
        startActivity(goto_vitals);
    }
}
