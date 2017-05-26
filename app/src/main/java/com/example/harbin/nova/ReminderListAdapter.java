package com.example.harbin.nova;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loonggg.alarmmanager.clock.data.ReminderContract;
import com.loonggg.alarmmanager.clock.data.ReminderDbHelper;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {


    private Context mContext;

    private Cursor mCursor;


    private SQLiteDatabase mDb;

    public ReminderListAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;

        ReminderDbHelper dbHelper = new ReminderDbHelper(context);
        mDb = dbHelper.getWritableDatabase();

    }


    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.reminder_reminder_list_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReminderViewHolder holder, int position) {

        if(!mCursor.moveToPosition(position)){
            return ;
        }


        final String medicine = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE));
        int dosage = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_DOSAGE));
        int NOofDosage = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE));
        String info = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_INFO));
        String time = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND));
        long id = mCursor.getLong(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));
        boolean remind = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_REMIND)) > 0;
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
                            String time = mCursor.getString(
                                    mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_TIME_REMIND));
                            String[] times = time.split(":");
                            AlarmManagerUtil.setAlarm(mContext, 2, Integer.parseInt(times[0]), Integer
                                    .parseInt(times[1]), id, i, medicine, 1);

                        }
                    }
                    // set alarm
                    Toast.makeText(mContext, "Reminder Added", Toast.LENGTH_SHORT).show();

                    // change sql data
                    ContentValues cv = new ContentValues();
                    cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMIND, 1);
                    mDb.update(ReminderContract.ReminderlistEntry.TABLE_NAME, cv,
                            "_id=" + idL, null);

                }else{
                    // cancel alarm
                    AlarmManagerUtil.cancelAlarm(mContext, AlarmManagerUtil.ALARM_ACTION, id);
                    Toast.makeText(mContext, "Reminder Canceled", Toast.LENGTH_SHORT).show();

                    // change sql data
                    ContentValues cv = new ContentValues();
                    cv.put(ReminderContract.ReminderlistEntry.COLUMN_REMIND, 0);
                    mDb.update(ReminderContract.ReminderlistEntry.TABLE_NAME, cv,
                            "_id="+idL, null);

                }
            }
        });
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
        TextView dosageTextView;
        TextView NOofDosageTextView;
        TextView infoTextView;
        ProgressBar pb;
        TextView mDaysTextView;

        TextView timeTextView;

        Switch remind_switch;

        public ReminderViewHolder(View itemView){
            super(itemView);
            medicineTextView = (TextView) itemView.findViewById(R.id.reminder_medicine_text_view);
            dosageTextView = (TextView) itemView.findViewById(R.id.reminder_dosage_text_view);
            NOofDosageTextView = (TextView) itemView.findViewById(R.id.reminder_NO_text_view);
            infoTextView = (TextView) itemView.findViewById(R.id.reminder_info_text_view);
            timeTextView = (TextView) itemView.findViewById(R.id.reminder_time_text_view);
            remind_switch = (Switch) itemView.findViewById(R.id.reminder_remind_switch);

        }

    }


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

}



