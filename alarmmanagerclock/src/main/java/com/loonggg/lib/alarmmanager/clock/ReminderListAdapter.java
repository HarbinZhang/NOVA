package com.loonggg.lib.alarmmanager.clock;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loonggg.lib.alarmmanager.clock.data.ReminderContract;
import com.loonggg.lib.alarmmanager.clock.data.ReminderDbHelper;

import java.util.Calendar;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {


    private Context mContext;

    private Cursor mCursor;



    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;

    private SQLiteDatabase mDb;

    private static SharedPreferences pres;
    private static SharedPreferences.Editor editor;

    private String[] times = new String[6];

    final static private String[] remindTimeName = {"beforeBreakfastTime", "afterBreakfastTime", "beforeLunchTime",
            "afterLunchTime", "beforeDinnerTime", "afterDinnerTime"};

    final static private String[] remindTimeValue = {"7:00", "8:00", "12:00", "13:00", "18:00" , "19:00"};





    public ReminderListAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;

        pres = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pres.edit();



        times[0] = pres.getString("beforeBreakfastTime", remindTimeValue[0]);
        times[1] = pres.getString("afterBreakfastTime", remindTimeValue[1]);
        times[2] = pres.getString("beforeLunchTime", remindTimeValue[2]);
        times[3] = pres.getString("afterLunchTime", remindTimeValue[3]);
        times[4] = pres.getString("beforeDinnerTime", remindTimeValue[4]);
        times[5] = pres.getString("afterDinnerTime", remindTimeValue[5]);



        ReminderDbHelper dbHelper = new ReminderDbHelper(context);
        mDb = dbHelper.getWritableDatabase();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }


    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.activity_reminder_list_adapter, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReminderViewHolder holder, int position) {

        if(!mCursor.moveToPosition(position)){
            return ;
        }

        final String medicine = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE));
        final String strength = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_STRENGTH));
        final String time = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_REMINDTIME));
        final long id = mCursor.getLong(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));
        final int state = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_STATE));

        holder.itemView.setTag(id);
        holder.medicineTextView.setText(medicine);
        holder.strengthTextView.setText(strength);
        holder.remindTime.setText(times[Integer.valueOf(time)-1]);

        if(state == 0){
            holder.imageState.setImageResource(R.drawable.alarm_pink);
        }else if(state == 2){
            holder.imageState.setImageResource(R.drawable.alarm_grey);
        }else {
            holder.imageState.setImageResource(R.drawable.alarm_red);

            int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            mDatabase.child("users").child(mUserId).child("reminders").child("eaten")
                    .child(medicine).child(String.valueOf(weekday-1)).setValue(-1);
        }


        if(strength.contains("tablet")){
            holder.imageView.setImageResource(R.drawable.tablet);
        }else if(strength.contains("capsule")){
            holder.imageView.setImageResource(R.drawable.capsule);
        }else if(strength.contains("spoon")){
            holder.imageView.setImageResource(R.drawable.spoon);
        }else{
            holder.imageView.setImageResource(R.drawable.unknown);
        }


        holder.imageState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1) {
                    changeState(id);
                    Toast.makeText(mContext, " Medicine eaten ", Toast.LENGTH_SHORT).show();
                    holder.imageState.setImageResource(R.drawable.alarm_grey);

                    int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    mDatabase.child("users").child(mUserId).child("reminders").child("eaten")
                            .child(medicine).child(String.valueOf(weekday-1)).setValue(1);
                }
            }
        });

/*
//        final String medicine = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE));
//        int dosage = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_DOSAGE));
//        int NOofDosage = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE));
//        String info = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_INFO));
//        String time = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND));
//        long id = mCursor.getLong(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));
//        boolean remind = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_REMIND)) > 0;
//        String mDays = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_DAYS));



//        holder.mDaysTextView.setText(mDays);
        holder.itemView.setTag(id);
        holder.medicineTextView.setText(medicine);
        holder.dosageTextView.setText(String.valueOf(dosage));
        holder.NOofDosageTextView.setText(String.valueOf(NOofDosage));
        holder.infoTextView.setText(info);
        holder.timeTextView.setText(time);
        holder.remind_switch.setChecked(remind);
        holder.remind_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                int id = holder.itemView.getTag().intValue();
                long idL = (long) holder.itemView.getTag();
                int id = safeLongToInt(idL);

                if(isChecked){
                    // read cycle from sql
                    for(int i=1; i<=7; i++){
                        boolean remind = mCursor.getInt(mCursor.getColumnIndex("weekday_"+String.valueOf(i))) > 0;
                        if(remind){
//                            String time = mCursor.getString(
//                                    mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND));
//                            String[] times = time.split(":");
//                            AlarmManagerUtil.setAlarm(mContext, 2, Integer.parseInt(times[0]), Integer
//                                    .parseInt(times[1]), id, i, medicine, 1, 1);

                        }
                    }
                    // set alarm
                    Toast.makeText(mContext, "Reminder Added", Toast.LENGTH_SHORT).show();

                    // change sql data
                    ContentValues cv = new ContentValues();
//                    cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMIND, 1);
                    mDb.update(ReminderContract.ReminderlistEntry.TABLE_NAME, cv,
                            "_id=" + idL, null);

                }else{
                    // cancel alarm
                    AlarmManagerUtil.cancelAlarm(mContext, AlarmManagerUtil.ALARM_ACTION, id);
                    Toast.makeText(mContext, "Reminder Canceled", Toast.LENGTH_SHORT).show();

                    // change sql data
                    ContentValues cv = new ContentValues();
//                    cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMIND, 0);
//                    mDb.update(ReminderContract.ReminderlistEntry.TABLE_NAME, cv,
//                            "_id="+idL, null);

                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor){
        if(mCursor != null) mCursor.close();
        mCursor = newCursor;
        if(newCursor != null){
            this.notifyDataSetChanged();
        }
    }




    class ReminderViewHolder extends RecyclerView.ViewHolder{

        TextView medicineTextView;
        TextView strengthTextView;
        TextView remindTime;
        ImageView imageView;
        ImageView imageState;

//        TextView dosageTextView;
//        TextView NOofDosageTextView;
//        TextView infoTextView;
//        ProgressBar pb;
//        TextView mDaysTextView;

//        TextView timeTextView;

        Switch remind_switch;

        public ReminderViewHolder(View itemView){
            super(itemView);
            medicineTextView = (TextView) itemView.findViewById(R.id.reminder_medicine_text_view);
            strengthTextView = (TextView) itemView.findViewById(R.id.reminder_strength_text_view);
            remindTime = (TextView) itemView.findViewById(R.id.reminder_remindTime_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.image_reminder);
//            dosageTextView = (TextView) itemView.findViewById(R.id.reminder_dosage_text_view);
//            NOofDosageTextView = (TextView) itemView.findViewById(R.id.reminder_NO_text_view);
//            infoTextView = (TextView) itemView.findViewById(R.id.reminder_info_text_view);
//            timeTextView = (TextView) itemView.findViewById(R.id.reminder_time_text_view);
            imageState = (ImageView) itemView.findViewById(R.id.image_state);
        }

    }


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }


    private void changeState(final long sqlID){
        if(sqlID == -1){
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(ReminderContract.ReminderlistEntry.COLUMN_STATE, 2);

        String whereClause = ReminderContract.ReminderlistEntry._ID + " = " + sqlID;

        mDb.update(
                ReminderContract.ReminderlistEntry.TABLE_NAME,
                cv,
                whereClause,
                null
        );


    }

}



