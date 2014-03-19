package com.jibo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.data.entity.SurveyEntity;
import com.jibo.dbhelper.SurveyAdapter;
import com.jibo.dbhelper.SurveyQuestionAdapter;

public class SurveyListAdapter extends BaseAdapter {

	private Context ctx;
	private int s = 40;

	private LayoutInflater inflater;
	private ViewHolder holder;
	private ArrayList<SurveyEntity> surveyList;
	private SurveyQuestionAdapter questionAdapter;
	private SurveyAdapter surveyAdapter;
	private String userName;
	public SurveyListAdapter(Context ctx, ArrayList<SurveyEntity> aaa, String userName) {
		this.ctx = ctx;
		this.userName = userName;
		questionAdapter = new SurveyQuestionAdapter(ctx, 1);
		inflater = LayoutInflater.from(ctx);
		surveyList = aaa;
		surveyAdapter = new SurveyAdapter(ctx, 1);
	}

	@Override
	public int getCount() {
		return surveyList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return surveyList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder {
		ProgressBar pbProgress;
		TextView txtTitle;
		TextView txtTime;
		TextView txtReward;
		TextView txtPerson;
		ImageView img;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.survey_list_item, null);
			holder = new ViewHolder();
			holder.pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_survey);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_survey_title);
			holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
			holder.txtReward = (TextView) convertView.findViewById(R.id.txt_reward);
			holder.txtPerson = (TextView) convertView.findViewById(R.id.txt_person);
			holder.img = (ImageView) convertView.findViewById(R.id.img_survey_new_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SurveyEntity en = surveyList.get(position);
		HashMap<Integer, Integer> map = questionAdapter.getAnswerCount(en.getID(), "1", userName);
		holder.txtTitle.setText(en.getKeyWords());
		holder.txtTime.setText(en.getEstimateTime());
		holder.txtReward.setText(en.getPay());
		holder.txtPerson.setText(en.getpCount());
		for(Entry e:map.entrySet()) {
			int current = Integer.parseInt(e.getKey().toString());
			int total = Integer.parseInt(e.getValue().toString());
			System.out.println("current    "+current);
			System.out.println("total     "+total);
			if(total!=0) {
				holder.pbProgress.setProgress(current*100/total);
			} else {
				holder.pbProgress.setProgress(0);
			}
		}
		String status = surveyAdapter.getStatus(en.getID(), userName);
		if(!"1".equals(status)) {
			holder.img.setBackgroundResource(R.drawable.survey_new_icon);
		}
		return convertView;
	}

}
