package com.example.harbin.nova.appointment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.harbin.nova.R;
import com.example.harbin.nova.login.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class AppointmentActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    protected FirebaseUser mFirebaseUser;
    protected DatabaseReference mDatabase;
    protected String mUserId;
    protected FirebaseAuth mFirebaseAuth;
    protected String doctorID;
    protected User mUser;

    protected List<WeekViewEvent> onlineEvents = new ArrayList<WeekViewEvent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
        doctorID = prefs.getString("doctorID","");




        Query mAppointmentQuery = mDatabase.child("doctors").child(doctorID).child("appointments");
        mAppointmentQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AppointmentInfo info = dataSnapshot.getValue(AppointmentInfo.class);
//                String [] days = info.startDay.split("-");
//                Calendar startTime = Calendar.getInstance();
//                startTime.set(Calendar.HOUR_OF_DAY, info.startTime);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, Integer.valueOf(days[1]));
//                startTime.set(Calendar.YEAR, Integer.valueOf(days[0]));
//                startTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(days[2]));

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, info.startTime);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.MONTH, info.startMonth - 1);
                startTime.set(Calendar.YEAR, info.startYear);
                startTime.set(Calendar.DAY_OF_MONTH, info.startDay);




                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, 1);

                WeekViewEvent event = new WeekViewEvent(2, info.firstname + " " + info.lastname, startTime, endTime);
                event.setColor(getResources().getColor(R.color.event_color_01));
                onlineEvents.add(event);

                getWeekView().notifyDatasetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                AppointmentInfo info = dataSnapshot.getValue(AppointmentInfo.class);

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, info.startTime);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.MONTH, info.startMonth - 1);
                startTime.set(Calendar.YEAR, info.startYear);
                startTime.set(Calendar.DAY_OF_MONTH, info.startDay);

                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, 1);

                WeekViewEvent event = new WeekViewEvent(2, info.firstname + " " + info.lastname, startTime, endTime);
                event.setColor(getResources().getColor(R.color.event_color_01));
                onlineEvents.remove(event);


                getWeekView().goToToday();
//                getWeekView().notifyDatasetChanged();
//                getWeekView().notifyDatasetChanged();
//                getWeekView().notifyDatasetChanged();
//                getWeekView().notifyDatasetChanged();
//                getWeekView().notifyDatasetChanged();
//                getWeekView().notifyDatasetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Query mUserQuery = mDatabase.child("users").child(mUserId);
        mUserQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.appointment_weekView);

// Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

// The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

// Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        mWeekView.setEmptyViewLongPressListener(this);

        setupDateTimeInterpreter(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time){
        return String.format("Event of %02d:%s %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    protected int getEventTime(Calendar time) {
//        return String.format("%02d:%s %s/%d %d", time.get(Calendar.HOUR_OF_DAY), "00", time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.YEAR));
//        return String.format("Event of %02d:%s %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        return time.get(Calendar.HOUR_OF_DAY);
    }

    protected String getEventDay(Calendar time) {
        return String.format("%d-%d-%d", time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
//        return String.format("Event of %02d:%s %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    protected String getPressedTime(Calendar time){
        return String.format("%02d:%s-%02d:%s?", time.get(Calendar.HOUR_OF_DAY), "00", time.get(Calendar.HOUR_OF_DAY)+1, "00");
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);

        confirmBuilder.setMessage("Are you sure to cancel this appointment?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Calendar cal = event.getStartTime();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) + 1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        String timeKey = String.valueOf(year) + "_" + String.valueOf(month) + "_" +
                                String.valueOf(day) + "_" + String.valueOf(hour);
                        mDatabase.child("doctors").child(doctorID).child("appointments").child(timeKey).removeValue();
                        dialog.cancel();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog confirmDialog = confirmBuilder.create();
        confirmDialog.setTitle("Confirm");
        confirmDialog.show();




    }

    @Override
    public void onEmptyViewLongPress(final Calendar time) {
//        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        final int hour = getEventTime(time);
//        final String day = String.valueOf(getEventDay(time));
        final int year = time.get(Calendar.YEAR);
        final int month = time.get(Calendar.MONTH) + 1;
        final int day = time.get(Calendar.DAY_OF_MONTH);
        confirmBuilder.setMessage("Are you sure to create an appointment in " + getPressedTime(time))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        AppointmentInfo info = new AppointmentInfo(hour, mUser.firstname, mUser.lastname,
                                mUser.email, year, month, day);
                        String timeKey = String.valueOf(year) + "_" + String.valueOf(month) + "_" +
                                String.valueOf(day) + "_" + String.valueOf(hour);
                        mDatabase.child("doctors").child(doctorID).child("appointments").child(timeKey).setValue(info);
//                        String key = mDatabase.child("doctors").child(doctorID).child("appointments").push().getKey();
//                        Map<String, Object> childUpdates = new HashMap<>();
//                        childUpdates.put("/doctors/"+ doctorID +"/appointments/" + key, info);
//                        mDatabase.child("doctors").child(doctorID).child("appointments").child(key).setValue(info);
//                        mDatabase.updateChildren(childUpdates);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog confirmDialog = confirmBuilder.create();
        confirmDialog.setTitle("Confirm");
        confirmDialog.show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }
}
