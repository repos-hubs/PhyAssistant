package com.jibo.base.src;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jibo.base.src.request.RecursiveListener;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.util.Logs;

public class LinkRequestItemClicker implements OnItemClickListener {
	private RequestSrc curSrcObject;
	private RequestController srcRequests;
	private RequestSrc nextSrcRequest;
	public Map<Integer, Object> src_click = new HashMap<Integer, Object>();

	public LinkRequestItemClicker(RequestController srcRequests,
			Object clickitem) {
		super();
		this.srcRequests = srcRequests;
		this.clickitem = clickitem;
	}

	public LinkRequestItemClicker(RequestController srcRequests) {
		super();
		this.srcRequests = srcRequests;
	}

	public RequestSrc getCurSrcRequest() {
		return curSrcObject;
	}

	public void setRequestSrc(RequestSrc srcObject) {
		this.curSrcObject = srcObject;
	}

	public RequestSrc getNextSrcRequest() {
		return nextSrcRequest;
	}

	public void setNextRequestSrc(RequestSrc nextSrcRequest) {
		this.nextSrcRequest = nextSrcRequest;
	}

	public RequestController getSrcRequests() {
		return srcRequests;
	}

	public Object prevClick;
	public Object clickitem;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		clickitem = parent.getItemAtPosition(position);
//		if (prevClick != clickitem) {
//			Logs.i("================ "
//					+ curSrcObject.linkRequestItemClicker.srcRequests.rts
//							.indexOf(curSrcObject) + " " + prevClick + " "
//					+ clickitem);
//			if (prevClick != null)
				src_click.put(curSrcObject.linkRequestItemClicker.srcRequests.rts
						.indexOf(curSrcObject)+1, clickitem);
			
//		}
		prevClick = clickitem;
		if (clickitem == null)
			return;
		curSrcObject.linkRequestItemClicker.srcRequests.clickitemTp = clickitem;
		click();
	}

	public List<String> getClickValue() {
		return Arrays
				.asList(((EntityObj) ((LinkRequestItemClicker) curSrcObject.selfDefineditemClickListener).clickitem)
						.get(curSrcObject.getNormalLinkForVisit().get(0)));
	}

	public void click() {
		RequestSrc defaultCurSrcRequest = curSrcObject;
		if (defaultCurSrcRequest == null) {
			defaultCurSrcRequest = nextSrcRequest;
		}

		if (srcRequests.recur_enabled && defaultCurSrcRequest.isRecursiveBool()) {
			if (defaultCurSrcRequest.getCallbackListener() != null) {
				if (defaultCurSrcRequest.getCallbackListener() instanceof RecursiveListener) {
					// RequestSrc prevRequestSrc =
					// getNextSrcRequest(defaultCurSrcRequest);
					// if (prevRequestSrc != null) {
					// prevRequestSrc.getLinkRequestItemClicker().setRequestSrc(prevRequestSrc);
					// }
					// prevRequestSrc.getLinkRequestItemClicker().setNextRequestSrc();

					((RecursiveListener) defaultCurSrcRequest
							.getCallbackListener()).nextLayerValue = Arrays
							.asList(new String[] { ((EntityObj) clickitem)
									.get(defaultCurSrcRequest
											.getNormalLinkForVisit().get(0)) });
					if (defaultCurSrcRequest.linkRequestItemClicker
							.getSrcRequests().dynActivity) {

						if (defaultCurSrcRequest.view != null) {
							this.srcRequests.activity.startActivity(new Intent(
									defaultCurSrcRequest.view.getContext(),
									FloorActivity.class));
							return;
						}
					}
					defaultCurSrcRequest.launchRequest(defaultCurSrcRequest
							.getCheckInfo());

				}
			}
		} else {

			defaultCurSrcRequest.reset();//
			srcRequests.curRequestSrc.reset();//
			defaultCurSrcRequest.setRuntimeLinkForVisit(defaultCurSrcRequest
					.getNormalLinkForVisit());
			if (defaultCurSrcRequest.linkRequestItemClicker.getSrcRequests().dynActivity) {
				if (defaultCurSrcRequest.view != null) {
					this.srcRequests.activity.startActivity(new Intent(
							defaultCurSrcRequest.view.getContext(),
							FloorActivity.class));
					return;
				}
			}
			RequestController
					.next(defaultCurSrcRequest, clickitem, srcRequests);
		}
	}

}
