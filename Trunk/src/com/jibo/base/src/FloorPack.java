package com.jibo.base.src;

import android.view.View;
import android.view.View.OnClickListener;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.app.research.FirstStep;
import com.jibo.base.src.request.RecursiveListener;
import com.jibo.base.src.request.RequestSrc;

public class FloorPack {

	public View detailist;
	public static boolean hasHead;

	/**
	 * @param args
	 */
	public void startup(BaseActivity ba, String category,
			final RequestController srcRequests) {
		try {
			if (detailist == null) {
				detailist = ba.findViewById(R.id.detail_list);
			}

			// ba.setContentView(detailist);
			FirstStep firstStep = new FirstStep();
			if (!hasHead) {
				hasHead = true;
				firstStep.process(ba, category, srcRequests);
				srcRequests.tag = category;
				srcRequests.rts.get(0).view = detailist;
				srcRequests.rts.get(0).activity = ba;
				ba.findViewById(R.id.viewall).setVisibility(View.GONE);
				srcRequests.atymap.put(srcRequests.rts.get(0), ba);
				firstStep.launch(category, srcRequests);
				return;
			} else {
				srcRequests.viewsystem.push(detailist);
			}
			final RequestSrc nextRequestSrc = srcRequests.getNextSrcRequest(srcRequests.curRequestSrc);
			nextRequestSrc.view = detailist;
			final RequestSrc defaultCurSrcRequest = srcRequests.curRequestSrc;
			srcRequests.atymap.put(defaultCurSrcRequest, ba);
			srcRequests.curRequestSrc.activity = ba;
			if (!srcRequests.recur_enabled) {
				// RequestController.next(defaultCurSrcRequest,
				// defaultCurSrcRequest.linkRequestItemClicker.clickitem==null?srcRequests.clickitemTp:defaultCurSrcRequest.linkRequestItemClicker.clickitem,
				// srcRequests);

				defaultCurSrcRequest.linkRequestItemClicker.clickitem = defaultCurSrcRequest.linkRequestItemClicker.src_click
						.get(srcRequests.rts.indexOf(defaultCurSrcRequest));
				srcRequests
						.getNextSrcRequest(defaultCurSrcRequest)
						.getNavigationNode()
						.setCurrVisitValue(
								new String[] { ((EntityObj) srcRequests.clickitemTp).get(defaultCurSrcRequest
										.isRecursiveBool() ? (defaultCurSrcRequest
										.getCallbackListener()
										.getLogicListener()).elseVIsitLink
										: defaultCurSrcRequest
												.getRuntimeLinkForVisit()
												.get(0)) });
				
				RequestController
						.next(defaultCurSrcRequest,
								defaultCurSrcRequest.linkRequestItemClicker.clickitem == null ? srcRequests.clickitemTp
										: defaultCurSrcRequest.linkRequestItemClicker.clickitem,
								srcRequests);
				
				return;
			}
			if (srcRequests.recur_enabled
					&& defaultCurSrcRequest.isRecursiveBool()) {
				if (defaultCurSrcRequest.getCallbackListener() != null) {
					if (defaultCurSrcRequest.getCallbackListener() instanceof RecursiveListener) {

						if (defaultCurSrcRequest.view != null) {
							if (firstStep.categoryKeys.contains(srcRequests.tag
									.toLowerCase())) {
								
								if (srcRequests.rts.size() >= 3) {
									defaultCurSrcRequest.activity.findViewById(R.id.viewall).setBackgroundResource(R.drawable.arrow03);
									
									defaultCurSrcRequest.categoryViewIsSetted = true;
									defaultCurSrcRequest.categoryViewListener = new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// Logs.i("c..................l...................ick");
											// Context context =
											// arg0.getContext();
											// srcRequests.recur_enabled
											// = false;
											// context.startActivity(new
											// Intent(
											// context,
											// FloorActivity.class));
											srcRequests.recur_enabled = false;
											RequestSrc rq = srcRequests
													.getPrevSrcRequest(defaultCurSrcRequest);
											rq.linkRequestItemClicker.clickitem = srcRequests.clickitemTp;
											rq.linkRequestItemClicker
													.click();
										}

									};
									defaultCurSrcRequest.categoryView
											.setOnClickListener(defaultCurSrcRequest.categoryViewListener);
								} else {
									ba.findViewById(R.id.viewall)
											.setVisibility(View.GONE);
								}
							} else {
								ba.findViewById(R.id.viewall).setVisibility(
										View.GONE);
							}
							
							defaultCurSrcRequest
									.launchRequest(defaultCurSrcRequest
											.getCheckInfo());
						}

					}
				}
			} else {
				
				if (defaultCurSrcRequest.linkRequestItemClicker.clickitem == null) {
					defaultCurSrcRequest.linkRequestItemClicker.clickitem = srcRequests.clickitemTp;
				}
				RequestController.next(defaultCurSrcRequest,
						defaultCurSrcRequest.linkRequestItemClicker.clickitem,
						srcRequests);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
