package com.jibo.app;

import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.jibo.app.news.NewsAdapter;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.util.Logs;
import com.jibo.v4.pagerui.PageActivity;

public class ArticleActivity extends PageActivity {
	public ListView getAdaptView() {
		return null;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (getAdaptView() != null) {
			if (getAdaptView().getAdapter() instanceof MapAdapter){
				Logs.i("= a "+((MapAdapter) getAdaptView().getAdapter()));
				((MapAdapter) getAdaptView().getAdapter()).notifyDataSetChanged();
			}
			else if (getAdaptView().getAdapter() instanceof HeaderViewListAdapter){
				Logs.i("= aa "+((HeaderViewListAdapter) getAdaptView()
						.getAdapter()).getWrappedAdapter());
				Logs.i("= 1a "+DetailsData.viewedNews);
				if(((HeaderViewListAdapter) getAdaptView()
						.getAdapter()).getWrappedAdapter() instanceof NewsAdapter){
					Logs.i("= bb "+((HeaderViewListAdapter) getAdaptView()
							.getAdapter()).getWrappedAdapter());
					((NewsAdapter) ((HeaderViewListAdapter) getAdaptView()
							.getAdapter()).getWrappedAdapter())
							.notifyDataSetChanged();
				}else
				((MapAdapter) ((HeaderViewListAdapter) getAdaptView()
						.getAdapter()).getWrappedAdapter())
						.notifyDataSetChanged();}
		}
		
	}
}
