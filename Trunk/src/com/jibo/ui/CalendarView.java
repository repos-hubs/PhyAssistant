package com.jibo.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.PopupWindow;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;

public class CalendarView {
	private PopupWindow dayPopupWindow;
	private View dayPopupView;
	private WheelView days;
	private WheelView years;
	private WheelView months;
	private Button dayDoneBtn, dayCancelBtn;
	
	private static int START_YEAR = 1800, END_YEAR =2100;
	
	private BaseActivity context;
	
	public CalendarView(BaseActivity context){
		this.context = context;
		
	}
	/**
	 * 　弹出设置日期的ｐｏｐｗｉｎｄｏｗ
	 */
	public void showSetDayWindow(final Calendar c) {
		if (null == dayPopupView) {
			dayPopupView = context.getLayoutInflater().inflate(R.layout.set_day_pop,
					null);
			days = (WheelView) dayPopupView.findViewById(R.id.day);
			years = (WheelView) dayPopupView.findViewById(R.id.year);
			months = (WheelView) dayPopupView.findViewById(R.id.month);

			years.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));
			years.setLabel("年");
			years.setInterpolator(new AnticipateOvershootInterpolator());

			months.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
			months.setLabel("月");
			months.setCyclic(true);
			months.setInterpolator(new AnticipateOvershootInterpolator());

			days.setLabel("日");
			days.setCyclic(true);
			days.setInterpolator(new AnticipateOvershootInterpolator());

			OnWheelChangedListener changeListener = new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					int a = getDays(START_YEAR + years.getCurrentItem(),
							months.getCurrentItem());
					int currentItem = days.getCurrentItem();
					days.setAdapter(new NumericWheelAdapter(1, a, "%02d"));
					days.invalidate();
					if (currentItem >= a)
						days.setCurrentItem(0);
				}
			};

			years.addChangingListener(changeListener);
			months.addChangingListener(changeListener);

			dayDoneBtn = (Button) dayPopupView.findViewById(R.id.done_btn);
			dayDoneBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int year = START_YEAR + years.getCurrentItem();
					int month = months.getCurrentItem();
					int day = days.getCurrentItem() + 1;
					GregorianCalendar date = new GregorianCalendar(year, month,
							day);
					// TODO add you action
					dayPopupWindow.dismiss();
				}
			});

			dayCancelBtn = (Button) dayPopupView.findViewById(R.id.cancel_btn);
			dayCancelBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dayPopupWindow.dismiss();
				}
			});
		}

		if (null == dayPopupWindow) {
			dayPopupWindow = new PopupWindow(dayPopupView, context.getWindowManager().getDefaultDisplay().getWidth(),
					ViewGroup.LayoutParams.WRAP_CONTENT);
			dayPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			dayPopupWindow.setOutsideTouchable(true);
			dayPopupWindow.setAnimationStyle(R.style.dialogWindowAnim);
			dayPopupWindow.update();
			dayPopupWindow.setTouchable(true);
			dayPopupWindow.setFocusable(true);
		}

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		days.setAdapter(new NumericWheelAdapter(1, getDays(year, month), "%02d"));

		years.setCurrentItem(year - START_YEAR);
		months.setCurrentItem(month);
		days.setCurrentItem(day - 1);

		dayPopupWindow.showAtLocation(
				context.getWindow().getDecorView().findViewById(R.id.parent_layout),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	private int getDays(int year, int month) {
		month = month == 11 ? 12 : (month + 1) % 12;
		int days = 0;
		if (month != 2) {
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				days = 31;
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				days = 30;
				break;
			}
		} else {
			// 闰年
			if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
				days = 29;
			else
				days = 28;

		}
		return days;

	}
	
}
