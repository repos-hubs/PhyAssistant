package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.activity.MarketActivity;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.MarketPackageEntity;

public class MarketPaserAdapter extends SoapDataPaser {
	private ArrayList<MarketPackageEntity> marketAssociateEntityList;
	private ArrayList<MarketPackageEntity> marketUnAssociateEntityList;
	private ArrayList<MarketPackageEntity> mineEntityList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		marketAssociateEntityList = new ArrayList<MarketPackageEntity>();
		marketUnAssociateEntityList = new ArrayList<MarketPackageEntity>();
		mineEntityList = new ArrayList<MarketPackageEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result.getProperty("GetCategoryInfoResult");
		System.out.println("resultList     "+resultList.getPropertyCount());
		String propertyValue = "";
		String type = "";
		String dpt = "";
		String productid = "";
		String category = "";
		String sort = "";
		String productId = "";
		String dataInfo = MarketActivity.dataInfo;
		
		for(int i=0; i<resultList.getPropertyCount(); i++) {
			SoapObject marketObject = (SoapObject) resultList.getProperty(i);
			
			if(marketObject.getProperty("Url")!=null&&!marketObject.getProperty("Url").toString().equalsIgnoreCase("Null")) {
				MarketPackageEntity entity = new MarketPackageEntity();
				propertyValue = marketObject.getProperty("Title").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setTitle(propertyValue);
				} else {
					entity.setTitle("");
				}
					
				propertyValue = marketObject.getProperty("Url").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setUrl(propertyValue);
				} else {
					entity.setUrl("");
				}
				
				propertyValue = marketObject.getProperty("category").toString();
				category = propertyValue;
				if(!"anyType{}".equals(propertyValue)) {
					entity.setCategory(propertyValue);
				} else {
					entity.setCategory("");
				}
				
				propertyValue = marketObject.getProperty("productid").toString();
				productid = propertyValue;
				if(!"anyType{}".equals(propertyValue)) {
					entity.setProductID(propertyValue);
				} else {
					entity.setProductID("");
				}
				
				//TODO
				propertyValue = marketObject.getProperty("sort").toString();
				sort = propertyValue;
				if(!"anyType{}".equals(propertyValue)) {
					entity.setVersion(propertyValue);
				} else {
					entity.setVersion("");
				}
				
				propertyValue = marketObject.getProperty("department").toString();
				dpt = propertyValue;
				if(!"anyType{}".equals(propertyValue)) {
					entity.setDepartment(propertyValue);
				} else {
					entity.setDepartment("");
				}
					
				propertyValue = marketObject.getProperty("Format").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setFormat(propertyValue);
				} else {
					entity.setFormat("");
				}
				
				propertyValue = marketObject.getProperty("Icon").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setIcon(propertyValue);
				} else {
					entity.setIcon("");
				}
				
				propertyValue = marketObject.getProperty("Type").toString();
				type = propertyValue;
				if(!"anyType{}".equals(propertyValue)) {
					entity.setType(propertyValue);
				} else {
					entity.setType("");
				}
				
				propertyValue = marketObject.getProperty("Intro").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setIntro(propertyValue);
				} else {
					entity.setIntro("");
				}
				
				propertyValue = marketObject.getProperty("permission").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setPermission(propertyValue);
				} else {
					entity.setPermission("");
				}
				
				propertyValue = marketObject.getProperty("imgs").toString();
				if(!"anyType{}".equals(propertyValue)) {
					SoapObject imgListObj = (SoapObject) marketObject.getProperty("imgs");
					ArrayList<String> imgList = new ArrayList<String>();
					for(int j=0;j<imgListObj.getPropertyCount();j++) {
						String imgUrl = imgListObj.getProperty(j).toString();
						imgList.add(imgUrl);
					}
					entity.setImgList(imgList);
				} else {
					entity.setImgList(null);
				}
				
				propertyValue = marketObject.getProperty("Rate").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setRates(propertyValue);
				} else {
					entity.setRates("");
				}
				
				propertyValue = marketObject.getProperty("Downloadcount").toString();
				if(!"anyType{}".equals(propertyValue)) {
					entity.setDownloadCount(propertyValue);
				} else {
					entity.setDownloadCount("");
				}
				ArrayList<String> dptList = new ArrayList<String>();
				String dptArr[] = dpt.split(",");
				for(String str:dptArr) {
					dptList.add(str);
				}
//				if(type.equals("diff")) {
//					mineEntityList.add(entity);
//				} else{
//					if(dptList.contains(SharedPreferencesMgr.getDept())||dpt.equals("all")) {
//						marketAssociateEntityList.add(entity);
//					} else {
//						marketUnAssociateEntityList.add(entity);
//					}
//				}
				productId = category.trim() + "-" + productid.trim() + "-" + sort.trim();
				if(dataInfo != null && !dataInfo.equals("")){
					boolean isInstall = false;
					String[] dataInfos = dataInfo.split("\\|");
					for(int j=0; j<dataInfos.length; j++){
						if(dataInfos[j].equals(productId)){
							mineEntityList.add(entity);
							isInstall = true;
							break;
						}
					}
					if(!isInstall){
						if(dptList.contains(SharedPreferencesMgr.getDept())||dpt.equals("all")) {
							marketAssociateEntityList.add(entity);
						} else {
							marketUnAssociateEntityList.add(entity);
						}
					}
					
				}
				
			}
		}
	}
	public ArrayList<MarketPackageEntity> getMarketAssociateEntityList() {
		return marketAssociateEntityList;
	}
	public void setMarketAssociateEntityList(
			ArrayList<MarketPackageEntity> marketAssociateEntityList) {
		this.marketAssociateEntityList = marketAssociateEntityList;
	}
	public ArrayList<MarketPackageEntity> getMarketUnAssociateEntityList() {
		return marketUnAssociateEntityList;
	}
	public void setMarketUnAssociateEntityList(
			ArrayList<MarketPackageEntity> marketUnAssociateEntityList) {
		this.marketUnAssociateEntityList = marketUnAssociateEntityList;
	}
	public ArrayList<MarketPackageEntity> getMineEntityList() {
		return mineEntityList;
	}
	public void setMineEntityList(ArrayList<MarketPackageEntity> mineEntityList) {
		this.mineEntityList = mineEntityList;
	}
	
}
