/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.app.invite;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.SyncStateContract.Constants;
import android.view.View;


import com.api.android.GBApp.R;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.util.Logs;

/**
 * 
 * @author chenliang
 */
public class SearchListViewHolder extends ViewHolder {

	SearchActivity searchActivity;
	Cursor mediaCursor = null;
	Cursor filesCursor = null;
	AdapterSrc dataSrc;
	AdaptInfo adaptInfo;

	public SearchListViewHolder(View view, SearchActivity activity) {
		super(view, activity);
		searchActivity = activity;
		if (Data.info == null) {
			Data.info = SearchActivity.getSourceContacts("", activity);
		}
		searchActivity.fetchInvitd(searchActivity.username);
	}

	@Override
	public synchronized void dataChanged() {
		Logs.i("inputkeyWords " + searchActivity.objectWords);
		if (dataSrc != null) {
			dataSrc.clear();
		}
		dataSrc = new AdapterSrc(searchActivity.getKeyContacts(Data.info,
				searchActivity.objectWords));
		(searchActivity.adapter).setItemDataSrc(dataSrc);
		searchActivity.adapter.reinitSelectedAllBck(dataSrc.getCount());
		((SearchAdapter) searchActivity.adapter)
				.switchKeyWords(searchActivity.objectWords);
	}

	@Override
	public void start() {
		view = searchActivity.listview;
		searchActivity.buildAdapter(SearchAdapter.class,
				searchActivity.getAdaptInfo());
		((SearchAdapter) searchActivity.adapter).setHighlightInfo(
				searchActivity.objectWords, new String[] { "name","phone","email" }, Color.RED);
		searchActivity.listview.setAdapter(searchActivity.adapter);
	}

	public synchronized void rebind() {
		// if (((Cursor) searchActivity.adapter.getItemDataSrc().getContent())
		// != null
		// && ((Cursor) searchActivity.adapter.getItemDataSrc()
		// .getContent()).isClosed()) {
		// return;
		// }
		// searchActivity.setListViewImageLoadMode(searchActivity.listview,
		// ThumbnailLoader.MODE_DIRECT);
		searchActivity.adapter.notifyDataSetChanged();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}
}
