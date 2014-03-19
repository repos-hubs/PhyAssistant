package com.jibo.base.overscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.api.android.GBApp.R;

public class OverScrollListView extends PullToRefreshAdapterViewBase<ListView> {

//	private LoadingLayout headerLoadingView;
//	private LoadingLayout footerLoadingView;


	class InternalListView extends ListView implements EmptyViewMethodAccessor {

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			OverScrollListView.this.setEmptyView(emptyView);
		}
		
		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}
	}

	public OverScrollListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
	}
	
	public OverScrollListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public OverScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		this.setPadding(0, refresh.getHeight(), 0, 0);
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalListView) getRefreshableView()).getContextMenuInfo();
	}

//	public void setReleaseLabel(String releaseLabel) {
//		super.setReleaseLabel(releaseLabel);
//		if (null != headerLoadingView) {
//			headerLoadingView.setReleaseLabel(releaseLabel);
//		}
//		if (null != footerLoadingView) {
//			footerLoadingView.setReleaseLabel(releaseLabel);
//		}
//	}
//
//	public void setPullLabel(String pullLabel) {
//		super.setPullLabel(pullLabel);
//
//		if (null != headerLoadingView) {
//			headerLoadingView.setPullLabel(pullLabel);
//		}
//		if (null != footerLoadingView) {
//			footerLoadingView.setPullLabel(pullLabel);
//		}
//	}
//
//	public void setRefreshingLabel(String refreshingLabel) {
//		super.setRefreshingLabel(refreshingLabel);
//
//		if (null != headerLoadingView) {
//			headerLoadingView.setRefreshingLabel(refreshingLabel);
//		}
//		if (null != footerLoadingView) {
//			footerLoadingView.setRefreshingLabel(refreshingLabel);
//		}
//	}

	View cateView;
	
	public View getCateView() {
		return cateView;
	}

	public void setCateView(View cateView) {
		this.cateView = cateView;
	}
	ViewGroup view;
	public void rmRefresh(){
		view.findViewById(R.id.refreshid).setVisibility(View.GONE);
		
	}
	@Override
	protected final View createRefreshableView(Context context, AttributeSet attrs) {
		
		view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.detail_list, null);
//		((ViewGroup)view.findViewById(R.id.outerid)).addView(refresh, 0);
		ListView lv =  (ListView) view.findViewById(R.id.lst_item);
		cateView = view.findViewById(R.id.category);
		//		final int mode = this.getMode();
//		// Loading View Strings
//		String pullLabel = context.getString(R.string.pull_to_refresh_pull_label);
//		String refreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
//		String releaseLabel = context.getString(R.string.pull_to_refresh_release_label);

		// Add Loading Views
//		if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
//			FrameLayout frame = new FrameLayout(context);
//			headerLoadingView = new LoadingLayout(context, MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel,
//					refreshingLabel);
//			frame.addView(headerLoadingView, FrameLayout.LayoutParams.FILL_PARENT,
//					FrameLayout.LayoutParams.WRAP_CONTENT);
//			headerLoadingView.setVisibility(View.GONE);
//			lv.addHeaderView(frame);
//		}
//		if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
//			FrameLayout frame = new FrameLayout(context);
//			footerLoadingView = new LoadingLayout(context, MODE_PULL_UP_TO_REFRESH, releaseLabel, pullLabel,
//					refreshingLabel);
//			frame.addView(footerLoadingView, FrameLayout.LayoutParams.FILL_PARENT,
//					FrameLayout.LayoutParams.WRAP_CONTENT);
//			footerLoadingView.setVisibility(View.GONE);
//			lv.addFooterView(frame);
//		}

		// Set it to this so it can be used in ListActivity/ListFragment
//		view.setId(android.R.id.list);
		return view;
	}

	
//	@Override
//	protected void setRefreshingInternal(boolean doScroll) {
//		super.setRefreshingInternal(false);
//
//		final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
//		final int selection, scrollToY;
//
//		switch (getCurrentMode()) {
//			case MODE_PULL_UP_TO_REFRESH:
//				originalLoadingLayout = this.getFooterLayout();
//				listViewLoadingLayout = this.footerLoadingView;
//				selection = refreshableView.getCount() - 1;
//				scrollToY = getScrollY() - getHeaderHeight();
//				break;
//			case MODE_PULL_DOWN_TO_REFRESH:
//			default:
//				originalLoadingLayout = this.getHeaderLayout();
//				listViewLoadingLayout = this.headerLoadingView;
//				selection = 0;
//				scrollToY = getScrollY() + getHeaderHeight();
//				break;
//		}
//
//		if (doScroll) {
//			// We scroll slightly so that the ListView's header/footer is at the
//			// same Y position as our normal header/footer
//			this.setHeaderScroll(scrollToY);
//		}
//
//		// Hide our original Loading View
//		originalLoadingLayout.setVisibility(View.INVISIBLE);
//
//		// Show the ListView Loading View and set it to refresh
//		listViewLoadingLayout.setVisibility(View.VISIBLE);
//		listViewLoadingLayout.refreshing();
//
//		if (doScroll) {
//			// Make sure the ListView is scrolled to show the loading
//			// header/footer
//			refreshableView.setSelection(selection);
//
//			// Smooth scroll as normal
//			smoothScrollTo(0);
//		}
//	}

//	@Override
//	protected void resetHeader() {
//
//		LoadingLayout originalLoadingLayout;
//		LoadingLayout listViewLoadingLayout;
//
//		int scrollToHeight = getHeaderHeight();
//		final boolean doScroll;
//
//		switch (getCurrentMode()) {
//			case MODE_PULL_UP_TO_REFRESH:
//				originalLoadingLayout = this.getFooterLayout();
//				listViewLoadingLayout = footerLoadingView;
//				doScroll = this.isReadyForPullUp();
//				break;
//			case MODE_PULL_DOWN_TO_REFRESH:
//			default:
//				originalLoadingLayout = this.getHeaderLayout();
//				listViewLoadingLayout = headerLoadingView;
//				scrollToHeight *= -1;
//				doScroll = this.isReadyForPullDown();
//				break;
//		}
//
//		// Set our Original View to Visible
//		originalLoadingLayout.setVisibility(View.VISIBLE);
//
//		// Scroll so our View is at the same Y as the ListView header/footer,
//		// but only scroll if the ListView is at the top/bottom
//		if (doScroll) {
//			this.setHeaderScroll(scrollToHeight);
//		}
//
//		// Hide the ListView Header/Footer
//		listViewLoadingLayout.setVisibility(View.GONE);
//
//		super.resetHeader();
//	}

}
