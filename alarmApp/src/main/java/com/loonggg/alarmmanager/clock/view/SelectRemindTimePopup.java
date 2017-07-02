package com.loonggg.alarmmanager.clock.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class SelectRemindTimePopup implements OnClickListener {

    private TextView tv_b1, tv_b2, tv_l1, tv_l2, tv_d1, tv_d2, tv_sure;
    public PopupWindow mPopupWindow;
    private SelectRemindTimePopupOnClickListener selectRemindTimePopupListener;

    public PopupWindow getmPopupWindow() {
        return mPopupWindow;
    }

    private Context mContext;

    @SuppressWarnings("deprecation")
    public SelectRemindTimePopup(Context context) {
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
                // mPopupWindow.dismiss();
                return true;
            }
        });

    }

    public View initViews() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.selectremindtime_pop_window,
                null);



        tv_b1 = (TextView) view.findViewById(R.id.tv_before_breakfast);
        tv_b2 = (TextView) view.findViewById(R.id.tv_after_breakfast);
        tv_l1 = (TextView) view.findViewById(R.id.tv_before_lunch);
        tv_l2 = (TextView) view.findViewById(R.id.tv_after_lunch);
        tv_d1 = (TextView) view.findViewById(R.id.tv_before_dinnar);
        tv_d2 = (TextView) view.findViewById(R.id.tv_after_dinnar);

        tv_sure = (TextView) view.findViewById(R.id.tv_drugcycle_sure);

        tv_b1.setOnClickListener(this);
        tv_b2.setOnClickListener(this);
        tv_d1.setOnClickListener(this);
        tv_d2.setOnClickListener(this);
        tv_l1.setOnClickListener(this);
        tv_l2.setOnClickListener(this);
        tv_sure.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Drawable nav_right = mContext.getResources().getDrawable(R.drawable.cycle_check);
        nav_right.setBounds(0, 0, nav_right.getMinimumWidth(), nav_right.getMinimumHeight());
        int id = v.getId();


        if(id == R.id.tv_before_breakfast) {
            if (tv_b1.getCompoundDrawables()[2] == null)
                tv_b1.setCompoundDrawables(null, null, nav_right, null);
            else tv_b1.setCompoundDrawables(null, null, null, null);
            selectRemindTimePopupListener.obtainMessage(0, "");
        }else if(id == R.id.tv_after_breakfast) {
            if (tv_b2.getCompoundDrawables()[2] == null)
                tv_b2.setCompoundDrawables(null, null, nav_right, null);
            else tv_b2.setCompoundDrawables(null, null, null, null);
            selectRemindTimePopupListener.obtainMessage(1, "");
        }else if(id == R.id.tv_before_lunch) {
            if (tv_l1.getCompoundDrawables()[2] == null)
                tv_l1.setCompoundDrawables(null, null, nav_right, null);
            else tv_l1.setCompoundDrawables(null, null, null, null);
            selectRemindTimePopupListener.obtainMessage(2, "");
        }else if(id == R.id.tv_after_lunch) {
            if (tv_l2.getCompoundDrawables()[2] == null)
                tv_l2.setCompoundDrawables(null, null, nav_right, null);
            else tv_l2.setCompoundDrawables(null, null, null, null);
            selectRemindTimePopupListener.obtainMessage(3, "");
        }else if(id == R.id.tv_before_dinnar) {
            if (tv_d1.getCompoundDrawables()[2] == null)
                tv_d1.setCompoundDrawables(null, null, nav_right, null);
            else tv_d1.setCompoundDrawables(null, null, null, null);
            selectRemindTimePopupListener.obtainMessage(4, "");
        }else if(id == R.id.tv_after_dinnar) {
            if (tv_d2.getCompoundDrawables()[2] == null)
                tv_d2.setCompoundDrawables(null, null, nav_right, null);
            else tv_d2.setCompoundDrawables(null, null, null, null);
            selectRemindTimePopupListener.obtainMessage(5, "");
        }else if(id == R.id.tv_drugcycle_sure) {
            int remind = ((tv_b1.getCompoundDrawables()[2] == null) ? 0 : 1) * 1
                    + ((tv_b2.getCompoundDrawables()[2] == null) ? 0 : 1) * 2
                    + ((tv_l1.getCompoundDrawables()[2] == null) ? 0 : 1) * 4
                    + ((tv_l2.getCompoundDrawables()[2] == null) ? 0 : 1) * 8
                    + ((tv_d1.getCompoundDrawables()[2] == null) ? 0 : 1) * 16
                    + ((tv_d2.getCompoundDrawables()[2] == null) ? 0 : 1) * 32;
            selectRemindTimePopupListener.obtainMessage(7, String.valueOf(remind));
            dismiss();
        }

    }

    public interface SelectRemindTimePopupOnClickListener {
        void obtainMessage(int flag, String ret);
    }

    public void setOnSelectRemindTimePopupListener(SelectRemindTimePopupOnClickListener l) {
        this.selectRemindTimePopupListener = l;
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
