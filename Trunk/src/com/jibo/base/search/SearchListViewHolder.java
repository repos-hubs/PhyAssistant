/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.base.search;

import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Color;
import android.view.View;

import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
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

	}

	boolean resultInited;

	@Override
	public synchronized void dataChanged() {
		Logs.i("inputkeyWords " + searchActivity.objectWords);
		if (searchActivity.getSourceContacts("", searchActivity).size() == 0) {
			return;
		}
		if (!resultInited) {
			resultInited = true;
			searchActivity.result = new ArrayList<EntityObj>(
					searchActivity.getSourceContacts("", searchActivity));
		}
		searchActivity.resultCache = new ArrayList<EntityObj>(
				searchActivity.result);
		if (dataSrc != null) {
			dataSrc.clear();
		}
		dataSrc = new AdapterSrc(searchActivity.getKeyContacts(
				searchActivity.resultCache, searchActivity.objectWords));
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
				searchActivity.objectWords, new String[] { "name" }, Color.RED);
		searchActivity.listview.setAdapter(searchActivity.adapter);
	}

	public synchronized void rebind() {
		searchActivity.adapter.notifyDataSetChanged();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}
}
