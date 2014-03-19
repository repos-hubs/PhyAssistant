package com.jibo.data;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.base.src.EntityObj;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.SpecialCollectionListEntity;

public class SpecialCollectionPaser extends SoapDataPaser{
	
	public ArrayList<SpecialCollectionListEntity> list;
	
	@Override
	public List<EntityObj> getObjs() {
		return super.getObjs();
	}


	@Override
	public void paser(SoapSerializationEnvelope response) {

		list = new ArrayList<SpecialCollectionListEntity>();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_SPECIAL_COLLECTION);

		String date;
		PropertyInfo propertyInfo;
		propertyInfo = new PropertyInfo();
		int i = 0;
		do {
			try {
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if (date.equals("anyType{}"))
					date = "";
				if(propertyInfo.name.equals("ReturnValue")){
					SoapObject kwListObj = (SoapObject) detail.getProperty("ReturnValue");
//					SoapObject soapChilds = (SoapObject) detail.getProperty(i);
					for(int m=0; m<kwListObj.getPropertyCount(); m++){
						kwListObj.getPropertyInfo(m, propertyInfo);
						
						if(propertyInfo.name.equals("SpecialAlbum")){
							SoapObject kwListObj1 = (SoapObject) kwListObj.getProperty(m);
							SpecialCollectionListEntity entity = new SpecialCollectionListEntity();
//							SoapObject soapChilds1 = (SoapObject) soapChilds.getProperty(m);
							for(int k=0;k<kwListObj1.getPropertyCount();k++) {
								kwListObj1.getPropertyInfo(k, propertyInfo);
								String strValue = kwListObj1.getProperty(k).toString();
								if ("".equals(strValue) || strValue.equals("anyType{}"))
									continue;

								if (propertyInfo.name.equals("Key")) {
									entity.key = strValue;
								} else if (propertyInfo.name.equals("CompanyName")) {
									entity.companyName = strValue;
								}else if (propertyInfo.name.equals("Name")) {
									entity.name = strValue;
								}else if (propertyInfo.name.equals("IconURL")) {
									entity.iconUrl = strValue;
								}else if (propertyInfo.name.equals("DownloadLink")) {
									entity.downloadLink = strValue;
								}else if (propertyInfo.name.equals("ActivatedStamp")) {
									entity.activeStamp = strValue;
								}else if (propertyInfo.name.equals("InvalidStamp")) {
									entity.invalidStamp = strValue;
								}
							}
							list.add(entity);
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (date != null);
		bSuccess = true;
	}
}
