package com.jibo.util.tips;

import com.jibo.activity.AccountManagerActivity;
import com.jibo.activity.DrugReferenceListActivity1;
import com.jibo.activity.NewDrugReferenceActivity;
import com.jibo.activity.NewSurveyActivity;
import com.jibo.activity.TabCalcList_NewActivity;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Mask extends View {
	private Activity context;
	private int touchFrequency = 1;

	public Mask(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context  = (Activity) context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(context instanceof AccountManagerActivity){
			return true;
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(context.getClass() == DrugReferenceListActivity1.class && touchFrequency == 1){
					TipHelper.disableTipViewOnScreenVisibility();
					touchFrequency = 2;
					TipHelper.registerTips((DrugReferenceListActivity1) context, 2);
					TipHelper.runSegments(context);
			}else if(context.getClass() == TabCalcList_NewActivity.class && touchFrequency == 1){
				TipHelper.disableTipViewOnScreenVisibility();
				touchFrequency = 2;
				TipHelper.registerTips((TabCalcList_NewActivity) context, 2);
				TipHelper.runSegments(context);
			}else if(context.getClass() == NewDrugReferenceActivity.class && touchFrequency == 1){
				TipHelper.disableTipViewOnScreenVisibility();
				touchFrequency = 2;
				TipHelper.registerTips((NewDrugReferenceActivity) context, 2);
				TipHelper.runSegments(context);
			}else if(context.getClass() == NewDrugReferenceActivity.class && touchFrequency == 2){
				TipHelper.disableTipViewOnScreenVisibility();
				touchFrequency = 3;
				TipHelper.registerTips((NewDrugReferenceActivity) context, 3);
				TipHelper.runSegments(context);
			}else if(context.getClass() == NewSurveyActivity.class && touchFrequency == 1){
				TipHelper.disableTipViewOnScreenVisibility();
				touchFrequency = 2;
				TipHelper.registerTips((NewSurveyActivity) context, 2);
				TipHelper.runSegments(context);
			}else if(context.getClass() == NewSurveyActivity.class && touchFrequency == 2){
				TipHelper.disableTipViewOnScreenVisibility();
				touchFrequency = 3;
				TipHelper.registerTips((NewSurveyActivity) context, 3);
				TipHelper.runSegments(context);
			}else{
				TipHelper.disableTipViewOnScreenVisibility();
			}
		}
		return true;
	}

}
