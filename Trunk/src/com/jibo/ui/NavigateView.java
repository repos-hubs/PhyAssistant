package com.jibo.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.api.android.GBApp.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义导航条
 * 
 * @author simon
 * 
 */
public class NavigateView extends RelativeLayout implements OnClickListener {

	public static final int NORMAL_TYPE = 0;
	public static final int CATEGORY_TYPE = 1;
	public static final int OTHER_TYPE = 2;
	public static final int CATEGORY1_TYPE = 3;

	private Context mContext;
	private LayoutInflater inflater;
	/**
	 * UI组件
	 */
	private RelativeLayout normalLayout, categoryLayout, otherLayout, categoryLayout1;
	private TextView normalText, categoryText, otherText, categoryText1;
	private ImageView normalAnimationBtn, categoryAnimationBtn,
			otherAnimationBtn, categoryAnimationBtn1;
	/** 分割线，分别位于normalAnimationBtn, categoryAnimationBtn之右*/
	private ImageView lineSperater1, lineSperater2, lineSperater3;
	/**
	 * 监听事件
	 */
	private OnChangeListener onChangeListener;
	private GotoBackFirstInit gotoBackListener;

	/** 当前动画执行UI组件 */
	private ImageView currentAnimationBtn;
	private Timer timer;
	private TimerTask timerTask;
	private int version_alpha = 255;
	private int version_flag = 1;
	/** 当前位置 */
	private int currentType;
	
	/** 按钮是否可点击 */
	public boolean NORMAL_IS_ENABLED = true;
	public boolean CATEGORY_IS_ENABLED = true;
	public boolean OTHER_IS_ENABLED = true;
	public boolean CATEGORY1_IS_ENABLED = true;

	public NavigateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.navigateview_layout, this, true);
		inits();
	}
	
	public int getCurrentType(){
		return currentType;
	}
	
	/**
	 * 设置tab个数，用药安全为四个
	 * @param count
	 */
	public void setTabCount(int count){
		if(count == 4){
			categoryLayout1.setVisibility(View.VISIBLE);
		}
	}

	/***
	 * 初始化UI
	 */
	private void inits() {
		
		NORMAL_IS_ENABLED = true;
		CATEGORY_IS_ENABLED = true;
		OTHER_IS_ENABLED = true;

		normalLayout = (RelativeLayout) this.findViewById(R.id.normal_layout);
		categoryLayout = (RelativeLayout) this
				.findViewById(R.id.category_layout);
		categoryLayout1 = (RelativeLayout) this.findViewById(R.id.category_layout1);
		otherLayout = (RelativeLayout) this.findViewById(R.id.other_layout);

		normalText = (TextView) this.findViewById(R.id.normal_text);
		normalAnimationBtn = (ImageView) this
				.findViewById(R.id.normal_animation_btn);
		categoryText = (TextView) this.findViewById(R.id.category_text);
		categoryAnimationBtn = (ImageView) this
				.findViewById(R.id.category_animation_btn);
		categoryText1 = (TextView) this.findViewById(R.id.category_text1);
		categoryAnimationBtn1 = (ImageView) this
				.findViewById(R.id.category_animation_btn1);
		otherText = (TextView) this.findViewById(R.id.other_text);
		otherAnimationBtn = (ImageView) this
				.findViewById(R.id.other_animation_btn);
		
		lineSperater1 = (ImageView) findViewById(R.id.linesperater1);
		lineSperater2 = (ImageView) findViewById(R.id.linesperater2);
		lineSperater3 = (ImageView) findViewById(R.id.linesperater3);

		normalLayout.setOnClickListener(this);
		categoryLayout.setOnClickListener(this);
		categoryLayout1.setOnClickListener(this);
		otherLayout.setOnClickListener(this);

	}

	/**
	 * 设置标题栏字体
	 * 
	 * @param type
	 * @param text
	 */
	public void setText(int type, String text) {
		switch (type) {
		case NORMAL_TYPE:
			normalText.setText(text);
			break;
		case CATEGORY_TYPE:
			categoryText.setText(text);
			break;
		case OTHER_TYPE:
			otherText.setText(text);
			break;
		case CATEGORY1_TYPE:
			categoryText1.setText(text);
			break;
		}
	}

	public void setText(int type, int resId) {
		switch (type) {
		case NORMAL_TYPE:
			normalText.setText(resId);
			break;
		case CATEGORY_TYPE:
			categoryText.setText(resId);
			break;
		case OTHER_TYPE:
			otherText.setText(resId);
			break;
		case CATEGORY1_TYPE:
			categoryText1.setText(resId);
			break;
		}
	}

	public void onClick(final View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}

		int type = 0;
		switch (v.getId()) {
		case R.id.normal_layout:
			if(!NORMAL_IS_ENABLED) return;
			if(categoryLayout1.getVisibility() == View.VISIBLE){
				lineSperater2.setVisibility(View.VISIBLE);
				lineSperater3.setVisibility(View.VISIBLE);
			}else{
				lineSperater2.setVisibility(View.VISIBLE);
			}
			type = NORMAL_TYPE;
			break;
		case R.id.category_layout:
			if(!CATEGORY_IS_ENABLED) return;
			if(categoryLayout1.getVisibility() == View.VISIBLE){
				lineSperater3.setVisibility(View.VISIBLE);
				lineSperater1.setVisibility(View.GONE);
				lineSperater2.setVisibility(View.GONE);
			}else{
				lineSperater1.setVisibility(View.GONE);
			}
			type = CATEGORY_TYPE;
			break;
		case R.id.other_layout:
			if(!OTHER_IS_ENABLED) return;
			if(categoryLayout1.getVisibility() == View.VISIBLE){
				lineSperater1.setVisibility(View.VISIBLE);
				lineSperater2.setVisibility(View.GONE);
				lineSperater3.setVisibility(View.GONE);
			}else{
				lineSperater1.setVisibility(View.VISIBLE);
				lineSperater2.setVisibility(View.GONE);
			}
			type = OTHER_TYPE;
			break;
		case R.id.category_layout1:
			if(!CATEGORY1_IS_ENABLED) return;
			if(categoryLayout1.getVisibility() == View.VISIBLE){
				lineSperater1.setVisibility(View.VISIBLE);
				lineSperater3.setVisibility(View.GONE);
				lineSperater2.setVisibility(View.VISIBLE);
			}
			type = CATEGORY1_TYPE;
			break;
		}
		if (currentType == type) {//重复点击当前选项卡，触发goback事件
			if (gotoBackListener != null)
				gotoBackListener.gotoBack(type);
			return;
		}
		if(onChangeListener!=null){
			onChangeListener.onChange(type);
		}
	}
	
	/**
	 * 设置按钮是否可点击
	 * @param type
	 * @param isEnabled
	 */
	public void setEnabled(int type, boolean isEnabled){
		switch(type){
		case NORMAL_TYPE:
			NORMAL_IS_ENABLED = isEnabled;
			if(isEnabled){
				normalText.setTextColor(Color.BLACK);
			}else{
				normalText.setTextColor(R.color.ltgray);
			}
			break;
		case CATEGORY_TYPE:
			CATEGORY_IS_ENABLED = isEnabled;
			if(isEnabled){
				categoryText.setTextColor(Color.BLACK);
			}else{
				categoryText.setTextColor(R.color.ltgray);
			}
			break;
		case OTHER_TYPE:
			OTHER_IS_ENABLED = isEnabled;
			if(isEnabled){
				otherText.setTextColor(Color.BLACK);
			}else{
				otherText.setTextColor(R.color.ltgray);
			}
			break;
		}
	}

	/**
	 * 当前在某一模块下，并且再次点击响应的事件监听
	 * 
	 * @author simon
	 * 
	 */
	public interface GotoBackFirstInit {
		public void gotoBack(int type);
	}

	/**
	 * 导航栏切换回调
	 * 
	 * @author simon
	 * 
	 */
	public interface OnChangeListener {
		public void onChange(int type);
	}

	public void setOnChangeListener(OnChangeListener onChangeListener) {
		this.onChangeListener = onChangeListener;
	}

	public void setGotoBackListener(GotoBackFirstInit gotoBackListener) {
		this.gotoBackListener = gotoBackListener;
	}

	/***
	 * 改变导航UI状态
	 * 
	 * @param type
	 */
	public void changeUI(int type) {

		RelativeLayout layoutDefs[] = new RelativeLayout[] { normalLayout,
				categoryLayout, otherLayout };
		TextView texts[] = new TextView[]{normalText,categoryText,otherText};
		if(categoryLayout1.getVisibility() == View.VISIBLE){
			layoutDefs = new RelativeLayout[] { normalLayout,
					categoryLayout, otherLayout , categoryLayout1};
			texts = new TextView[]{normalText,categoryText,otherText,categoryText1};
		}
		layoutDefs[currentType]
				.setBackgroundResource(R.drawable.tabwidget_select1);
		layoutDefs[type].setBackgroundResource(R.drawable.tabwidget_select2);
		texts[currentType].setTextColor(Color.BLACK);
		texts[type].setTextColor(Color.WHITE);
//		startTimer(type);
		currentType = type;
		switch(type){
		case 0:
			if(NORMAL_IS_ENABLED){
				if(categoryLayout1.getVisibility() == View.VISIBLE){
					lineSperater2.setVisibility(View.VISIBLE);
					lineSperater3.setVisibility(View.VISIBLE);
				}else{
					lineSperater2.setVisibility(View.VISIBLE);
				}
			}
			break;
		case 1:
			if(CATEGORY_IS_ENABLED){
				if(categoryLayout1.getVisibility() == View.VISIBLE){
					lineSperater3.setVisibility(View.VISIBLE);
					lineSperater1.setVisibility(View.GONE);
					lineSperater2.setVisibility(View.GONE);
				}else{
					lineSperater1.setVisibility(View.GONE);
				}
			}
			break;
		case 2:
			if(OTHER_IS_ENABLED){
				if(categoryLayout1.getVisibility() == View.VISIBLE){
					lineSperater1.setVisibility(View.VISIBLE);
					lineSperater2.setVisibility(View.GONE);
					lineSperater3.setVisibility(View.GONE);
				}else{
					lineSperater1.setVisibility(View.VISIBLE);
					lineSperater2.setVisibility(View.GONE);
				}
			}
			break;
		case 3:
			if(CATEGORY1_IS_ENABLED){
				if(categoryLayout1.getVisibility() == View.VISIBLE){
					lineSperater1.setVisibility(View.VISIBLE);
					lineSperater3.setVisibility(View.GONE);
					lineSperater2.setVisibility(View.VISIBLE);
				}
			}
			break;
			default: break;
		}
	}
	
	/**
	 * 白色三角闪烁动画
	 * @param type
	 */
	@SuppressWarnings("unused")
	private void startTimer(int type){
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}

		version_alpha = 0;
		version_flag = 1;
		if (currentAnimationBtn != null) {
			currentAnimationBtn.setAlpha(version_alpha);
			currentAnimationBtn.invalidate();
			currentAnimationBtn.setVisibility(View.INVISIBLE);
		}

		ImageView buttonAnimations[] = new ImageView[] { normalAnimationBtn,
				categoryAnimationBtn, otherAnimationBtn };
		if(categoryLayout1.getVisibility() == View.VISIBLE){
			buttonAnimations = new ImageView[] { normalAnimationBtn,
					categoryAnimationBtn, categoryAnimationBtn1, otherAnimationBtn };
		}
		currentAnimationBtn = buttonAnimations[type];
		currentAnimationBtn.setVisibility(View.VISIBLE);

		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public synchronized void run() {
				if (version_flag == 1) {
					version_alpha = version_alpha - 51;
					Message msg = handler.obtainMessage(0, version_alpha);
					handler.sendMessage(msg);
					if (version_alpha == 0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						version_flag = 0;
					}
				} else {
					version_alpha = version_alpha + 51;
					Message msg = handler.obtainMessage(0, version_alpha);
					handler.sendMessage(msg);
					if (version_alpha == 255) {
						version_flag = 1;
					}
				}
			}
		};
		timer.schedule(timerTask, 1000, 100);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int alpha = (Integer) msg.obj;
			currentAnimationBtn.setAlpha(alpha);
			currentAnimationBtn.invalidate();
		};
	};

	@Override
	protected Parcelable onSaveInstanceState() {
		Log.i("simon", "保存状态");
		Parcelable superState = super.onSaveInstanceState();
		return new SavedState(superState, currentType);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Log.i("simon", "恢复状态");
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		changeUI(ss.getType());
	}

	private static class SavedState extends BaseSavedState {

		/**
		 * 保存当前类别
		 */
		private int mType;

		private SavedState(Parcelable superState, int type) {
			super(superState);
			mType = type;
		}

		private SavedState(Parcel in) {
			super(in);
			mType = in.readInt();
		}

		public int getType() {
			return mType;
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mType);
		}
	}

}
