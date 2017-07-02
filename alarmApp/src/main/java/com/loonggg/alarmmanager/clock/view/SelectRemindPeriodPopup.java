package com.loonggg.alarmmanager.clock.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loonggg.alarmmanager.clock.R;


public class SelectRemindPeriodPopup implements OnClickListener {
    private TextView everyday_tv, second_tv, third_tv, fourth_tv, fifth_tv, sixth_tv, seventh_tv;
    public PopupWindow mPopupWindow;
    private SelectRemindPeriodPopupOnClickListener selectRemindPeriodPopupListener;

    public PopupWindow getmPopupWindow() {
        return mPopupWindow;
    }

    private Context mContext;

    @SuppressWarnings("deprecation")
    public SelectRemindPeriodPopup(Context context) {
        mContext = context;
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.FILL_PARENT);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
        mPopupWindow.setContentView(initViews());
        mPopupWindow.getContentView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPopupWindow.setFocusable(false);
                mPopupWindow.dismiss();
                return true;
            }
        });

    }

    public View initViews() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.selectremindperiod_pop_window,
                null);

        everyday_tv = (TextView) view.findViewById(R.id.everyday_tv);
        second_tv = (TextView) view.findViewById(R.id.second_tv);
        third_tv = (TextView) view.findViewById(R.id.third_tv);
        fourth_tv = (TextView) view.findViewById(R.id.fourth_tv);
        fifth_tv = (TextView) view.findViewById(R.id.fifth_tv);
        sixth_tv = (TextView) view.findViewById(R.id.sixth_tv);
        seventh_tv = (TextView) view.findViewById(R.id.seventh_tv);

        everyday_tv.setOnClickListener(this);
        second_tv.setOnClickListener(this);
        third_tv.setOnClickListener(this);
        fourth_tv.setOnClickListener(this);
        fifth_tv.setOnClickListener(this);
        sixth_tv.setOnClickListener(this);
        seventh_tv.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.everyday_tv){
            selectRemindPeriodPopupListener.obtainMessage(1);
        }
        else if(id == R.id.second_tv){
            selectRemindPeriodPopupListener.obtainMessage(2);
        }
        else if(id == R.id.third_tv){
            selectRemindPeriodPopupListener.obtainMessage(3);
        }
        else if(id == R.id.fourth_tv){
            selectRemindPeriodPopupListener.obtainMessage(4);
        }
        else if(id == R.id.fifth_tv){
            selectRemindPeriodPopupListener.obtainMessage(5);
        }
        else if(id == R.id.sixth_tv){
            selectRemindPeriodPopupListener.obtainMessage(6);
        }
        else if(id == R.id.seventh_tv){
            selectRemindPeriodPopupListener.obtainMessage(7);
        }
        dismiss();

    }

    public interface SelectRemindPeriodPopupOnClickListener {
        void obtainMessage(int flag);
    }

    public void setOnSelectRemindPeriodPopupListener(SelectRemindPeriodPopupOnClickListener l) {
        this.selectRemindPeriodPopupListener = l;
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void showPopup(View rootView) {
        // 第一个参数是要将PopupWindow放到的View，第二个参数是位置，第三第四是偏移值
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }
}
