package com.jibo.app.interact;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.content.Context;

import com.jibo.GBApplication;
import com.jibo.activity.BaseActivity;
import com.jibo.common.Constant;
import com.jibo.common.DeviceInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.GetgoogleParser;
import com.jibo.data.InteractionDrugProductPaser;
import com.jibo.data.InteractionFindPaser;
import com.jibo.data.InteractionPaser;
import com.jibo.data.InteractionRelationshipPaser;
import com.jibo.data.entity.InteractionEntity;
import com.jibo.data.entity.InteractionProductEntity;
import com.jibo.data.entity.InteractionRelationshipEntity;
import com.jibo.dbhelper.InteractAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;

public class InteractUpdate {
	private InteractAdapter dao;
	private BaseActivity ctx;
	public DeviceInfo isnetwork;
	private String localLanguage;
	private Context context;
	public InteractUpdate(BaseActivity activ,Context context) {
		super();
		this.ctx = activ;
		localLanguage = Locale.getDefault().getLanguage();
		if("en".equals(localLanguage)){
			localLanguage = "en-US";
		}else if("zh".equals(localLanguage)){
			localLanguage = "zh-CN";
		}
		dao = new InteractAdapter(context,Integer.parseInt(Constant.dbVersion));
		isnetwork = new DeviceInfo(context);
	}
	/*增量更新的顺序
	 * interactUpdate.checkDrugInteraction();
		interactUpdate.checkDrugInteractionRelationship();
		interactUpdate.checkDrugProduct();
	 */
	public void checkDrugInteraction(){
		String updatedStamp = dao.checkLastSyncTime("DrugInteraction");
		Properties propertyInfo = new Properties();
		int count = 100;
		String cultureInfo = localLanguage;
		propertyInfo.put(SoapRes.CULTUREINFO, cultureInfo);
		propertyInfo.put(SoapRes.COUNT, count);
		propertyInfo.put(SoapRes.LASTSYNCSTAMP, updatedStamp);
		ctx.sendRequest(SoapRes.URLINTERACTION,
				SoapRes.REQ_ID_GET_INTERACTION, propertyInfo,
				new BaseResponseHandler(ctx, false));
	}
	public void checkDrugInteractionRelationship(){
		String updatedStamp = dao.checkLastSyncTime("DrugInteractionRelationship");
		Properties propertyInfo = new Properties();
		int count = 100;
		propertyInfo.put(SoapRes.COUNT, count);
		propertyInfo.put(SoapRes.LASTSYNCSTAMP, updatedStamp);
		ctx.sendRequest(SoapRes.URLINTERACTION,
				SoapRes.REQ_ID_GET_INTERACTIONRELATIONSHIP, propertyInfo,
				new BaseResponseHandler(ctx, false));
	}
	
	public void checkDrugProduct(){
		String updatedStamp = dao.checkLastSyncTime("DrugProduct");
		Properties propertyInfo = new Properties();
		int count = 100;
		String cultureInfo = localLanguage;
		propertyInfo.put(SoapRes.CULTUREINFO, cultureInfo);
		propertyInfo.put(SoapRes.COUNT, count);
		propertyInfo.put(SoapRes.LASTSYNCSTAMP, updatedStamp);
		ctx.sendRequest(SoapRes.URLINTERACTION,
				SoapRes.REQ_ID_GET_DRUGPRODUCT, propertyInfo,
				new BaseResponseHandler(ctx, false));
	}
	public void update(Object o){
		 if (o instanceof InteractionPaser) {
				InteractionPaser codePaser = (InteractionPaser) o;
				String rescode = codePaser.getRescode();
				if ("200".equals(rescode)) {
					updateInteraction(codePaser);
				}else{
					checkDrugInteractionRelationship();
				}
			}else if (o instanceof InteractionDrugProductPaser) {
				InteractionDrugProductPaser codePaser = (InteractionDrugProductPaser) o;
				String rescode = codePaser.getRescode();
				if ("200".equals(rescode)) {
					updateInteractionDrugProduct(codePaser);
				}
			}else if (o instanceof InteractionRelationshipPaser) {
				InteractionRelationshipPaser codePaser = (InteractionRelationshipPaser) o;
				String rescode = codePaser.getRescode();
				if ("200".equals(rescode)) {
					updateInteractionRelationship(codePaser);
				}else{
					checkDrugProduct();
				}
			}
	}
	private void updateInteraction(InteractionPaser codePaser) {
		List<InteractionEntity> list = codePaser.getCoauthorList() ;
		int interactionPaserCount = list.size();
		Iterator it = list.iterator();
		while(it.hasNext()){
			InteractionEntity interactionEntity = (InteractionEntity) it.next();
			String table = "DrugInteraction";
			Map<String,String> wheres = new HashMap<String, String>();
			Map<String,String> wheres1 = new HashMap<String, String>();
			if("Normal".equals(interactionEntity.getStatus())){
				wheres1.put("id", interactionEntity.getKey());
				wheres.put("id", interactionEntity.getKey());
				wheres.put("Comments", interactionEntity.getComments());
				wheres.put("Description", interactionEntity.getDescription());
				wheres.put("Drug", interactionEntity.getDrug());
				wheres.put("CultureInfo", interactionEntity.getComments());
				wheres.put("LastUpdatedStamp", interactionEntity.getLastUpdatedStamp());
				wheres.put("Status", interactionEntity.getStatus());
				wheres.put("IID", interactionEntity.getIid());
				if(dao.selectData(table, wheres1)==0){
					//插入
					dao.insertData(table, wheres);
				}else{
					dao.updateData(table, wheres, wheres1);
				}
			}else{
				//删除
				wheres.put("Key",interactionEntity.getKey());
				dao.delData(table, wheres);
			}
		}
		if(isnetwork.isWifi()){
			if(interactionPaserCount>0){
				checkDrugInteraction();
			}
		}
	}

	private void updateInteractionDrugProduct(
			InteractionDrugProductPaser codePaser) {
		List<InteractionProductEntity> list = codePaser.getCoauthorList() ;
		int interactionDrugProductPaserCount = list.size();
		Iterator it = list.iterator();
		while(it.hasNext()){
			InteractionProductEntity interactionProductEntity = (InteractionProductEntity) it.next();
			String table = "DrugProduct";
			Map<String,String> wheres = new HashMap<String, String>();
			Map<String,String> wheres1 = new HashMap<String, String>();
			if("Normal".equals(interactionProductEntity.getStatus())){
				wheres1.put("id", interactionProductEntity.getKey());
				wheres.put("id", interactionProductEntity.getKey());
				wheres.put("Name", interactionProductEntity.getDrugName());
				wheres.put("CultureInfo", interactionProductEntity.getCultureInfo());
				wheres.put("LastUpdatedStamp", interactionProductEntity.getLastUpdatedStamp());
				wheres.put("Status", interactionProductEntity.getStatus());
				wheres.put("PID", interactionProductEntity.getPid());
				wheres.put("IsOTC", interactionProductEntity.getIsOTC());
				wheres.put("IsTCM", interactionProductEntity.getIsTCM());
				wheres.put("IsAHFS", interactionProductEntity.getIsAHFS());
				wheres.put("SaleRank", interactionProductEntity.getSaleRank());
				if(dao.selectData(table, wheres1)==0){
					//插入
					dao.insertData(table, wheres);
				}else{
					dao.updateData(table, wheres, wheres1);
				}
			}else{
				//删除
				wheres.put("Key",interactionProductEntity.getKey());
				dao.delData(table, wheres);
			}
		}
		if(interactionDrugProductPaserCount>0&&isnetwork.isWifi()){
			checkDrugProduct();
		}
	}

	private void updateInteractionRelationship(
			InteractionRelationshipPaser codePaser) {
		List<InteractionRelationshipEntity> list = codePaser.getCoauthorList() ;
		int interactionRelationshipPaserCount = list.size();
		Iterator it = list.iterator();
		while(it.hasNext()){
			InteractionRelationshipEntity interactionEntity = (InteractionRelationshipEntity) it.next();
			String table = "DrugInteractionRelationship";
			Map<String,String> wheres = new HashMap<String, String>();
			Map<String,String> wheres1 = new HashMap<String, String>();
			if("Normal".equals(interactionEntity.getStatus())){
				wheres1.put("id", interactionEntity.getKey());
				wheres.put("id", interactionEntity.getKey());
				wheres.put("PID", interactionEntity.getPid());
				wheres.put("IID", interactionEntity.getIid());
				wheres.put("Role", interactionEntity.getRole());
				wheres.put("LastUpdatedStamp", interactionEntity.getLastUpdatedStamp());
				wheres.put("Status", interactionEntity.getStatus());
				if(dao.selectData(table, wheres1)==0){
					//插入
					dao.insertData(table, wheres);
				}else{
					dao.updateData(table, wheres, wheres1);
				}
			}else{
				//删除
				wheres.put("Key",interactionEntity.getKey());
				dao.delData(table, wheres);
			}
		}
		if(isnetwork.isWifi()){
			if(interactionRelationshipPaserCount>0){
				checkDrugInteractionRelationship();
			}
		}
	}
}
