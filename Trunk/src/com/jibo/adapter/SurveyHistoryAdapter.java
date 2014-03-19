package com.jibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.data.entity.SurveyHistoryEntity;

public class SurveyHistoryAdapter extends BaseAdapter {

	private ArrayList<SurveyHistoryEntity> historyList;
	private Context ctx;
	private LayoutInflater inflater;
	private ViewHolder holder;
	public SurveyHistoryAdapter(Context ctx, ArrayList<SurveyHistoryEntity> historyList) {
		this.ctx = ctx;
		this.historyList = historyList;
		this.inflater = LayoutInflater.from(ctx);
	}
	@Override
	public int getCount() {
		return historyList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return historyList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder {
		TextView txtTitle;
		ImageView imgStatus;
		TextView txtTime;
		TextView txtPay;
	}
	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.survey_history_item, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_surveyHistoryTitle);
			holder.imgStatus = (ImageView) convertView.findViewById(R.id.img_historyStatus);
			holder.txtTime = (TextView) convertView.findViewById(R.id.txt_surveyHistoryTime);
			holder.txtPay = (TextView) convertView.findViewById(R.id.txt_surveyHistoryPay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SurveyHistoryEntity en = historyList.get(arg0);
		
		holder.txtTitle.setText(en.getTitle());
		holder.txtTime.setText(en.getSubmitTime());
		holder.txtPay.setText(en.getPay());
		// 0:Î´»Ø´ð,1:´ýÉóºË,2:ÉóºËÖÐ,3:ÉóºËÍ¨¹ý´ýÖ§¸¶,4:ÉóºËÊ§°Ü,5:Ö§¸¶³É¹¦,Ä¬ÈÏÎª:0
		if("0".equals(en.getSurveyStatus())) {
		} else if("1".equals(en.getSurveyStatus())) {
			holder.imgStatus.setBackgroundResource(R.drawable.status_wait_audit);
		} else if("2".equals(en.getSurveyStatus())) {
			holder.imgStatus.setBackgroundResource(R.drawable.status_auditing);
		} else if("3".equals(en.getSurveyStatus())) {
			
		} else if("4".equals(en.getSurveyStatus())) {
			holder.imgStatus.setBackgroundResource(R.drawable.status_audit_fail);
		} else if("5".equals(en.getSurveyStatus())) {
			holder.imgStatus.setBackgroundResource(R.drawable.status_pay_success);
		} 
		
		return convertView;
	}

}
