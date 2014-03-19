package com.jibo.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.DrugAlertsDetailActivity;
import com.jibo.activity.ECGArticleActivity;
import com.jibo.activity.HistoryFavoriteActivity;
import com.jibo.activity.NewDrugReferenceActivity;
import com.jibo.activity.RelatedArticlesActivity;
import com.jibo.activity.RelatedEventsActivity;
import com.jibo.activity.TabCalcInfoActivity2;
import com.jibo.activity.TumorArticleActivity1;
import com.jibo.app.DetailsData;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.base.src.EntityObj;
import com.jibo.common.Constant;
import com.jibo.data.entity.PromotionBrandInfoEntity;
import com.jibo.data.entity.ViewHistoryEntity;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.util.Logs;

public class ViewHistoryAdapter extends BaseAdapter {

	Context mContext;
	int mCategory;
	ArrayList<ViewHistoryEntity> mList;
	int mMonth;
	int mDate;
	int mYear;
	Calendar mCalendar;
	private int mAction;
	private Object mObj = new Object();
	private long time;
	ArrayList<ViewHistoryEntity> finalLst;

	ForegroundColorSpan fgSpan;

	public final int VIEW_ACTION_TODAY = 0;
	public final int VIEW_ACTION_YESTERDAY = 1;
	public final int VIEW_ACTION_WEEK = 2;
	public final int VIEW_ACTION_MONTH = 3;
	public final int VIEW_ACTION_OLDER = 4;

	private HistoryAdapter historyAdapter;

	public ViewHistoryAdapter(Context context,
			ArrayList<ViewHistoryEntity> lst, int action) {

		synchronized (mObj) {
			mContext = context;
			this.mAction = action;
			// historyAdapter = new HistoryAdapter(mContext, 1);
			time = System.currentTimeMillis();
			mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(time);
			mYear = mCalendar.get(Calendar.YEAR);
			mMonth = mCalendar.get(Calendar.MONTH) + 1;
			mDate = mCalendar.get(Calendar.DATE);

			fgSpan = new ForegroundColorSpan(mContext.getResources().getColor(
					R.color.gray));
		}

		synchronized (mObj) {
			finalLst = new ArrayList<ViewHistoryEntity>();
			for (ViewHistoryEntity bean : requestLst(lst)) {
				removeDuplicate(bean);
			}
		}

		synchronized (mObj) {
			mList = finalLst;
			System.out.println("mList   " + mList.size());
		}

	}

	public ArrayList<ViewHistoryEntity> getmList() {
		return mList;
	}

	public void setmList(ArrayList<ViewHistoryEntity> mList) {
	}

	/**
	 * @description 根据当前日期获取列表
	 * @param ArrayList
	 *            <ViewHistoryEntity> lst 数据总列表
	 * @return ArrayList<ViewHistoryEntity> 列表
	 */
	public ArrayList<ViewHistoryEntity> requestLst(
			ArrayList<ViewHistoryEntity> lst) {
		ArrayList<ViewHistoryEntity> resultList = new ArrayList<ViewHistoryEntity>();
		String currentTime = mYear + "-" + mMonth + "-" + mDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date savedDate;
		Date currentDate;
		Calendar savedCalendar = Calendar.getInstance();
		Calendar currentCalendar = Calendar.getInstance();
		switch (mAction) {
		case VIEW_ACTION_TODAY:
			for (ViewHistoryEntity historyBean : lst) {
				if (historyBean.getDate() == mDate
						&& historyBean.getMonth() == mMonth) {
					resultList.add(historyBean);
				}
			}
			break;
		case VIEW_ACTION_YESTERDAY:
			for (ViewHistoryEntity historyBean : lst) {
				if ((historyBean.getDate() == mDate - 1 && historyBean
						.getMonth() == mMonth)) {
					resultList.add(historyBean);
				}
			}
			break;
		case VIEW_ACTION_WEEK:
			for (ViewHistoryEntity historyBean : lst) {
				String savedTime = historyBean.getYear() + "-"
						+ historyBean.getMonth() + "-" + historyBean.getDate();
				try {
					savedDate = format.parse(savedTime);
					currentDate = format.parse(currentTime);
					savedCalendar.setTime(savedDate);
					currentCalendar.setTime(currentDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (savedCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar
						.get(Calendar.WEEK_OF_YEAR)) {
					resultList.add(historyBean);
				}
			}
			break;
		case VIEW_ACTION_MONTH:
			for (ViewHistoryEntity historyBean : lst) {
				if (historyBean.getMonth() == mMonth) {
					resultList.add(historyBean);
				}
			}
			break;
		case VIEW_ACTION_OLDER:
			for (ViewHistoryEntity historyBean : lst) {
				if (historyBean.getMonth() != mMonth) {
					resultList.add(historyBean);
				}
			}
			break;
		}
		return resultList;
	}

	/**
	 * @description 删除重复
	 * @param ViewHistoryEntity
	 *            resultBean 历史对象
	 * @return ArrayList<ViewHistoryEntity> 去除后的列表
	 */
	public ArrayList<ViewHistoryEntity> removeDuplicate(
			ViewHistoryEntity resultBean) {
		int duplicateFlag = 0;
		if (finalLst.size() == 0) {
			finalLst.add(resultBean);
		} else {
			for (int i = 0; i < finalLst.size(); i++) {
				if (resultBean.getvId() == finalLst.get(i).getvId()
						&& resultBean.getColId() == finalLst.get(i).getColId()
						&& resultBean.getField1() == finalLst.get(i)
								.getField1()) {
					try {
						if (resultBean.getContent()!=null&&resultBean.getContent().equals(
								finalLst.get(i).getContent())) {
							duplicateFlag = 1;
							break;
						}
					} catch (Exception e) {
						Logs.i("resultBean "+resultBean);						
						Logs.i("finalLst.get(i) "+finalLst.get(i));
						Logs.i("resultBean .getContent() "+resultBean.getContent());
						Logs.i("finalLst.get(i) .getContent() " +
								""+finalLst.get(i).getContent());
					}
				}
			}
			if (duplicateFlag == 0) {
				if (!"".equals(resultBean.getContent())) {
					finalLst.add(resultBean);
				}
			}
		}

		return finalLst;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		arg1 = inflater.inflate(R.layout.view_history_item, null);
		ImageView imgViewHistory = (ImageView) arg1
				.findViewById(R.id.img_view_history_item);
		TextView txtViewHistory = (TextView) arg1
				.findViewById(R.id.txt_view_history_item);
		switch (mList.get(arg0).getColId()) {
		case Constant.CALC_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_calculator);
			break;
		case Constant.DRUG_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_drug);
			break;
		case Constant.NEWS_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_news);
			break;
		case Constant.EVENTS_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_event);
			break;
		case Constant.ECG_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_tool);
			break;
		case Constant.RESEARCH_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_research);
			break;
		case Constant.TNM_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_tool);
			break;
		case Constant.DRUG_ALERT_COLID:
			imgViewHistory.setBackgroundResource(R.drawable.history_drug_alert);
			break;
		}

		arg1.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_MOVE) {
					v.setBackgroundColor(Color.GREEN);
				} else {
					v.setBackgroundColor(0);
				}
				return false;
			}
		});

		arg1.setOnClickListener(new View.OnClickListener() {
			private char[] bid;
			private PromotionBrandInfoEntity brandEntity;

			@Override
			public void onClick(View v) {
				try {
					Intent intent = null;
					// 类型id
					switch (mList.get(arg0).getColId()) {
					case Constant.CALC_COLID:
						Intent intent1 = new Intent(mContext,
								TabCalcInfoActivity2.class);
						intent1.putExtra("from", "history");
						intent1.putExtra("id", mList.get(arg0).getvId());
						mContext.startActivity(intent1);
						break;
					case Constant.DRUG_COLID:
						String drugId = mList.get(arg0).getvId();
						String adminRouteId = mList.get(arg0).getField1();
						// int drugFlag =
						// Integer.parseInt(mList.get(arg0).getContent().substring(mList.get(arg0).getContent().length()-1));
						System.out.println("drugId    " + drugId);
						String drugFlag = mList.get(arg0).getContent()
								.split("-")[0];
						// if("A".equals(drugFlag)) {
						// DrugInfoEntity en =
						// drugAdapter.FillDrugsInfoDataByLocalDatabase(String.valueOf(drugId));
						// intent = new Intent(mContext,
						// DrugReferenceActivity.class);
						// intent.putExtra("MAINPAGE", true);
						// intent.putExtra("drugInfo", en);
						// } else if("B".equals(drugFlag)) {
						// intent = new Intent(mContext,
						// DrugSpecActivity.class);
						// intent.putExtra("brandId", String.valueOf(drugId));
						// } else
						if (drugFlag.startsWith("C")) {
							int mod = Integer.parseInt(drugFlag.substring(1));
							intent = new Intent(mContext,
									NewDrugReferenceActivity.class);
							intent.putExtra("mode", mod);
							if (mod == 2) {// 厂商说明书模式进入
								intent.putExtra("brandId",
										String.valueOf(drugId));
							} else if (mod == 0) {// 普通模式进入
								intent.putExtra("drugId",
										String.valueOf(drugId));
								intent.putExtra("adminRouteId", adminRouteId);
							}

						}
						//
						mContext.startActivity(intent);
						break;
					case Constant.NEWS_COLID:
						intent = new Intent(mContext, NewsDetailActivity.class);
						if (mList.size() == objs.size()) {
							DetailsData.entities = objs;
						} else {
							DetailsData.entities = convert(mList);
						}
						DetailsData.tappedne = DetailsData.entities.get(arg0);
						DetailsData.fetchDetailOnSoap(mList.get(arg0).getvId()
								+ "", (BaseActivity) mContext,DetailsData.entities.get(arg0));
//						mContext.startActivity(intent);
						break;
					case Constant.EVENTS_COLID:
						intent = new Intent(mContext,
								RelatedEventsActivity.class);
						intent.putExtra(Constant.ID, mList.get(arg0).getvId()
								+ "");
						mContext.startActivity(intent);
						break;
					case Constant.RESEARCH_COLID:
//						intent = new Intent(mContext,
//								RelatedArticlesActivity.class);
//						intent.putExtra(Constant.ID, mList.get(arg0).getvId());
//						mContext.startActivity(intent);
						
//						if (mList.size() == objs.size()) {
//							DetailsData.entities = objs;
//						} else {
//							DetailsData.entities = convertPaper(mList);
//						}
//						DetailsData.tappedne = DetailsData.entities.get(arg0);
//						intent = new Intent(mContext, PaperDetailActivity.class);
//						intent.putExtra("id",mList.get(arg0).getvId());
//						mContext.startActivity(intent);
						break;
					case Constant.ECG_COLID:
						intent = new Intent(mContext, ECGArticleActivity.class);
						intent.putExtra("id", mList.get(arg0).getvId());
						mContext.startActivity(intent);
						break;
					case Constant.TNM_COLID:
						intent = new Intent(mContext,
								TumorArticleActivity1.class);
						intent.putExtra("rank", "CNC"
								+ mList.get(arg0).getvId());
						intent.putExtra("rankTitle", mList.get(arg0)
								.getContent());
						mContext.startActivity(intent);
						break;
					case Constant.DRUG_ALERT_COLID:
						intent = new Intent(mContext,
								DrugAlertsDetailActivity.class);
						String str[] = mList.get(arg0).getContent()
								.split(mContext.getString(R.string.str_split));
						intent.putExtra("typeID", str[1]);
						intent.putExtra("title", str[0]);
						mContext.startActivity(intent);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		txtViewHistory.setTextColor(Color.BLACK);
		CharSequence contentFinal = "";
		if (Constant.DRUG_COLID == mList.get(arg0).getColId()) {
			ViewHistoryEntity info = mList.get(arg0);
			String content = info.getContent();
			String adminRouteContent = info.getField2();
			if (content.startsWith("A-") || content.startsWith("B-")) {
				contentFinal = content.substring(2);
			} else if (content.matches("^C\\d-.*")) {
				contentFinal = content.substring(3);
			}

			if (!TextUtils.isEmpty(adminRouteContent)) {
				adminRouteContent = "[" + adminRouteContent + "]";
				String splitString = "  ";
				String text = contentFinal + splitString + adminRouteContent;

				SpannableString spannable = new SpannableString(text);
				spannable.setSpan(fgSpan, text.indexOf(splitString)
						+ splitString.length(), text.indexOf(splitString)
						+ splitString.length() + adminRouteContent.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				contentFinal = spannable;
			}

		} else {
			contentFinal = mList.get(arg0).getContent();
		}
		System.out.println(mList.get(arg0).getColId() + "     " + contentFinal);
		txtViewHistory.setText(contentFinal);
		return arg1;
	}

	protected List<EntityObj> convert(ArrayList<ViewHistoryEntity> mList2) {
		List<EntityObj> eojs = new ArrayList<EntityObj>();
		for (int i = 0;i<mList2.size();i++) {
			ViewHistoryEntity vh = mList2.get(i);
			EntityObj eoj = new EntityObj();
			eoj.fieldContents.put("id", vh.getvId() + "");

			eojs.add(eoj);
		}
		return eojs;
	}
	
	protected List<EntityObj> convertPaper(ArrayList<ViewHistoryEntity> mList2) {
		List<EntityObj> eojs = new ArrayList<EntityObj>();
		for (int i = 0;i<mList2.size();i++) {
			ViewHistoryEntity vh = mList2.get(i);
			EntityObj eoj = new EntityObj();
			eoj.fieldContents.put("Id", vh.getvId() + "");

			eojs.add(eoj);
		}
		return eojs;
	}

	List<EntityObj> objs = new ArrayList<EntityObj>();

}
