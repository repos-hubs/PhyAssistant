package com.jibo.activity;


import com.api.android.GBApp.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * 
 * @author Terry
 * @功能：给药品信息提供三个分类按钮，分别是：常用，分类，收藏
 *
 */
public class DrugInfoClassifyActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}
	/**
	 * @作用：视图绑定监听事件
	 * @param v 需要设置监听事件的视图
	 * 
	 */
	public void setOnClickListener(View v){
		Button but = (Button)v;
		but.setOnClickListener(new MyOnclickListener());
	}
	
	/**
	 * @描述：视图监听事件处理的实现类
	 * @author Terry
	 *
	 */
	private class MyOnclickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			DrugInfoClassifyActivity.this.dealOnClick(v);
		}
	}
	
	/**
	 * @作用：让子类重写，从而实现对事件的处理
	 * @param v 点击的视图
	 * 
	 */
	protected void dealOnClick(View v){}
	
	public Button getPopularButton() {
		return (Button)findViewById(R.id.popular);
	}

	public Button getSpecialButton() {
		return (Button)findViewById(R.id.specialtys);
	}

	public Button getFavoritButton() {
		return (Button)findViewById(R.id.favorites);
	}
}
