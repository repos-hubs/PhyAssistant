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
 * @���ܣ���ҩƷ��Ϣ�ṩ�������ఴť���ֱ��ǣ����ã����࣬�ղ�
 *
 */
public class DrugInfoClassifyActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}
	/**
	 * @���ã���ͼ�󶨼����¼�
	 * @param v ��Ҫ���ü����¼�����ͼ
	 * 
	 */
	public void setOnClickListener(View v){
		Button but = (Button)v;
		but.setOnClickListener(new MyOnclickListener());
	}
	
	/**
	 * @��������ͼ�����¼������ʵ����
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
	 * @���ã���������д���Ӷ�ʵ�ֶ��¼��Ĵ���
	 * @param v �������ͼ
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
