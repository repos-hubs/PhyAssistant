package com.jibo.base.overscroll;

import com.api.android.GBApp.R;
import com.jibo.util.Logs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public abstract class PullToRefreshAdapterViewBase<T extends AbsListView>
		extends PullToRefreshBase<T> implements OnScrollListener {

	private int lastSavedFirstVisibleItem = -1;
	private OnScrollListener onScrollListener;
	private OnLastItemVisibleListener onLastItemVisibleListener;
	private View emptyView;
	private FrameLayout refreshableViewHolder;
	private ImageView mTopImageView;

	public PullToRefreshAdapterViewBase(Context context) {
		super(context);
		// refreshableView.setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, int mode) {
		super(context, mode);
		// ((ListView)refreshableView).setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		// refreshableView.setOnScrollListener(this);
	}

	abstract public ContextMenuInfo getContextMenuInfo();

	public final void onScroll(final AbsListView view,
			final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {

		if (null != onLastItemVisibleListener) {
			// detect if last item is visible
			if (visibleItemCount > 0
					&& (firstVisibleItem + visibleItemCount == totalItemCount)) {
				// only process first event
				if (firstVisibleItem != lastSavedFirstVisibleItem) {
					lastSavedFirstVisibleItem = firstVisibleItem;
					onLastItemVisibleListener.onLastItemVisible();
				}
			}
		}

		if (null != onScrollListener) {
			onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public final void onScrollStateChanged(final AbsListView view,
			final int scrollState) {
		if (null != onScrollListener) {
			onScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	public void setBackToTopView(ImageView mTopImageView) {
		this.mTopImageView = mTopImageView;
		mTopImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (refreshableView instanceof ListView) {
					((ListView) refreshableView).setSelection(0);
				} else if (refreshableView instanceof GridView) {
					((GridView) refreshableView).setSelection(0);
				}
			}
		});
	}

	/**
	 * Sets the Empty View to be used by the Adapter View.
	 * 
	 * We need it handle it ourselves so that we can Pull-to-Refresh when the
	 * Empty View is shown.
	 * 
	 * Please note, you do <strong>not</strong> usually need to call this method
	 * yourself. Calling setEmptyView on the AdapterView will automatically call
	 * this method and set everything up. This includes when the Android
	 * Framework automatically sets the Empty View based on it's ID.
	 * 
	 * @param newEmptyView
	 *            - Empty View to be used
	 */
	public final void setEmptyView(View newEmptyView) {
		// If we already have an Empty View, remove it
		if (null != emptyView) {
			refreshableViewHolder.removeView(emptyView);
		}

		if (null != newEmptyView) {
			ViewParent newEmptyViewParent = newEmptyView.getParent();
			if (null != newEmptyViewParent
					&& newEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
			}

			this.refreshableViewHolder.addView(newEmptyView,
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT);
		}

		if (refreshableView instanceof EmptyViewMethodAccessor) {
			((EmptyViewMethodAccessor) refreshableView)
					.setEmptyViewInternal(newEmptyView);
		} else {
			// this.refreshableView.setEmptyView(newEmptyView);
		}
	}

	public final void setOnLastItemVisibleListener(
			OnLastItemVisibleListener listener) {
		onLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(OnScrollListener listener) {
		onScrollListener = listener;
	}

	protected void addRefreshableView(Context context, T refreshableView) {
		refreshableViewHolder = new FrameLayout(context);
		refreshableViewHolder.addView(refreshableView,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		addView(refreshableViewHolder, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 0, 1.0f));
	};

	protected boolean isReadyForPullDown() {
		return isFirstItemVisible();
	}

	protected boolean isReadyForPullUp() {
		return isLastItemVisible();
	}

	public AdapterView getListView() {
		return (AdapterView) this.refreshableView.findViewById(R.id.lst_item);
	}

	private boolean isFirstItemVisible() {		
		
		if(this.getListView()==null){
			Logs.i(" == return false 111");
			return false;
		}
		Logs.i(" == return false 1112_2 "+this.getListView().getVisibility());
		if(this.getListView().getVisibility()==View.GONE){
			Logs.i(" == return false 1112_21 "+this.getListView().getVisibility());
			return true;
		}
		if (this.getListView().getCount() == 0) {
			return true;
		} else if (getListView().getFirstVisiblePosition() == 0) {

			final View firstVisibleChild = getListView().getChildAt(0);
			
			if (firstVisibleChild != null) {
				Logs.i(" == return false 1112");
				return firstVisibleChild.getTop() >= getListView().getTop();
			}
		}
		Logs.i(" == return false 1113");
		return false;
	}

	private boolean isLastItemVisible() {
		if(this.getListView()==null){
			return false;
		}
		final int count = this.getListView().getCount();
		final int lastVisiblePosition = getListView().getLastVisiblePosition();

		if (count == 0) {
			return true;
		} else if (lastVisiblePosition == count - 1) {

			final int childIndex = lastVisiblePosition
					- getListView().getFirstVisiblePosition();
			final View lastVisibleChild = getListView().getChildAt(childIndex);

			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= getListView()
						.getBottom();
			}
		}
		return false;
	}
}
