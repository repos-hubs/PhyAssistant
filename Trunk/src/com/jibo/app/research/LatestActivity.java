package com.jibo.app.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.HomePageActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.ArticleActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.push.PushConst;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.overscroll.OverScrollListView;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

public class LatestActivity extends ArticleActivity {

	protected RequestController srcRequests;
	public OverScrollListView view;
	private boolean inited;
	private RequestSrc dtl;
	private View emptyView;
	private TranslateAnimation animation;
	private View refreshLayout;

	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
		if (view == null) {
			view = (OverScrollListView) (LayoutInflater.from(context).inflate(
					R.layout.overlistview, null));
			view.findViewById(R.id.refreshid).setVisibility(View.VISIBLE);
			this.setContentView(view);
			enableDropDown();
			emptyView = LayoutInflater.from(context).inflate(
					R.layout.empty_frame, null);
			((TextView) emptyView.findViewById(R.id.emptytext))
					.setText(getString(getEmptyText()));
			inited = false;

			handleInitViewLoad(250);
		}
		toDl();
	}

	public void enableDropDown() {
		// TODO Auto-generated method stub
		view.setPullToRefreshEnabled(true);
	}

	public int getEmptyText() {
		return R.string.empty_result;
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				view.rmRefresh();
				// view.initHeaderLayout(view.findViewById(R.id.lst_item)
				// .getHeight());
				return;
			}
			initOverScrollView();
		}

	};
	private Dialog waitingdialog;
	protected boolean initview;

	public void toDl() {
		RequestInfos infos = new RequestInfos();
		List<ScrollCounter> count = new ArrayList<ScrollCounter>();
		count.add(new ScrollCounter(20, 1));
		count.add(new ScrollCounter(10, -1));
		infos.putSrc(
				new SoapInfo(
						new String[] { "sign", "strSearch" },
						new String[] { "",/*
										 * \"*:*\"
										 */
						"{\"fq\":\"?\",\"start\":\"0\",\"rows\":\"20\",\"sort\":\"PubDate desc\"}" },
						SoapRes.REQ_ID_GET_PAPER_LIST, SoapRes.URLRESEARCH,
						count, "detail"), 1);
		dtl = new GBIRequest(infos, this, null, AdaptUI.genResearchAdapter(),
				"") {
			boolean shouldReplace = false;

			@Override
			public boolean shouldInstead(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub

				if (cached) {// ×ªï¿½ï¿½ï¿½æ»»ï¿½ï¿½ï¿½ï¿½

					cached = false;
					return true;
				}
				return false;

			}

			@Override
			public void preHandle() {
				// TODO Auto-generated method stub
				super.preHandle();
				if (waitingdialog != null && waitingdialog.isShowing()) {
					waitingdialog.cancel();
					waitingdialog.dismiss();

				}

			}

			boolean shouldCache;

			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				Logs.i("--- tmpPiece " + pieceIndex);
				if (this.pieceIndex == 0) {
					return true;
				}
				return false;
			}

			@Override
			public void postHandle(List<EntityObj> eob) {
				// TODO Auto-generated method stub
				super.postHandle(eob);
				if (this.pieceIndex == 0) {
					if (DetailsData.entities == null) {
						DetailsData.entities = eob;
					}
				} else {
					DetailsData.entities.addAll(eob);
				}

			}

			@Override
			public void transferPageParams(RequestSrc currSrc, DescInfo info) {
				// TODO Auto-generated method stub
				super.lucene_nextpage(currSrc, info);

			}

		};
		dtl.setToCache(true);
		srcRequests = new RequestController(view, this);
		((TextView) emptyView.findViewById(R.id.emptytext))
				.setText(R.string.seach_empty);
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(dtl);
		change();

		dtl.setItemClickListener(new DetailItemClickListener(
				"research_latest_papers", "id"));
		searchText();

	}

	public void initOverScrollView() {
		if (!inited) {
			try {

				view.initHeaderLayout(view.findViewById(R.id.outerid)
						.getHeight());
				inited = true;

				view.getHeaderLayout().findViewById(R.id.register_submit)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								view.onRefreshComplete();
								Logs.i("--- fq str filter "
										+ view.getHeaderLayout()
												.setLocalSettings());
								((MapAdapter) ((HeaderViewListAdapter) ((ListView) view
										.findViewById(R.id.lst_item))
										.getAdapter()).getWrappedAdapter())
										.getItemDataSrc().clear();
								((MapAdapter) ((HeaderViewListAdapter) ((ListView) view
										.findViewById(R.id.lst_item))
										.getAdapter()).getWrappedAdapter())
										.notifyDataSetChanged();

								srcRequests
										.startCat(new String[] { view
												.getHeaderLayout()
												.setLocalSettings() });
								showDialog(DialogRes.DIALOG_WAITING_FOR_DATA);

							}

						});
				view.getHeaderLayout().findViewById(R.id.register_cancel)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								MobclickAgent.onEvent(LatestActivity.this,
										"research_latest_filter_cancel");
								uploadLoginLogNew("research", "latest",
										"filter", null);
								view.onRefreshComplete();
							}

						});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// init animation add by teddy

		// animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
		// Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
		// Animation.RELATIVE_TO_SELF, refreshLayout.getHeight());
		// animation.setDuration(1000);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(650);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(1);
			}

		}).start();
		// fade();
		// TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 200);
		// animation.setInterpolator(new AccelerateDecelerateInterpolator());
		// animation.setDuration(2400);
		// animation.setRepeatCount(0);
		// animation.setAnimationListener(new Animation.AnimationListener() {
		// @Override
		// public void onAnimationStart(Animation animation) {
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {

		//
		// }
		// });
		// ((ViewGroup)
		// view.findViewById(R.id.outerid)).startAnimation(animation);

	}

	private void fade() {
		int w = this.getWindowManager().getDefaultDisplay().getWidth();
		int h = this.getWindowManager().getDefaultDisplay().getHeight();
		if (view.isPullToRefreshEnabled()) {
			// view.rmRefresh();
			// view.requestLayout();
			// view.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
			// SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, h
			// - view.findViewById(R.id.outerid).getHeight(), 0));
			// for(int i = h
			// - view.findViewById(R.id.outerid).getHeight();i<h
			// -
			// view.findViewById(R.id.outerid).getHeight()+view.refresh.getHeight();i++){
			// view.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
			// SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 0, i, 0));
			// }

			// for(int i = 0;i<10000;i++){
			// view.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
			// SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 0,
			// view.findViewById(R.id.outerid).getHeight()+350, 0));
			// }
			// for(int i = h
			// - view.findViewById(R.id.outerid).getHeight()+500;i>h
			// - view.findViewById(R.id.outerid).getHeight();i--){
			// view.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
			// SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 0, i, 0));
			// }
			// //
			// view.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
			// // SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0,
			// view.findViewById(R.id.outerid).getHeight()+500, 0));
			handler.sendEmptyMessage(1);
			// TranslateAnimation scrollback = new TranslateAnimation(0, 0, 0,
			// -200);
			// scrollback
			// .setInterpolator(new AccelerateDecelerateInterpolator());
			// scrollback.setDuration(2500);
			// scrollback.setRepeatCount(0);
			// view.startAnimation(scrollback);
			// scrollback.setAnimationListener(new AnimationL());
		}
	}

	public void searchText(String text) {
		srcRequests.startCat(new String[] { text });
	}

	public void change() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		handleInitViewLoad(0);
		super.onWindowFocusChanged(hasFocus);
	}

	private void handleInitViewLoad(int period) {
		if (!initview) {
			initview = true;
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(0);
				}

			}, period);
		}
	}

	@Override
	public void onBack(Boolean stayTop, boolean isBackKey) {
		srcRequests.onBack(stayTop);
		if (PushConst.pushFlag == null || PushConst.pushFlag) {
				startActivity(new Intent(this, HomePageActivity.class));
		}
	}

	public void searchText() {
		// TODO Auto-generated method stub
		String text = SharedPreferenceUtil.getValue(context, "filter_info",
				"search", String.class).toString();
		Logs.i("search 1 " + text);
		if (text == null || text.equals("")) {
			text = "AbstractIsEmpty:false";
		}
		Logs.i("search 2 " + text);
		searchText(text);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
		this.waitingdialog = dialog;
	}

	public ListView getAdaptView() {
		return dtl == null ? null : dtl.listView;
	}

	class AnimationL implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
			// view.addView(refreshLayout, 0, new LinearLayout.LayoutParams(
			// ViewGroup.LayoutParams.FILL_PARENT,
			// ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			view.rmRefresh();
		}
	}
}
