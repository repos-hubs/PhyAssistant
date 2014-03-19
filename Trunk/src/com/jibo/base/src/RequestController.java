package com.jibo.base.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.util.Logs;
import com.jibo.v4.pagerui.PageActivity;

public class RequestController {
	public String tag;
	public boolean dynActivity;
	public Map<RequestSrc,Activity> atymap = new HashMap<RequestSrc,Activity>(); 
	public Stack<View> viewsystem = new Stack<View>();
	public View emptyView;

	public View getEmptyView() {
		return emptyView;
	}

	public final static int RECUR_NO_OPENED = -1;
	public final static int RECUR_DISABLED = -2;
	public boolean recur_enabled = true;
	public int recurIdx = -1;

	public void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
	}

	public List<RequestSrc> rts = new ArrayList<RequestSrc>(0);
	public View view;
	Activity activity;
	private LinkRequestItemClicker linkRequestItemClicker;
	public RequestSrc curRequestSrc;
	public Object clickitemTp;

	public void removeRequest(RequestSrc requestSrc) {
		if (requestSrc.isRecursiveBool() && !requestSrc.recursiveHead) {
			setBackwardRelation(requestSrc);
			this.rts.remove(requestSrc);
		}
	}

	public void addRequest(int index, RequestSrc insertedRequestSrc) {
		insertedRequestSrc.layerIndex = index;
		this.rts.add(index, insertedRequestSrc);
		setBackwardRelation(insertedRequestSrc);
		int nextIndex = -1;
		if ((nextIndex = rts.lastIndexOf(insertedRequestSrc)) != rts.size() - 1) {
			setBackwardRelation(rts.get(nextIndex + 1));
		} else {
			if (insertedRequestSrc.getLinkRequestItemClicker() == null) {
				insertedRequestSrc
						.setLinkRequestItemClicker(new LinkRequestItemClicker(
								this));
			}
			insertedRequestSrc.getLinkRequestItemClicker().setRequestSrc(
					insertedRequestSrc);
			insertedRequestSrc.getLinkRequestItemClicker().setNextRequestSrc(
					null);
		}
	}

	public void setBackwardRelation(RequestSrc insertedRequestSrc) {
		RequestSrc tmp;
		RequestSrc prevRequestSrc = this.getPrevSrcRequest(insertedRequestSrc);
		if (prevRequestSrc == null) {// 第一项
			tmp = insertedRequestSrc;
			tmp.setLinkRequestItemClicker(new LinkRequestItemClicker(this));
			tmp.getLinkRequestItemClicker().setRequestSrc(null);
		} else {
			tmp = prevRequestSrc;// 后面一项
			Object clicked = null;
			if (tmp.getLinkRequestItemClicker() == null) {
				tmp.setLinkRequestItemClicker(new LinkRequestItemClicker(this));
			}
			tmp.getLinkRequestItemClicker().setRequestSrc(tmp);
		}
		tmp.getLinkRequestItemClicker().setNextRequestSrc(insertedRequestSrc);
	}

	public RequestSrc getCurRequestSrc() {
		return curRequestSrc;
	}

	public void setCurRequestSrc(RequestSrc curRequestSrc) {
		this.curRequestSrc = curRequestSrc;
	}

	public void startCat(String[] pnValue, int i, String label) {
		try {
			if (label != null) {
				rts.get(i).getNavigationNode().setCurrLyLabel(label);
			}
			rts.get(i).setEntrance(pnValue);
			rts.get(i).enterSrc(rts.get(i), this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startCat(String[] pnValue) {
		startCat(pnValue, 0, null);
	}

	public void startFloorActivity(Context context) {
		this.curRequestSrc = rts.get(0);
		context.startActivity(new Intent(context, FloorActivity.class));
	}

	public void startDl() {
		try {
			rts.get(0).enterSrc(rts.get(0), this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(PageActivity activity) {
		this.activity = activity;
	}

	public RequestController(View view, Activity activity) {
		super();
		this.view = view;
		this.activity = activity;
	}

	public LinkRequestItemClicker getLinkRequestItemClicker() {
		return linkRequestItemClicker;
	}

	public void setLinkRequestItemClicker(
			LinkRequestItemClicker linkRequestItemClicker) {
		this.linkRequestItemClicker = linkRequestItemClicker;
	}

	public RequestSrc getPrevSrcRequest(RequestSrc srcRequest) {
		int i = rts.indexOf(srcRequest);
		if (i <= 0) {
			return null;
		}
		Logs.i("--- rts.prev src " + rts.get(i - 1));
		return rts.get(i - 1);
	}

	public RequestSrc getAvbPrevSrcRequest(RequestSrc srcRequest) {
		int i = rts.indexOf(srcRequest);
		if (i <= 0) {
			return srcRequest;
		}
		return rts.get(i - 1);
	}

	public RequestSrc getNextSrcRequest(RequestSrc srcRequest) {
		int i = rts.indexOf(srcRequest);
		if (i == rts.size() - 1) {
			return null;
		}
		return rts.get(i + 1);
	}

	public List<RequestSrc> getRts() {
		return rts;
	}

	public void clear() {
		getRts().clear();
	}

	public void addRequest(RequestSrc nextRequestSrc) {
		addRequest(rts.size(), nextRequestSrc);
	}

	public static void next(RequestSrc curSrcRequest, Object oj,
			RequestController srcRequests) {
		RequestSrc targetSrc = prep(curSrcRequest, oj, srcRequests);
		Logs.i("=== targetSrc 1 " + targetSrc);
		curSrcRequest.onFinish();
		targetSrc.enterSrc(targetSrc, srcRequests);
	}

	public static RequestSrc prep(RequestSrc defaultCurSrcRequest, Object oj,
			RequestController srcRequests) {
		EntityObj ej = (EntityObj) oj;
		RequestSrc targetSrc = null;
		List<String> vals = new ArrayList<String>(0);
		Object tmp = null;
		Object tmpValue = null;
		String labelVal = null;
		if (ej != null && ej.fieldContents != null) {
			Logs.i("--- " + ej.fieldContents);
			for (int i = 0; i < defaultCurSrcRequest.getRuntimeLinkForVisit()
					.size(); i++) {
				if ((tmp = defaultCurSrcRequest.getRuntimeLinkForVisit().get(i)) == null) {
					continue;
				}
				tmpValue = ej.fieldContents.get(tmp);
				if (tmpValue == null) {
					continue;
				}
				vals.add(tmpValue.toString());
			}
			Logs.i("--- tmp " + tmp + " "
					+ defaultCurSrcRequest.getNavigationNode().getLyLabel());
			if ((tmpValue = ej.fieldContents.get(defaultCurSrcRequest
					.getNavigationNode().getLyLabel())) != null) {
				labelVal = tmpValue.toString();
			}
			Logs.i("--- labelVal " + vals + " ");
			int i = srcRequests.rts.indexOf(defaultCurSrcRequest);
			targetSrc = srcRequests.rts.get(i + 1);
			targetSrc.getNavigationNode().setCurrLyLabel(labelVal);
			if (srcRequests.recur_enabled) {
				targetSrc.getNavigationNode().setCurrVisitValue(vals);
			}
			targetSrc.lang.cacheInfo.setCacheTable(srcRequests.activity
					.getClass().getName().replaceAll("\\.", "_"), labelVal);

		}
		return targetSrc;
	}

	public void toRequest(int i, String[] pnValue) {
		if (this.curRequestSrc != null) {
			this.curRequestSrc.onFinish();
		}

		RequestSrc targetSrc = this.rts.get(i);
		targetSrc.setEntrance(pnValue);
		targetSrc.enterSrc(targetSrc, this);
	}

	public boolean onBack() {
		return onBack(this.curRequestSrc, this, false);
	}

	public boolean backFloor() {
		return onBack(this.curRequestSrc, this, false, false);
	}

	public boolean onBack(Boolean stayTop) {
		return onBack(this.curRequestSrc, this, stayTop);
	}

	public void pop() {
		this.rts.remove(rts.size() - 1);
		setCurRequestSrc(rts.get(rts.size() - 1));
	}

	public static boolean onBack(RequestSrc curSrcObj,
			RequestController requestSrces, Boolean stayTop) {
		return onBack(curSrcObj, requestSrces, stayTop, true);
	}

	public static boolean onBack(RequestSrc curSrcObj,
			RequestController requestSrces, Boolean stayTop, boolean show) {
		try {
			
			RequestSrc targetSrcObj = requestSrces.getPrevSrcRequest(curSrcObj);
			if (curSrcObj != null) {
				for (Object enty : curSrcObj.es) {
					DescInfo soap = ((DescInfo) ((Entry) enty).getKey());
					soap.setRuntimeVal(soap.getPropertyVal());
				}
			}
			if (targetSrcObj != null) {
				// Logs.i("--- visit src " + targetSrcObj + " " + requestSrces +
				// " "
				// + requestSrces.rts.indexOf(targetSrcObj));
				requestSrces.recur_enabled = true;
				curSrcObj.restoreAdapterView();
				curSrcObj.onFinish();
				requestSrces.removeRequest(curSrcObj);
				requestSrces.curRequestSrc = targetSrcObj;
				if (show) {
					RequestSrc.enterSrc(targetSrcObj, requestSrces);
				}
				Logs.i("=== requestSrces "+requestSrces.rts);
			} else {
				if (stayTop == null)
					curSrcObj.base.finish();
				if (!stayTop)
					if (curSrcObj.base.getClass() == PageActivity.class) {
						((PageActivity) curSrcObj.base).finishParentClass();
					} else {
						curSrcObj.base.finish();
					}
				else
					return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String toString() {
		return "RequestSrces [rts=" + rts + "]";
	}

}
