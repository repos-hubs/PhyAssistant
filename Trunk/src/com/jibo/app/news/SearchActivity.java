package com.jibo.app.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.AdapterView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.app.GBIRequest;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.Constant;
import com.jibo.common.SoapRes;
import com.jibo.util.Logs;

public class SearchActivity extends AllActivity {

	private RequestController srcRequests;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String searchvalue = this.getIntent().getStringExtra(
				BaseSearchActivity.SEARCH_TEXT);
		toDl(searchvalue);
		setPopupWindowType(Constant.MENU_TYPE_1);
		
	}

	public void toDl(String searchvalue) {
		RequestInfos soaps = new RequestInfos();
		List<ScrollCounter> count = new ArrayList<ScrollCounter>();
		count.add(new ScrollCounter(20, 1));
		count.add(new ScrollCounter(10, -1));
		soaps.putSrc(new SoapInfo(new String[] { SoapRes.KEY_SINCE_ID,
				SoapRes.KEY_MAX_ID, SoapRes.KEY_COUNT,
				SoapRes.KEY_NEWS_CATEGORY, SoapRes.KEY_SEARCHVALUE },
				new String[] { "", "", "", "", searchvalue },
				SoapRes.REQ_ID_GET_IMAGELIST, SoapRes.URLIMAGENews, count,
				"detail"), 1);
		RequestSrc dtl = new GBIRequest(soaps, 0, -1, false, this, 1,"") {
			boolean shouldReplace = false;

			@Override
			public boolean shouldInstead(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST_TOP) {
					if (eob.isEmpty()) {
						return shouldReplace = false;
					}
					shouldReplace = true;
					return true;
				} else if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST) {
					if (shouldReplace) {
						return false;
					}
					Logs.i("--- info.propertyInfo cached i " + cached);
					if (cached) {// ×ªÒÆÌæ»»ÈÎÎñ
						shouldReplace = false;
						cached = false;
						return true;
					}
					return shouldReplace = false;
				}
				return false;

			}

			boolean shouldCache;

			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST) {
					if (!shouldCache) {
						shouldCache = true;
						// return false;
						return true;
					}
					return false;
				}
				return false;
			}
		};
		dtl.setToCache(false);
		dtl.setItemClickListener(detailClickListener);
		srcRequests = new RequestController( view, this);
		srcRequests.addRequest(dtl);
		srcRequests.startDl();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			onBack(null,false);
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onBack(Boolean stayTop,boolean isBackKey) {
		// TODO Auto-generated method stub
		srcRequests.onBack(stayTop);
	}
	
	
}
