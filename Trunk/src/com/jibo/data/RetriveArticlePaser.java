package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.RetrieveArticleEntity;

public class RetriveArticlePaser extends SoapDataPaser {
	private ArrayList<RetrieveArticleEntity> articleList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		articleList = new ArrayList<RetrieveArticleEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result.getProperty("RetrieveArticlesByDoctorResult");
		for(int i=0; i<resultList.getPropertyCount(); i++) {
			SoapObject detail = (SoapObject) resultList.getProperty(i);
			RetrieveArticleEntity entity = new RetrieveArticleEntity();
			entity.setId(detail.getProperty("ID").toString().replace("anyType{}", ""));
			entity.setTitle(detail.getProperty("Title").toString().replace("anyType{}", ""));
			entity.setAuthors(detail.getProperty("Authors").toString().replace("anyType{}", ""));
			entity.setJournalName(detail.getProperty("JournalName").toString().replace("anyType{}", ""));
			articleList.add(entity);
		}
	}
	public ArrayList<RetrieveArticleEntity> getArticleList() {
		return articleList;
	}
	public void setArticleList(ArrayList<RetrieveArticleEntity> articleList) {
		this.articleList = articleList;
	}
}
