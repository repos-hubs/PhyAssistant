package com.jibo.app;

import android.view.LayoutInflater;
import android.view.View;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.app.news.NewsAdapter;
import com.jibo.app.research.ResearchAdapter;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;

public class AdaptUI {
	public static AdaptInfo adaptInfo;
	public static AdaptInfo newsAdaptInfo;
	public static ResearchAdapter researchAdapter;
	public static View emptyview;
	static {
		adaptInfo = new AdaptInfo();
		adaptInfo.objectFields = new String[] { "Title", "JournalName", "IF",
				"PublicDate"/*
							 * ,"journal" , "keyword"
							 */};
		adaptInfo.viewIds = new int[] { R.id.ArticleTitle,
				R.id.PeriodicalTitle, R.id.IF, R.id.date };
		adaptInfo.listviewItemLayoutId = R.layout.latest_list;

		newsAdaptInfo = new AdaptInfo();
		newsAdaptInfo.objectFields = new String[] { "imgUrl", "title",
				"newSummary", "stickMsg" };
		newsAdaptInfo.viewIds = new int[] { R.id.fileIcon, R.id.name,
				R.id.summary, R.id.special };
		newsAdaptInfo.listviewItemLayoutId = R.layout.image_item;

		researchAdapter = new ResearchAdapter(GBApplication.gbapp, adaptInfo,
				DetailsData.viewedNews);
		emptyview = LayoutInflater.from(GBApplication.gbapp).inflate(
				R.layout.empty_frame, null);
	}

	public static ResearchAdapter genResearchAdapter() {
		return new ResearchAdapter(GBApplication.gbapp, adaptInfo,
				DetailsData.viewedNews);
	}

	public static NewsAdapter genNewsAdapter() {
		return new NewsAdapter(GBApplication.gbapp, newsAdaptInfo,
				DetailsData.viewedNews);
	}
}
