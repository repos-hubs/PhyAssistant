package com.jibo.base.src.request;

import java.util.List;
import java.util.Map.Entry;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.RequestResult;
import com.jibo.base.src.request.CursorParser.CursorResult;
import com.jibo.base.src.request.RequestSrc.RequestLogInfo;
import com.jibo.base.src.request.config.DBInfo;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.data.EntityObjPaser;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.util.Logs;

public class AsycRequestHandler extends AsyncSoapResponseHandler {
	RequestSrc lis;

	public AsycRequestHandler(RequestSrc listUnit) {
		super();
		this.lis = listUnit;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		super.onFinish();
	}

	@Override
	public void onReload() {
		// TODO Auto-generated method stub
		Toast.makeText(GBApplication.gbapp,
				GBApplication.gbapp.getString(R.string.retryweb),
				Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(Object content) {
		// TODO Auto-generated method stub
		super.onSuccess(content);
//		if(lis.activity!=null){
//			if(lis.activity.handledRequest){
//				return;
//			}
//			lis.activity.handledRequest = true;
//		}
		lis.preHandle();
		try {
			// if (lis.isInPaging) {
			// if (lis.isPagingLoad) {
			// if (content instanceof EntityObjPaser) {
			//
			// if (!((EntityObjPaser) content).getTag()
			// .equalsIgnoreCase(lis.newFlag)) {
			// return;
			// }
			// }
			//
			// }
			// }
			RequestLogInfo requestLogInfo = null;
			RequestController rc = lis.linkRequestItemClicker.getSrcRequests();
			String tag = null;
			if (content instanceof EntityObjPaser) {
				Logs.i("--- log after " + ((EntityObjPaser) content).getTag());
				tag = ((EntityObjPaser) content).getTag();
				tag = tag.replaceAll(",", ";");
				if (lis.runtimeReqLogIdentifier.get((Object) tag) == null) {
					Logs.i("--- log end " + tag + " "
							+ lis.runtimeReqLogIdentifier);
					return;
				}
				if (lis.runtimeReqLogIdentifier.get((Object) tag).disabled) {
					Logs.i("req == failed "
							+ lis.runtimeReqLogIdentifier.get((Object) tag).count++);
					return;
				}
			} else if (content instanceof CursorResult) {
				tag = ((CursorResult) content).getTag();
				Logs.i("--- log1 " + lis + " " + tag);
				if (!lis.runtimeReqLogIdentifier.keySet().contains(
						((CursorResult) content).getTag())) {
					return;
				}
				requestLogInfo = lis.runtimeReqLogIdentifier.get(tag);
				lis.runtimeReqLogIdentifier.remove(tag);
				
			}
					
			if (requestLogInfo != null
					&& requestLogInfo.type == DescInfo.REQUEST_TYPE_LOGIC) {
				lis.runtimeReqLogIdentifier.remove(tag);
				if (lis.getCallbackListener() != null) {
					lis.getCallbackListener().setResultOj(
							((RequestResult) content).getObjs());
					if (lis.getCallbackListener() instanceof RecursiveListener) {
						if (rc.recur_enabled
								&& lis.getCallbackListener().callback()) {
							Logs.i("== class " + lis.getClass());
							RequestSrc copy = RequestSrc.class
									.getConstructor(RequestSrc.class)
									.newInstance(
											lis.linkRequestItemClicker
													.getSrcRequests().curRequestSrc);
							RequestSrc addedRequestSrc = lis
									.insertSrc(lis.linkRequestItemClicker
											.getSrcRequests().curRequestSrc,
											copy);
							if (lis.linkRequestItemClicker.getSrcRequests().dynActivity) {
								addedRequestSrc.view = lis.linkRequestItemClicker
										.getSrcRequests().viewsystem.peek();
								
							}
							// String[] values = new String[] { ((EntityObj)
							// ((LinkRequestItemClicker)
							// addedRequestSrc.itemClickListener).clickitem)
							// .get(lis.getLinkForVisit().get(0)) };
							// addedRequestSrc.getNavigationNode()
							// .setCurrVisitValue(values);
							rc.curRequestSrc
									.setRuntimeLinkForVisit(lis.linkRequestItemClicker
											.getSrcRequests().curRequestSrc
											.getNormalLinkForVisit());
							rc.recurIdx = lis.linkRequestItemClicker
									.getSrcRequests().rts
									.lastIndexOf(lis.linkRequestItemClicker
											.getSrcRequests().curRequestSrc) + 1;
							Logs.i("-- rc.recurIdx " + rc.recurIdx);
							RequestController
									.next(rc.curRequestSrc,
											lis.linkRequestItemClicker.clickitem,
											lis.linkRequestItemClicker
													.getSrcRequests());

						} else {
							if (lis.linkRequestItemClicker.getSrcRequests().dynActivity) {
								lis.view = lis.linkRequestItemClicker
										.getSrcRequests().viewsystem.peek();
								
							}
							((RecursiveListener) lis.getCallbackListener())
									.stop(lis.linkRequestItemClicker
											.getCurSrcRequest(),
											lis.linkRequestItemClicker
													.getNextSrcRequest());
						}
						return;
					}
				}
			}

			if (content != null) {
				List<EntityObj> eob = ((RequestResult) content).getObjs();
				Logs.i("--- src " + lis + " " + eob.size());
				if (eob.isEmpty()) {
					// if(((ViewGroup)lis.listView.getParent()))
				}
				if (lis.shouldCache(eob,
						((Entry<DescInfo, Integer>) lis.es.get(lis.tmpSoap)))) {
					lis.cache(eob);
				}
				if (lis.shouldInstead(eob,
						((Entry<DescInfo, Integer>) lis.es.get(lis.tmpSoap)))) {
					lis.adapter.getItemDataSrc().clear();
					lis.adapter.getItemDataSrc().setContent(eob);
					Logs.i(" --- s replace new size " + eob.size());
				} else
					((List<EntityObj>) lis.adapter.getItemDataSrc()
							.getContent()).addAll(eob);
				Logs.i(" --- s add size " + eob.size());
				lis.adapter.notifyDataSetChanged();

				if (lis.tmpSoap < lis.es.size()
						&& (((Entry<DescInfo, Integer>) lis.es.get(lis.tmpSoap))
								.getKey() instanceof DBInfo)) {
					removeFooter();
					Logs.i("----||" + lis.base);
				}

				lis.adjust4EmptyView();
				lis.postHandle(eob);

				int thistimecount = 0;
				boolean ltCount = ((thistimecount = lis.getBatchCount()) != -1 && eob
						.size() < thistimecount);
				if (ltCount) {
					lis.dataTail = true;
					removeFooter();
					return;
				}
				if (lis.tmpSoap == lis.es.size()) {
					return;
				}
				lis.tmpSoap = lis.nextSoap();
				if (lis.tmpSoap == lis.es.size()) {
					resetScrollCounterPiece();
					return;
				}
				Logs.i(" soap " + lis.tmpSoap + " " + lis.es.size());
				Boolean result = RequestSrc.sendRequest(lis,
						((Entry<DescInfo, Integer>) lis.es.get(lis.tmpSoap))
								.getKey(), false);
				if (ltCount || (result != null && !result)) {
					if (lis.tmpSoap + 1 == lis.es.size()) {
						// removeFooter();
						return;
					}
					lis.tmpSoap = lis.nextSoap();
					RequestSrc
							.sendRequest(lis,
									((Entry<DescInfo, Integer>) lis.es
											.get(lis.tmpSoap)).getKey(), false);
				}
				Logs.i("loadcontinue --- " + lis.adapter.getCount());
				if (lis.isInPaging) {
					lis.isPagingLoad = true;
				}
				Logs.i("req == success "
						+ lis.runtimeReqLogIdentifier.get((Object) tag).count++);
				lis.runtimeReqLogIdentifier.get((Object) tag).disabled = true;
				lis.newFlag = tag;
				lis.listView.findFocus();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetScrollCounterPiece() {
		for (ScrollCounter sc : ((Entry<DescInfo, Integer>) lis.es
				.get(lis.tmpSoap - 1)).getKey().batch) {
			sc.runtimePiece = sc.piece;
		}
	}

	public void removeFooter() {
		if (lis.listView.getFooterViewsCount() > 0) {
			Logs.i("-- " + lis.listView.removeFooterView(lis.footerView));
			// lis.footerView.setVisibility(View.GONE);
		}
	}

};
