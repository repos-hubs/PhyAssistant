package com.jibo.app.updatespot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jibo.app.updatespot.UpdateSpot.SoapOld;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.SoapRes;

import android.view.View;

public class SpotUtil {
	public static final String RESEARCH_SPOT = "research_spot";
	public static final String LASTEST_SPOT = "lastest_spot";
	public static final String COLLECTION_SPOT = "collectionnspot";
	public static final String SUBSCRIBED_JOURNALS = "subscribedJournals";
	public static Map<String, UpdateSpot> spots = new HashMap<String, UpdateSpot>();
	static {
		spots.put(
				SUBSCRIBED_JOURNALS,
				new JournalSpot(new SoapOld(new String[] { "sign", "userId",
						"updateTime" }, new String[] {
						"",/*
							 * \"*:*\"
							 */
						"?",
						new java.text.SimpleDateFormat("yyyy-MM-dd")
								.format(new Date()) },
						SoapRes.REQ_ID_GetUsersPeriodicalInfoByUserId,
						SoapRes.URLGETSUNSCRIPTIONS)));
		spots.put(LASTEST_SPOT, new AllSpot(
				new SoapOld(new String[] { "lastUpdatedTime", "sign" },
						new String[] { "2012-11-11", "" },
						SoapRes.REQ_ID_UPDATE_PAPERS_COUNT,
						SoapRes.UPDATE_PAPERS_COUNT)));
	}

}
