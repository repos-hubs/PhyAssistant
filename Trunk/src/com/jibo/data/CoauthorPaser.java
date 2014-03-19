package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.CoauthorEntity;

public class CoauthorPaser extends SoapDataPaser{
	private ArrayList<CoauthorEntity> coauthorList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		coauthorList = new ArrayList<CoauthorEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("GetCoauthorsByDoctorResult");
		for(int i=0; i<detail.getPropertyCount(); i++) {
			SoapObject coauthorObject = (SoapObject) detail.getProperty(i);
			CoauthorEntity entity = new CoauthorEntity();
			entity.setDoctorID(coauthorObject.getProperty("DoctorID").toString());
			entity.setCustomerID(coauthorObject.getProperty("CoauthorID").toString());
			entity.setCoauthorName(coauthorObject.getProperty("CoauthorName").toString());
			entity.setHospitalName(coauthorObject.getProperty("HospitalName").toString());
			entity.setCoPaperCount(coauthorObject.getProperty("CopapersCount").toString());
			coauthorList.add(entity);
		}
	}
	public ArrayList<CoauthorEntity> getCoauthorList() {
		return coauthorList;
	}
	public void setCoauthorList(ArrayList<CoauthorEntity> coauthorList) {
		this.coauthorList = coauthorList;
	}
	
	
}
