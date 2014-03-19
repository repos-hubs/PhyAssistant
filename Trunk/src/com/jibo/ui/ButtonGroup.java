package com.jibo.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.api.android.GBApp.R;

/**
 * ButtonGrop
 * @author will
 *
 */
public class ButtonGroup extends RelativeLayout implements OnClickListener{
	private Context context;
	private int count = 2;
	private LayoutInflater inflater;
	private Button btnLeft, btnRight, btnMiddle01;
	private ImageView imgMiddle01;
	private int selectBtnIndex = 0;
	
	public ButtonGroup(Context context,AttributeSet attrs){
		super(context, attrs);
		inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.buttongroup_layout, this, true);
		
		inits();
	}
	
	/**
	 * 
	 * @param context
	 * @param index 默认选中的
	 * @param count 按钮个数，最小为2
	 */
	public void setBtnCount(int count, int index) {
		if(count < 2)	count = 2;
		this.count = count;
		if(count > 2){
			btnMiddle01.setVisibility(View.VISIBLE);
			imgMiddle01.setVisibility(View.VISIBLE);
			btnMiddle01.setOnClickListener(this);
			switch (index) {
			case 0:
				onClick(btnLeft);
				break;
			case 1:
				onClick(btnMiddle01);
				break;
			case 2:
				onClick(btnRight);
				break;
			default:
				break;
			}
		}else{
			btnMiddle01.setVisibility(View.GONE);
			imgMiddle01.setVisibility(View.GONE);
			switch (index) {
			case 0:
				onClick(btnLeft);
				break;
			case 1:
				onClick(btnRight);
				break;
			default:
				break;
			}
		}
	}
	
	private void inits(){
		btnLeft = (ToogleButton) findViewById(R.id.btn_left);
		btnRight = (ToogleButton) findViewById(R.id.btn_right);
		btnMiddle01 = (ToogleButton) findViewById(R.id.btn_middle_01);
		imgMiddle01 = (ImageView) findViewById(R.id.btn_middle_01_line);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
	}
	
	/**
	 * 
	 * @param id btn的id
	 * @param text
	 */
	public void setText(int id, String text){
		switch (id) {
		case R.id.btn_left:
			btnLeft.setText(text);
			break;

		case R.id.btn_right:
			btnRight.setText(text);
			break;

		case R.id.btn_middle_01:
			btnMiddle01.setText(text);
			break;

		default:
			break;
		}
	}
	
	public int getSelectButtonIndex(){
		return selectBtnIndex;
	}

	@Override
	public void onClick(View v) {
		setBackGround(v);
		
		switch (v.getId()) {
		case R.id.btn_left:
			selectBtnIndex = 0;
			break;

		case R.id.btn_right:
			if(count > 2){
				selectBtnIndex = count - 1;
			}else{
				selectBtnIndex = 1;
			}
			break;

		case R.id.btn_middle_01:
			selectBtnIndex = 1;
			break;

		default:
			break;
		}
	}
	
	private void setBackGround(View v){
		switch (v.getId()) {
		case R.id.btn_left:
			btnLeft.setTextColor(Color.rgb(0x27, 0xA7, 0x9B));
			btnRight.setTextColor(Color.GRAY);
			if(btnMiddle01.getVisibility() == View.VISIBLE){
				btnMiddle01.setTextColor(Color.GRAY);
			}
			break;

		case R.id.btn_right:
			btnLeft.setTextColor(Color.GRAY);
			btnRight.setTextColor(Color.rgb(0x27, 0xA7, 0x9B));
			if(btnMiddle01.getVisibility() == View.VISIBLE){
				btnMiddle01.setTextColor(Color.GRAY);
			}
			break;

		case R.id.btn_middle_01:
			btnLeft.setTextColor(Color.GRAY);
			btnRight.setTextColor(Color.GRAY);
			if(btnMiddle01.getVisibility() == View.VISIBLE){
				btnMiddle01.setTextColor(Color.rgb(0x27, 0xA7, 0x9B));
			}
			break;

		default:
			break;
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
