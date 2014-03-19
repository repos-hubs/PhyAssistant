package com.jibo.app.news;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.app.ArticleActivity;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

public class CatActivity extends ArticleActivity {
	RequestController srcRequests;
	static String[] catsS = new String[] { "内科", "外科", "临床专科", "临床相关医学" };
	static List<String> cats = Arrays.asList(catsS);
	public View view;
	public String bigCategory;
	public boolean inited;
	protected View emptyView;

	@Override
	public void start() {
		MobclickAgent.onError(context);
		// TODO Auto-generated method stub

		if (!inited) {
			inited = true;
			emptyView = LayoutInflater.from(context).inflate(
					R.layout.empty_frame, null);
			((TextView) emptyView.findViewById(R.id.emptytext))
					.setText(getString(R.string.empty_result));
		}
		String dept = bigCategory;
		Logs.i("---" + dept);
		if (null != dept && !"".equals(dept)) {
			try {
				firstLaunch(new String[] { dept });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			super.start();
		}
	}

	public void sort(List<EntityObj> eob, Entry<DescInfo, Integer> entry) {
		if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_NEWS_CATEGORIES_BY_BIGCATEGROY) {
			Collections.<EntityObj> sort(eob, catCmr);
			if (ObjComparator.dept != null) {
				eob.remove(ObjComparator.dept);
				eob.add(0, ObjComparator.dept);
			}
		}
	}

	static Comparator<EntityObj> catCmr = new ObjComparator();

	public static class ObjComparator implements Comparator<EntityObj> {
		public static EntityObj dept;
		public String deptName = SharedPreferencesMgr.getDept();

		@Override
		public int compare(EntityObj object1, EntityObj object2) {
			// TODO Auto-generated method stub
			if (deptName != null && !deptName.equals("")) {
				setDeptName(object1);
				setDeptName(object2);
			}
			int c1 = cats.indexOf(object1.get("bigCategory").toString());
			c1 = c1 == -1 ? 100 : c1;
			int c2 = cats.indexOf(object2.get("bigCategory").toString());
			c2 = c2 == -1 ? 100 : c2;
			int comp = c1 - c2;
			if (comp == 0) {
				return -(Integer.parseInt(object1.get("newsCount").toString()) - Integer
						.parseInt(object2.get("newsCount").toString()));
			} else {
				return comp;
			}
		}

		private void setDeptName(EntityObj object2) {
			if (object2.get("name").equals(deptName)) {
				dept = object2;
			}
		}

	}

	public void firstLaunch(String[] cat) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = LayoutInflater.from(context).inflate(R.layout.detail_list, null);
		view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		this.setContentView(view);
		Object intentBigCat = this.getIntent().getExtras().get("bigCategory");
		if (intentBigCat == null || intentBigCat.toString().equals("")) {
			throw new IllegalStateException("param error: empty bigCategory");
		}
		this.bigCategory = intentBigCat.toString();
	}

	@Override
	public void onBack(Boolean stayTop, boolean isBackKey) {
		// TODO Auto-generated method stub

		srcRequests.onBack(stayTop);
	}

}
