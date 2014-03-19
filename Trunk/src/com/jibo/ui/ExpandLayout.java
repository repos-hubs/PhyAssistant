package com.jibo.ui;

import com.api.android.GBApp.R;
import com.jibo.ui.EllipsizeText.EllipsizeListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * 文字可伸展收缩自定义布局 
 * @author simon
 *
 */
public class ExpandLayout extends RelativeLayout implements OnClickListener,
		EllipsizeListener {

	private Context mContext;
	private LayoutInflater inflater;

	private EllipsizeText mText;
	private ImageView mBtn;

	/** 最大行数 */
	private int maxLines;
	/** 默认最大行数 */
	private int maxLinesDef = 2;
	/**按钮背景图片*/
	private int expandImgId = -1;
	private int shrinkImgId = -1;
	/**显示字体属性*/
	private float textSize;
	private int textColor;
	/**当前是否为收缩状态(待伸展)*/
	private boolean ellipsized;

	public ExpandLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.expand_layout, this, true);
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.Expand);
		maxLines = a.getInt(R.styleable.Expand_maxLines, maxLinesDef);
		expandImgId = a.getInt(R.styleable.Expand_expandImg, R.drawable.down);
		shrinkImgId = a.getInt(R.styleable.Expand_shrinkImg, R.drawable.up);
		textSize = a.getDimension(R.styleable.Expand_textsize, 15);
		textColor = a.getColor(R.styleable.Expand_textcolor, R.color.black);
		inits();
		a.recycle();
	}

	/***
	 * 初始化UI
	 */
	private void inits() {
		mText = (EllipsizeText) this.findViewById(R.id.expand_text);
		mBtn = (ImageView) this.findViewById(R.id.expand_btn);
//		mText.setTextSize(textSize);
//		mText.setTextColor(textColor);
		mText.setMaxLines(maxLines);
		mText.setEllipsizeLinstener(this);
		mText.setOnClickListener(this);
		mBtn.setOnClickListener(this);
	}
	
	public void setText(String text){
		mText.setText(text);
	}
	

	@Override
	public void onClick(final View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		switch (v.getId()) {
		case R.id.expand_text:
		case R.id.expand_btn:
			mText.changeShowText(ellipsized);
			break;
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Log.i("simon", "保存数据");
		return super.onSaveInstanceState();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Log.i("simon", "获取数据");
		super.onRestoreInstanceState(state);
	}

	@Override
	public void ellipsizeStateChanged(boolean ellipsized,
			boolean isShowExpandBtn) {
		if (isShowExpandBtn) {
			mBtn.setVisibility(View.VISIBLE);
			this.ellipsized = ellipsized;
			if (ellipsized) {
				mBtn.setImageResource(expandImgId);
			} else {
				mBtn.setImageResource(shrinkImgId);
			}
		}else{
			mBtn.setVisibility(View.GONE);
		}
	}

}
