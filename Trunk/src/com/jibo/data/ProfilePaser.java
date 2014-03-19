package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.ProfileEntity;

public class ProfilePaser extends SoapDataPaser {
	private ProfileEntity entity;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		entity = new ProfileEntity();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("GetProfileResult");
		
		System.out.println("detail   "+detail.toString());
		try {
			entity.setId(detail.getProperty("ID").toString());
			entity.setName(detail.getProperty("Name").toString());
			entity.setRankInChina(detail.getProperty("RankInChina").toString());
			entity.setRankInHospital(detail.getProperty("RankInHospital").toString());
			entity.setPaperCount(detail.getProperty("PapersCount").toString());
			entity.setCoauthorCount(detail.getProperty("CoauthorsCount").toString());
			entity.setCahsp(detail.getProperty("CAHsp").toString());
			entity.setCaohsp(detail.getProperty("CAOHsp").toString());
			entity.setKeywords(detail.getProperty("KWS").toString());
			entity.setCagrp(detail.getProperty("CAGRP").toString());
			entity.setBigSpecialty(detail.getProperty("BigSpecialty").toString());
			entity.setSpecialty(detail.getProperty("Specialty").toString());
			entity.setHospitalName(detail.getProperty("HospitalName").toString());
		} catch(Exception e) {
			entity = null;
			e.printStackTrace();
			return;
		}
		
	}
	public ProfileEntity getEntity() {
		return entity;
	}
	public void setEntity(ProfileEntity entity) {
		this.entity = entity;
	}
}
