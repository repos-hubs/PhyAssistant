package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.PackageInfoEntity;


public class PackageInfoPaser extends SoapDataPaser{
	private ArrayList<PackageInfoEntity> packageLst;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("getCustomer_DownAfterUpCategoryResult");
		System.out.println("detail    "+detail);
		packageLst = new ArrayList<PackageInfoEntity>();
		for(int i=0; i<detail.getPropertyCount(); i++) {
			PackageInfoEntity packageInfo = new PackageInfoEntity();
			SoapObject info = (SoapObject) detail.getProperty(i);
			packageInfo.setTitle(info.getProperty("title").toString());
			packageInfo.setType(info.getProperty("isType").toString());
			packageInfo.setFull_url(info.getProperty("fulldata").toString());
			packageInfo.setUpdate_url(info.getProperty("updatedata").toString());
			packageInfo.setDbName(info.getProperty("db_name").toString());
			packageInfo.setDataType(info.getProperty("data_type").toString());
			packageInfo.setVersion(info.getProperty("Version").toString());
			packageLst.add(packageInfo);
		}
	}

	public ArrayList<PackageInfoEntity> getPackageLst() {
		return packageLst;
	}

	public void setPackageLst(ArrayList<PackageInfoEntity> packageLst) {
		this.packageLst = packageLst;
	}
}
