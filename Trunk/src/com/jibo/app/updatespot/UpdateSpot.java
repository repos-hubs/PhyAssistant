package com.jibo.app.updatespot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import android.app.Activity;
import android.view.View;

import com.jibo.GBApplication;
import com.jibo.activity.HomePageActivity;
import com.jibo.base.src.request.AsycRequestHandler;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.DeviceInfo;
import com.jibo.data.UpdateParser;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.util.ActivityPool;
import com.jibo.util.Logs;

public abstract class UpdateSpot {
	public static class SoapOld extends SoapInfo {
		public SoapOld(String[] propertyKey, String[] propertyVal,
				int moduleid, String requestUrl) {
			super(propertyKey, propertyVal, -1, requestUrl, "");
			this.moduleid = moduleid;
			// TODO Auto-generated constructor stub
		}

		int moduleid;
	}

	public UpdateSpot() {
		super();
		// TODO Auto-generated constructor stub
	}

	AsycRequestHandler arh;
	Properties pi = new Properties();
	private SoapOld soapInfo;
	private Boolean cachedResult;

	public UpdateSpot(SoapOld soapInfo) {
		this.soapInfo = soapInfo;
		if (soapInfo.propertyKey.size() != soapInfo.propertyVal.size()) {
			for (int i = 0; i < soapInfo.propertyKey.size(); i++) {
				soapInfo.propertyInfo.put(soapInfo.propertyKey.get(i)
						.toString(), soapInfo.propertyVal.get(i).toString());
			}
		}
	}

	protected Object params;
	private Map<View, Boolean> view = new HashMap<View, Boolean>();
	public Map<View, Boolean> rtimeView = new HashMap<View, Boolean>();

	Map<Long, Boolean> maps = new HashMap<Long, Boolean>();
	Map asycResult = new HashMap();
	public Set<Class> modules = new HashSet<Class>();

	public Map<String, Long> getAsycResult() {
		return asycResult;
	}

	public void setAsycResult(Map<String, Long> asycResult) {
		this.asycResult = asycResult;
	}

	public void clear(Activity rtimeActivity) {
		for (View view : rtimeView.keySet()) {
			if (view == null)
				continue;
			if (rtimeActivity.findViewById(view.getId()) == null
					|| this.actyView.get(view) != rtimeActivity)
				continue;
			Logs.i("=== clear " + this);
			Logs.i("=== clear " + rtimeActivity);
			view.setVisibility(View.GONE);
			modules.add(rtimeActivity.getClass());
			rtimeView.remove(view);
			break;
		}
	}

	public void showspot(Activity rtimeActivity) {
		for (View view : rtimeView.keySet()) {
			if (view == null)
				continue;
			if (rtimeActivity.findViewById(view.getId()) == null
					|| this.actyView.get(view) != rtimeActivity)
				continue;
			if (rtimeView.get(view) != null && rtimeView.get(view)) {
				if (modules.contains(rtimeActivity.getClass())) {
					return;
				}
				if (rtimeActivity instanceof HomePageActivity) {
					HomePageActivity.updateBitmapStatus(ActivityPool
							.getInstance().activityMap
							.get(HomePageActivity.class), 99, 4);
				} else {
					Logs.i("=== show " + this);
					Logs.i("=== show " + rtimeActivity);
					view.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	}

	public Activity rtimeActivity;
	boolean fetchedNet;
	boolean fetching;
	private Map<View, Activity> actyView = new HashMap<View, Activity>();

	public void doSpot(final Activity rtimeActivity) {

		if (!fetchedNet) {
			if (!fetching) {
				fetching = true;
			} else {
				return;
			}
			fetchedNet = true;
			if(!DeviceInfo.instance.isNetWorkEnable()){
//				return;
			}
			GBApplication.gbapp.soapClient.sendRequest(soapInfo.requestUrl,
					soapInfo.moduleid, soapInfo.propertyInfo,
					new AsyncSoapResponseHandler() {

						@Override
						public void onSuccess(Object content) {
							// TODO Auto-generated method stub
							super.onSuccess(content);
							Map oj = (Map) ((UpdateParser) content).getResult();
							asycResult = new HashMap(oj);
							Logs.i("========================== asycResult "
									+ asycResult);
							update(rtimeActivity);
						}

					}, GBApplication.gbapp.getBaseContext());
		} else {
			update(rtimeActivity);
		}
	}

	public void update(Activity rtimeActivity) {
		if (cachedResult == null) {
			cachedResult = isupdateSpot();
		}
		if (cachedResult) {
			showspot(rtimeActivity);
		} else {
			clear(rtimeActivity);
		}
	}

	public abstract boolean isupdateSpot();

	public void setParams(Object... params) {
		this.params = params;
	}

	public void addSpotView(View view, Activity acty) {

		this.view.put(view, true);
		this.rtimeView.put(view, true);
		this.actyView.put(view, acty);
	}
}