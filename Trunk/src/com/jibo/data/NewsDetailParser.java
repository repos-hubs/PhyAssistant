package com.jibo.data;

import java.util.Comparator;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.app.DetailsData;
import com.jibo.base.src.EntityObj;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewsDetail;
import com.jibo.data.entity.NewsEntity;
import com.jibo.util.Logs;

public class NewsDetailParser extends SoapDataPaser {
	private String rescode = "";
	private String error = "";
	private NewsEntity newsEntity;
	private EntityObj entityDetail;
	public static int count = 0;

	public String getRescode() {
		return rescode;
	}

	public String getError() {
		return error;
	}

	public NewsEntity getNewsEntity() {
		return newsEntity;
	}

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub

		NewsDetail newsDetail = new NewsDetail();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result
				.getProperty(SoapRes.RESULT_REQ_ID_NEWS_DETAIL);
		Object propertyValue = null;
		try {
			propertyValue = resultList.getProperty("ID");
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.id = propertyValue.toString();
			}
			propertyValue = resultList.getProperty("Title");
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.title = propertyValue.toString();
			}
			propertyValue = resultList.getProperty("Date");
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.date = propertyValue.toString();
			}
			propertyValue = resultList.getProperty("Content");
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.content = propertyValue.toString();
			}
			propertyValue = resultList.getProperty("category");
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.category = propertyValue.toString();
			}
			propertyValue = resultList.getProperty("Source");
			propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
					.toString() : "";
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.source = propertyValue.toString();
			}
			propertyValue = resultList.getProperty("news_Source");
			propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
					.toString() : "";
			if (!"anyType{}".equals(propertyValue)) {
				newsDetail.newsSource = propertyValue.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			newsDetail = null;
		}
		if (newsDetail == null) {
			return;
		}
		EntityObj rt = new EntityObj();
		rt.fieldContents.put("id", newsDetail.id);

		int i = DetailsData.getEntities().lastIndexOf(rt);
		Logs.i(" title -- " + newsDetail.title+" "+count + " i " + i);
		try {
			if (i < 0) {
				return;
			}
			Logs.i(" title -- " + count + " i " + i + " --- ");
			count++;
			DetailsData.getEntities().get(i).fieldContents.put("newsDetail",
					newsDetail);
			entityDetail = DetailsData.getEntities().get(i);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public EntityObj getEntityDetail() {
		return entityDetail;
	}

	Comparator<NewsEntity> cpr = new Comparator<NewsEntity>() {

		@Override
		public int compare(NewsEntity object1, NewsEntity object2) {
			// TODO Auto-generated method stub
			boolean rlt = object1.getId().equals(object2.getId());
			return rlt ? 0 : object1.getId().compareTo(object2.getId());
		}
	};
}