package com.example.harbin.nova;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.harbin.nova.data.ReminderContract;

import org.w3c.dom.Text;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {


    private Context mContext;

    private Cursor mCursor;

    public ReminderListAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }


    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.reminder_reminder_list_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {

        if(!mCursor.moveToPosition(position)){
            return ;
        }


        String medicine = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_MEDICINE));
        int dosage = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_DOSAGE));
        int NOofDosage = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_NO_OF_DOSAGE));
        int progress = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_PROGRESS));
        String info = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_INFO));
        long id = mCursor.getLong(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry._ID));
        String mDays = mCursor.getString(mCursor.getColumnIndex(ReminderContract.ReminderlistEntry.COLUMN_DAYS));

        holder.mDaysTextView.setText(mDays);
        holder.itemView.setTag(id);
        holder.medicineTextView.setText(medicine);
        holder.dosageTextView.setText(String.valueOf(dosage));
        holder.NOofDosageTextView.setText(String.valueOf(NOofDosage));
        holder.infoTextView.setText(info);
        holder.pb.setMax(progress);
        holder.pb.setProgress(0);

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

        public ReminderViewHolder(View itemView){
            super(itemView);
            medicineTextView = (TextView) itemView.findViewById(R.id.reminder_medicine_text_view);
            dosageTextView = (TextView) itemView.findViewById(R.id.reminder_dosage_text_view);
            NOofDosageTextView = (TextView) itemView.findViewById(R.id.reminder_NO_text_view);
            infoTextView = (TextView) itemView.findViewById(R.id.reminder_info_text_view);
            pb = (ProgressBar) itemView.findViewById(R.id.reminder_progress_progress_bar);
            mDaysTextView = (TextView) itemView.findViewById(R.id.reminder_Days_text_view);
        }

    }




}



