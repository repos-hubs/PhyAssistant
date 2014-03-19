package com.jibo.util.tips;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.ContactMufacturedetailActivity;
import com.jibo.activity.DrugReferenceListActivity1;
import com.jibo.activity.HomePageActivity;
import com.jibo.activity.MarketActivity;
import com.jibo.activity.NewDrugReferenceActivity;
import com.jibo.activity.NewSurveyActivity;
import com.jibo.activity.TabCalcInfoActivity2;
import com.jibo.activity.TabCalcList_NewActivity;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.news.NewsPageActivity;
import com.jibo.app.news.ProActivity;
import com.jibo.util.ActivityPool;
import com.jibo.util.SharedPreferenceUtil;

public class TipHelper {
	public static Set<View> screenViews = new HashSet<View>();
	public static Map<Class<? extends Activity>, TipTask> backup;
	public static Map<Class<? extends Activity>, TipTask> traceRecorder;
	public static Map<Class<? extends Activity>, TipTask> traceRecorderRecovery;
	public static final String sharedpreferenceFile = "tip";
	public static final String sharedpreferenceKey = "tipUsed";
	public static final String sharedpreferenceCtrlKey = "tipCtrlUsed";
	public static final String sharedpreferenceFirstUseKey = "newUserFirstUse";
	public static Boolean newUserUseFlag = false;
	public static Boolean oldUserUseFlag = false;
	public static Boolean tipAllowed = false;//手指触摸-开关
	public static Boolean tipUsed = false;//用过一次

	static {
		if (traceRecorderRecovery == null) {
			traceRecorderRecovery = new HashMap<Class<? extends Activity>, TipTask>();
		}
//		newUserUseFlag = (Boolean) SharedPreferenceUtil.getValue(
//				GBApplication.gbapp, sharedpreferenceFile,
//				sharedpreferenceFirstUseKey, Boolean.class);
//		newUserUseFlag = newUserUseFlag == null ? false : newUserUseFlag;
//
//		tipUsed = (Boolean) SharedPreferenceUtil.getValue(GBApplication.gbapp,
//				sharedpreferenceFile, sharedpreferenceKey, Boolean.class);
//		tipUsed = tipUsed == null ? false : tipUsed;
//		tipAllowed = (Boolean) SharedPreferenceUtil.getValue(
//				GBApplication.gbapp, sharedpreferenceFile,
//				sharedpreferenceCtrlKey, Boolean.class);
//		tipAllowed = tipAllowed == null ? false : tipAllowed;
	}

	public static Map<Class<? extends Activity>, TipTask> getTraceRecorder() {
		if (traceRecorder == null) {
			traceRecorder = new HashMap<Class<? extends Activity>, TipTask>();
		}
		return traceRecorder;
	}

	public static Map<Class<? extends Activity>, TipTask> getBackup() {
		if (traceRecorderRecovery == null) {
			traceRecorderRecovery = new HashMap<Class<? extends Activity>, TipTask>();
		}
		return traceRecorderRecovery;
	}
	/**
	 * 
	 * 把你要实现的tip代码写在这里。而后用runSegments实现一下运行即可
	 * @param activity
	 * @param touchFrequency
	 */
	public static void registerTips(final Activity activity, final int touchFrequency) {
		TipTask tipTask = null;

		if (activity.getClass() == HomePageActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					HomePageActivity hActivity = (HomePageActivity) activity;
					TipHelper.addTipByGeneration(R.id.imgbtn_home, 0, 0, hActivity);
					TipHelper.addViewToActivity(hActivity.imgVersion, 0, 0, hActivity);
					TipHelper.addTip(R.id.mask, hActivity);
					TipHelper.addTip(R.id.left, hActivity);
					TipHelper.addTip(R.id.right, hActivity);
					TipHelper.addTip(R.id.rippleView, hActivity);
					TipHelper.addTip(R.id.leftArrow, hActivity);
					TipHelper.addTip(R.id.closeTips, hActivity);
					TipHelper.addTip(R.id.rippleTips, hActivity);
				}
			};
		} else if (activity.getClass() == DrugReferenceListActivity1.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					DrugReferenceListActivity1 drugAcitivity = (DrugReferenceListActivity1) activity;
					if(touchFrequency == 1){
						TipHelper.addTip(R.id.mask, drugAcitivity);
						TipHelper.addTip(R.id.header_panel_mask, drugAcitivity);
						TipHelper.addTip(R.id.closeTips,  drugAcitivity);
						TipHelper.addTip(R.id.left,	 drugAcitivity);
					}else if(touchFrequency == 2){
						TipHelper.addTip(R.id.mask, drugAcitivity);
						TipHelper.addTipByGeneration(R.id.chooseCategory, 0, 0,  drugAcitivity);
						TipHelper.addTip(R.id.closeTips,  drugAcitivity);
						TipHelper.addTip(R.id.left_categoryname,  drugAcitivity);
						TipHelper.addTip(R.id.rippleView, drugAcitivity);
						TipHelper.addTip(R.id.leftArrow, drugAcitivity);
						TipHelper.addTip(R.id.rightArrow, drugAcitivity);
						
						TextView tv = (TextView) findView(R.id.chooseCategory, activity);
						TextView tv1 = (TextView) findView(R.id.left_categoryname, activity);
						int[] location = new int[2];
						tv.getLocationOnScreen(location);
						RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
						rl.setMargins(location[0], location[1] + 18, 0, 0);
						tv1.setLayoutParams(rl);
					}
				}
			};
		}else if (activity.getClass() == TabCalcList_NewActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					TabCalcList_NewActivity formulaAcitivity = (TabCalcList_NewActivity) activity;
					if(touchFrequency == 1){
						TipHelper.addTip(R.id.mask, formulaAcitivity);
						TipHelper.addTip(R.id.header_panel_mask, formulaAcitivity);
						TipHelper.addTip(R.id.closeTips,  formulaAcitivity);
						TipHelper.addTip(R.id.left,	 formulaAcitivity);
					}else if(touchFrequency == 2){
						TipHelper.addTip(R.id.mask, formulaAcitivity);
						TipHelper.addTipByGeneration(R.id.chooseCategory, 0, 0,  formulaAcitivity);
						TipHelper.addTip(R.id.closeTips,  formulaAcitivity);
						if(((TextView)findView(R.id.chooseCategory, formulaAcitivity)).getVisibility() != View.GONE){
							TipHelper.addTip(R.id.left_categoryname,  formulaAcitivity);
						}
						TipHelper.addTip(R.id.rippleView, formulaAcitivity);
						TipHelper.addTip(R.id.leftArrow, formulaAcitivity);
						TipHelper.addTip(R.id.rightArrow, formulaAcitivity);
						
						TextView tv = (TextView) findView(R.id.chooseCategory, activity);
						TextView tv1 = (TextView) findView(R.id.left_categoryname, activity);
						int[] location = new int[2];
						tv.getLocationOnScreen(location);
						RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
						rl.setMargins(location[0], location[1] + 18, 0, 0);
						tv1.setLayoutParams(rl);
					}
				}
			};
		}else if (activity.getClass() == ProActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					ProActivity newsAcitivity = (ProActivity) activity;
					TipHelper.addTip(R.id.mask, ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					TipHelper.addTipByGeneration(R.id.category, 0, 0,  newsAcitivity, ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					TipHelper.addTip(R.id.closeTips,  ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					TipHelper.addTip(R.id.left_categoryname,  ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					TipHelper.addTip(R.id.rippleView, ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					TipHelper.addTip(R.id.leftArrow, ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					TipHelper.addTip(R.id.rightArrow, ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));

					TextView tv = (TextView) findView(R.id.category, newsAcitivity);
					TextView tv1 = (TextView) findView(R.id.left_categoryname, ActivityPool.getInstance().activityMap.get(NewsPageActivity.class));
					int[] location = new int[2];
					tv.getLocationOnScreen(location);
					RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
					rl.setMargins(location[0], location[1] + 18, 0, 0);
					tv1.setLayoutParams(rl);
				}
			};
		}else if (activity.getClass() == NewDrugReferenceActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					NewDrugReferenceActivity drugReferenceActivity = (NewDrugReferenceActivity) activity;
					if(touchFrequency == 1){
						TipHelper.addTip(R.id.mask, drugReferenceActivity);
						TipHelper.addTipByGeneration(R.id.normal_text, 0, 0,  drugReferenceActivity);
						TipHelper.addTip(R.id.closeTips,  drugReferenceActivity);
						TipHelper.addTip(R.id.left_navigate_one,  drugReferenceActivity);

						TextView tv = (TextView) findView(R.id.normal_text, activity);
						TextView tv1 = (TextView) findView(R.id.left_navigate_one, activity);
						int[] location = new int[2];
						tv.getLocationOnScreen(location);
						RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
						rl.setMargins(location[0], location[1] + 18, 0, 0);
						tv1.setLayoutParams(rl);
					}else if(touchFrequency == 2){
						TipHelper.addTip(R.id.mask, drugReferenceActivity);
						TipHelper.addTipByGeneration(R.id.category_text, 0, 0,  drugReferenceActivity);
						TipHelper.addTip(R.id.closeTips,  drugReferenceActivity);
						TipHelper.addTip(R.id.left_navigate_two,  drugReferenceActivity);

						TextView tv = (TextView) findView(R.id.category_text, activity);
						TextView tv1 = (TextView) findView(R.id.left_navigate_two, activity);
						int[] location = new int[2];
						tv.getLocationOnScreen(location);
						RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
						rl.setMargins(location[0], location[1] + 18, 0, 0);
						tv1.setLayoutParams(rl);
					}else if(touchFrequency == 3){
						TipHelper.addTip(R.id.mask, drugReferenceActivity);
						TipHelper.addTipByGeneration(R.id.other_text, 0, 0,  drugReferenceActivity);
						TipHelper.addTip(R.id.closeTips,  drugReferenceActivity);
						TipHelper.addTip(R.id.right_navigate_three,  drugReferenceActivity);

						TextView tv = (TextView) findView(R.id.other_text, activity);
						TextView tv1 = (TextView) findView(R.id.right_navigate_three, activity);
						int[] location = new int[2];
						tv.getLocationOnScreen(location);
						RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
						rl.setMargins(location[0] - 200, location[1] + 18, 0, 0);
						tv1.setLayoutParams(rl);
					}
				}
			};
		} else if (activity.getClass() == NewsDetailActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					NewsDetailActivity hActivity = (NewsDetailActivity) activity;
					TipHelper.addTip(R.id.mask, hActivity);
					TipHelper.addTip(R.id.rippleView, hActivity);
					TipHelper.addTip(R.id.leftArrow, hActivity);
					TipHelper.addTip(R.id.rightArrow, hActivity);
					TipHelper.addTip(R.id.closeTips, hActivity);
					TipHelper.addTip(R.id.rippleTips, hActivity);
				}
			};
		}else if (activity.getClass() == ContactMufacturedetailActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					ContactMufacturedetailActivity contactActivity = (ContactMufacturedetailActivity) activity;
					TipHelper.addTip(R.id.mask, contactActivity);
					TipHelper.addTip(R.id.closeTips, contactActivity);
					TipHelper.addTip(R.id.rippleTips, contactActivity);
					TipHelper.addTipByGeneration(R.id.telsBodyLayout, 0, 0,  contactActivity);
					TipHelper.addTipByGeneration(R.id.emailBodyLayout, 0, 0,  contactActivity);
				
					LinearLayout layout = (LinearLayout) findView(R.id.telsBodyLayout, activity);
					TextView tv1 = (TextView) findView(R.id.rippleTips, activity);
					int[] location = new int[2];
					layout.getLocationOnScreen(location);
					RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
					rl.setMargins(location[0], location[1] - 200, 0, 0);
					tv1.setLayoutParams(rl);
				}
			};
		}else if (activity.getClass() == NewSurveyActivity.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					NewSurveyActivity newSurveyActivity = (NewSurveyActivity) activity;
					
					if(touchFrequency == 1){
						TipHelper.addTip(R.id.mask, newSurveyActivity);
						TipHelper.addTip(R.id.tip_item, newSurveyActivity);
						TipHelper.addTip(R.id.left, newSurveyActivity);
						TipHelper.addTip(R.id.right_question, newSurveyActivity);
						TipHelper.addTipByGeneration(R.id.btn_survey_intro, 0, 0, newSurveyActivity);
					}else if(touchFrequency == 2){
						TipHelper.addTip(R.id.mask, newSurveyActivity);
						TipHelper.addTip(R.id.tip_item, newSurveyActivity);
						TipHelper.addTip(R.id.left, newSurveyActivity);
						TipHelper.addTip(R.id.right_gathering, newSurveyActivity);
						TipHelper.addTip(R.id.closeTips, newSurveyActivity);
						TipHelper.addTipByGeneration(R.id.btn_survey_payment, 0, 0, newSurveyActivity);
					}else if(touchFrequency == 3){
						TipHelper.addTip(R.id.mask, newSurveyActivity);
						TipHelper.addTip(R.id.tip_item, newSurveyActivity);
						TipHelper.addTip(R.id.left, newSurveyActivity);
						TipHelper.addTip(R.id.bottom, newSurveyActivity);
						TipHelper.addTip(R.id.closeTips, newSurveyActivity);
						TipHelper.addTipByGeneration(R.id.btn_survey_mine, 0, 0, newSurveyActivity);
					}
				}
			};
		}else if (activity.getClass() == TabCalcInfoActivity2.class) {
			tipTask = new TipTask(activity) {
				public void run() {
					TabCalcInfoActivity2 calcInfoActivity = (TabCalcInfoActivity2) activity;
					
					TipHelper.addTip(R.id.mask, calcInfoActivity);
					TipHelper.addTip(R.id.si_tip, calcInfoActivity);
					TipHelper.addTip(R.id.closeTips, calcInfoActivity);
					TipHelper.addViewToActivity(calcInfoActivity.radioGroup, 0, 0, calcInfoActivity);
				
					RadioGroup rg = (RadioGroup) calcInfoActivity.radioGroup;
					TextView tv1 = (TextView) findView(R.id.si_tip, activity);
					int[] location = new int[2];
					rg.getLocationOnScreen(location);
					RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tv1.getLayoutParams();
					rl.setMargins(location[0], location[1]+30, 0, 0);
					tv1.setLayoutParams(rl);
				}
			};
		}else if (activity instanceof MarketActivity) {
			tipTask = new TipTask(activity) {
				public void run() {
					MarketActivity act = (MarketActivity) activity;
					TipHelper.addTip(R.id.mask, act);
					View v1 = TipHelper.addTipByGeneration(R.id.img_tab_mine, 0, 0, act);
					v1.setId(200);
					Button installBtn = null;
					if (act.lltMarketContent.getChildCount() > 0) {
						for (int i = 0; i < act.lltMarketContent
								.getChildCount(); i++) {
							if (act.lltMarketContent.getChildAt(i) instanceof LinearLayout) {
								installBtn = (Button) act.lltMarketContent
										.getChildAt(i).findViewById(100);
								break;
							}
						}
					}
					if (null != installBtn) {
						Display dis = activity.getWindow().getWindowManager()
								.getDefaultDisplay();
						View v2 = TipHelper.addTipByGeneration(installBtn, 0, 0, act);
						v2.setId(222);
						ImageView view = new ImageView(activity);
						view.setId(333);
						view.setBackgroundResource(R.drawable.arrow_top);
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						lp.addRule(RelativeLayout.BELOW,200); 
						lp.setMargins(0, 0, dis.getWidth()-installBtn.getLeft(), 0);
						lp.addRule(RelativeLayout.ALIGN_BOTTOM,222); 
						view.setLayoutParams(lp);
						view.setPadding(0, 0, 0, 5);
						getRootView(activity).addView(view);
						screenViews.add(view);
						
						TextView text = new TextView(activity);
						text.setBackgroundResource(R.drawable.left1);
						RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						lp1.addRule(RelativeLayout.BELOW,200); 
						lp1.addRule(RelativeLayout.LEFT_OF,333);
						lp1.setMargins(20, 0, 0, 0);
						text.setLayoutParams(lp1);
						text.setTextSize(18);
						text.setTextColor(act.getResources().getColor(R.color.darkgreen));
						text.setText(R.string.gossip_market_left);
						getRootView(activity).addView(text);
						screenViews.add(text);
					}
					TipHelper.addTip(R.id.closeTips, act);
				}
			};
		}
		if (traceRecorderRecovery.containsKey(activity.getClass()) && touchFrequency == 1) {
			return;
		}
		getTraceRecorder().put(activity.getClass(), tipTask);
	}
	//提示
	// 运行请使用这句话 TipHelper.runSegments(this);在需要运行的activity中
	public static void runSegments(Activity actvy) {
		if (!TipHelper.getTraceRecorder().containsKey(actvy.getClass())) {
			return;
		}
		@SuppressWarnings("unused")
		boolean launchOrNot;
		if (!(launchOrNot = toLaunchTheActivityTip(actvy)))
			return;
		TipHelper.getTraceRecorder().get(actvy.getClass()).run();
		if (TipHelper.traceRecorder.containsKey(actvy.getClass())) {
			traceRecorderRecovery.put(actvy.getClass(),
					TipHelper.traceRecorder.remove(actvy.getClass()));
			TipHelper.traceRecorder.put(actvy.getClass(), null);
		}
	}

	public static void sign(Boolean option, boolean record) {
		tipUsed = tipAllowed = option;
		if (record) {

			SharedPreferenceUtil.putValue(GBApplication.gbapp,
					TipHelper.sharedpreferenceFile,
					TipHelper.sharedpreferenceCtrlKey, TipHelper.tipAllowed);
			SharedPreferenceUtil.putValue(GBApplication.gbapp,
					TipHelper.sharedpreferenceFile,
					TipHelper.sharedpreferenceKey, TipHelper.tipUsed);
		}
	}

	public static boolean toLaunchTheActivityTip(Activity atvy) {
		boolean launchornot = TipHelper.tipAllowed != null
				&& TipHelper.tipAllowed && TipHelper.tipUsed != null
				&& TipHelper.tipUsed
				&& getTraceRecorder().get(atvy.getClass()) != null;

		return launchornot;
	}

	public static void disableTipViewOnScreenVisibility() {
		for (View view : screenViews) {
			int id = view.getId();
			if(id == R.id.leftArrow || id == R.id.rightArrow){
				view.clearAnimation();
			}
			view.setVisibility(View.GONE);
			// ((ViewGroup)view.getParent()).removeView(view);
		}
	}

	public static View findView(int id, Activity activity) {
		return activity.findViewById(id);
	}

	public static ViewGroup getRootView(Activity activity) {
		ViewGroup vg = ((ViewGroup) (activity.getWindow().getDecorView()));
		ViewGroup frameLayout = null;
		while (vg.getChildCount() == 1
				&& vg.getChildAt(0).getClass() != FrameLayout.class) {
			vg = (ViewGroup) vg.getChildAt(0);
		}
		for (int i = 0; i < vg.getChildCount(); i++) {
			if (vg.getChildAt(i) instanceof FrameLayout) {
				frameLayout = (FrameLayout) vg.getChildAt(i);
				break;
			}
		}
		if (frameLayout == null) {
			throw new IllegalStateException("state invalid");
		}
		return ((ViewGroup) frameLayout.getChildAt(0));
	}

	public static View addViewToActivity(View tipview, int offsetL,
			int offsetT, Activity activity) {
		final View newView = cloneView(tipview, activity);
		ViewGroup rootView = getRootView(activity);
		rootView.addView(newView);

		int[] location = new int[2];
		tipview.getLocationOnScreen(location);

		int location0 = location[0];
		int location1 = location[1];
		// 状态栏高度
		Rect frame = new Rect();  
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top; 
		
		if((activity.getClass() == NewDrugReferenceActivity.class 
				|| activity instanceof ProActivity)
				&& newView.getClass() == TextView.class){
		}else if(newView.getClass() == TextView.class
				|| newView instanceof RadioGroup
				|| newView.getClass() == ImageView.class && activity.getClass() == HomePageActivity.class
				|| newView.getClass() == Button.class && activity.getClass() == NewSurveyActivity.class
				|| newView.getClass() == LinearLayout.class && activity.getClass() == ContactMufacturedetailActivity.class){
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) newView.getLayoutParams();
			lp.setMargins(location0, location1-statusBarHeight, 0, 0);
			newView.setLayoutParams(lp);
		}
		if(activity instanceof  MarketActivity){
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(location0, location1-statusBarHeight, 0, 0);
			newView.setLayoutParams(lp);
			
			newView.measure(tipview.getWidth(), tipview.getHeight());
			newView.setMinimumWidth(tipview.getWidth());
			newView.setMinimumHeight(tipview.getHeight());
		}
		
//		newView.layout(location0, location1,
//				display.getWidth() - tipview.getWidth() - location0,
//				display.getHeight() - tipview.getHeight() - location1);
//		newView.measure(tipview.getWidth(), tipview.getHeight());
//		newView.setMinimumWidth(tipview.getWidth());
//		newView.setMinimumHeight(tipview.getHeight());
//		((ViewGroup) (activity.getWindow().getDecorView())).requestLayout();
//		newView.scrollBy(DipUtil.dip2px(activity, offsetL),
//				DipUtil.dip2px(activity, offsetT));
		screenViews.add(newView);
		return newView;
	}
//	public static void clearTip(int id, Activity activity) {
//		View view = findView(id, activity);
//		view.setVisibility(View.GONE);
//	}
	public static void addTip(int id, Activity activity) {
		View view = findView(id, activity);
		view.setVisibility(View.VISIBLE);
		
		// 箭头动画
		if(activity.getClass() == TabCalcList_NewActivity.class && (id == R.id.leftArrow || id == R.id.rightArrow)){
			view.setAnimation(MyAnimation.load_Animation((TabCalcList_NewActivity)activity, R.anim.arrow_alpha_action));
		}
		if(activity.getClass() == DrugReferenceListActivity1.class && (id == R.id.leftArrow || id == R.id.rightArrow)){
			view.setAnimation(MyAnimation.load_Animation((DrugReferenceListActivity1)activity, R.anim.arrow_alpha_action));
		}
		if(activity.getClass() == NewsPageActivity.class && (id == R.id.leftArrow || id == R.id.rightArrow)){
			view.setAnimation(MyAnimation.load_Animation((NewsPageActivity)ActivityPool.getInstance().activityMap.get(NewsPageActivity.class), R.anim.arrow_alpha_action));
		}
		if(activity.getClass() == NewsDetailActivity.class && (id == R.id.leftArrow || id == R.id.rightArrow)){
			view.setAnimation(MyAnimation.load_Animation((NewsDetailActivity)activity, R.anim.arrow_alpha_action));
		}
		
		TipHelper.screenViews.add(view);
	}

	public static View addTipByGeneration(int id, int offsetL, int offsetT,
			Activity activity) {
		return addViewToActivity(findView(id, activity), offsetL, offsetT,
				activity);

	}
	public static View addTipByGeneration(int id, int offsetL, int offsetT,
			Activity activity,Activity container) {
		return addViewToActivity(findView(id, activity), offsetL, offsetT,
				container);

	}
	public static View addTipByGeneration(View view, int offsetL, int offsetT,
			Activity activity) {
		return addViewToActivity(view, offsetL, offsetT, activity);

	}

	public static View cloneView(View oldview, Activity activity) {
		View view = null;
		if (oldview.getClass() == AutoCompleteTextView.class) {
			view = new AutoCompleteTextView(oldview.getContext());
		}
		if(oldview.getClass() == TextView.class){
			view = new TextView(oldview.getContext());
			TextView oldText = (TextView) oldview;
			((TextView) view).setText(oldText.getText());
			((TextView) view).setTextColor(oldText.getTextColors());
			((TextView) view).setGravity(oldText.getGravity());
			((TextView) view).setTextSize(DipUtil.px2sp(oldText.getTextSize(), DipUtil.getScale(activity)));
			((TextView) view).setCompoundDrawables(null, null, oldText.getCompoundDrawables()[2], null);
			((TextView) view).setLayoutParams(new RelativeLayout.LayoutParams(oldText.getWidth(), oldText.getHeight()));
			
			Rect frame = new Rect();  
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
			int statusBarHeight = frame.top; 
			switch(oldview.getId()){
				case R.id.normal_text:
					((TextView) view).setGravity(Gravity.CENTER);
					view.setBackgroundDrawable(findView(R.id.normal_layout, activity).getBackground());
					RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(findView(R.id.normal_layout, activity).getWidth(), findView(R.id.normal_layout, activity).getHeight());
					int[] location = new int[2];
					findView(R.id.normal_layout, activity).getLocationOnScreen(location);
					rl.setMargins(location[0], location[1]-statusBarHeight, 0, 0);
					view.setLayoutParams(rl);
					((TextView) view).setPadding(0, 0, 0, 10);
					break;
				case R.id.category_text:
					((TextView) view).setGravity(Gravity.CENTER);
					view.setBackgroundDrawable(findView(R.id.category_layout, activity).getBackground());
					RelativeLayout.LayoutParams rl1 = new RelativeLayout.LayoutParams(findView(R.id.category_layout, activity).getWidth(), findView(R.id.normal_layout, activity).getHeight());
					int[] location1 = new int[2];
					findView(R.id.category_layout, activity).getLocationOnScreen(location1);
					rl1.setMargins(location1[0], location1[1]-statusBarHeight, 0, 0);
					view.setLayoutParams(rl1);
					((TextView) view).setPadding(0, 0, 0, 10);
					break;
				case R.id.other_text:
					((TextView) view).setGravity(Gravity.CENTER);
					view.setBackgroundDrawable(findView(R.id.other_layout, activity).getBackground());
					RelativeLayout.LayoutParams rl2 = new RelativeLayout.LayoutParams(findView(R.id.other_layout, activity).getWidth(), findView(R.id.normal_layout, activity).getHeight());
					int[] location2 = new int[2];
					findView(R.id.other_layout, activity).getLocationOnScreen(location2);
					rl2.setMargins(location2[0] - 3, location2[1]-statusBarHeight, 0, 0);
					view.setLayoutParams(rl2);
					((TextView) view).setPadding(0, 0, 0, 10);
					break;
					default:break;
			}
		}
		if(oldview.getClass() == RelativeLayout.class){
			view = new RelativeLayout(oldview.getContext());
		}
		if(oldview.getId() == R.id.telsBodyLayout || oldview.getId() == R.id.emailBodyLayout){
			view = new LinearLayout(oldview.getContext());
			LinearLayout oldLayout = (LinearLayout) oldview;
			((LinearLayout)view).setGravity(Gravity.CENTER_VERTICAL);
			((LinearLayout)view).setOrientation(oldLayout.getOrientation());
			((LinearLayout)view).setLayoutParams(new LinearLayout.LayoutParams(oldLayout.getWidth(), oldLayout.getHeight()));
			((LinearLayout)view).setBackgroundColor(activity.getResources().getColor(R.color.white));
			ImageView iv = new ImageView(oldLayout.getChildAt(0).getContext());
			iv.setImageDrawable(((ImageView)oldLayout.getChildAt(0)).getDrawable());
			TextView tv = new TextView(oldLayout.getChildAt(1).getContext());
			tv.setTextSize(DipUtil.px2sp(((TextView)oldLayout.getChildAt(1)).getTextSize(), DipUtil.getScale(activity)));
			tv.setText(((TextView)oldLayout.getChildAt(1)).getText());
			tv.setTextColor(((TextView)oldLayout.getChildAt(1)).getTextColors());
			((LinearLayout)view).addView(iv);
			((LinearLayout)view).addView(tv);
		}
		if (oldview.getClass() == Button.class) {
			view = new Button(oldview.getContext());
			Button oldButton = (Button) oldview;
			((Button) view).setText(oldButton.getText());
			((Button) view).setTextSize(DipUtil.px2sp(oldButton.getTextSize(),
					DipUtil.getScale(activity)));
			((Button) view).setTextColor(oldButton.getTextColors());
			((Button) view).setLayoutParams(new RelativeLayout.LayoutParams(oldButton.getWidth(), oldButton.getHeight()));
		}
		
		if (oldview instanceof ImageView) {
			view = new ImageView(oldview.getContext());
			((ImageView) view).setImageDrawable(((ImageView) oldview)
					.getDrawable());
			if(activity.getClass() == HomePageActivity.class)
			((ImageView) view).setLayoutParams(new RelativeLayout.LayoutParams(oldview.getWidth(), oldview.getHeight()));
		}
		if (oldview instanceof ImageButton) {
			view = view == null ? new ImageButton(oldview.getContext()) : view;
		}
		
		if(oldview instanceof RadioGroup){
			view = new RadioGroup(oldview.getContext());
			((RadioGroup) view).setBackgroundColor(Color.WHITE);
			((RadioGroup) view).setOrientation(((RadioGroup) oldview).getOrientation());
			((RadioGroup) view).setLayoutParams(new RelativeLayout.LayoutParams(oldview.getWidth(), oldview.getHeight()));
			RadioButton rb1 = new RadioButton(((RadioGroup) oldview).getChildAt(0).getContext());
			rb1.setText(((RadioButton)((RadioGroup) oldview).getChildAt(0)).getText());
			rb1.setTextColor(((RadioButton)((RadioGroup) oldview).getChildAt(0)).getTextColors());
			rb1.setTextSize(DipUtil.px2sp(((RadioButton)((RadioGroup) oldview).getChildAt(0)).getTextSize(), DipUtil.getScale(activity)));
			RadioButton rb2 = new RadioButton(((RadioGroup) oldview).getChildAt(1).getContext());
			rb2.setText(((RadioButton)((RadioGroup) oldview).getChildAt(1)).getText());
			rb2.setTextColor(((RadioButton)((RadioGroup) oldview).getChildAt(1)).getTextColors());
			rb2.setTextSize(DipUtil.px2sp(((RadioButton)((RadioGroup) oldview).getChildAt(1)).getTextSize(), DipUtil.getScale(activity)));
			((RadioGroup) view).addView(rb1);
			((RadioGroup) view).addView(rb2);
			((RadioButton)((RadioGroup) view).getChildAt(0)).setChecked(true);
		}
		
		if(oldview.getBackground() != null && activity.getClass() != ContactMufacturedetailActivity.class){
			view.setBackgroundDrawable(oldview.getBackground());
		}
		return view;
	}

	public static Bitmap scaleImage(Bitmap bitmap, int dst_w, int dst_h) {
		int src_w = bitmap.getWidth();
		int src_h = bitmap.getHeight();
		float scale;
		if (dst_w != -1) {
			scale = ((float) dst_w) / src_w;
		} else {
			scale = ((float) dst_h) / src_h;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
				true);
		return dstbmp;
	}

}
