package com.jibo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.text.TextUtils;
import android.util.Log;

import com.jibo.base.src.EntityObj;
import com.jibo.base.src.EntityUtil;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewsEntity;
import com.jibo.util.Logs;

public class EntityObjPaser extends UpdateParser {
	int id;
	List<EntityObj> objs = new ArrayList<EntityObj>();
	boolean topImage;
	ArrayList<NewsEntity> ens = new ArrayList<NewsEntity>();
	String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<EntityObj> getObjs() {
		return objs;
	}

	public ArrayList<NewsEntity> getList() {
		return ens;
	}

	public EntityObjPaser(int id) {
		super();
		this.id = id;
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return super.getResult();
	}

	@Override
	public void setResult(Object result) {
		// TODO Auto-generated method stub
		super.setResult(result);
	}

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		ens.clear();
		SoapObject result = (SoapObject) response.bodyIn;

		String callbackName = null;
		String log = response.bodyOut.toString().replaceAll(" ", "");
		Logs.i("bodyout === " + log);
		setTag(log);

		switch (id) {
		case SoapRes.REQ_ID_GetUsersPeriodicalInfoByUserId:
			callbackName = "GetUsersPeriodicalInfoByUserIdResult";
			setResult(new HashMap());
			getList("ReturnValue",
					(SoapObject) result.getProperty(callbackName));
			return;
		case SoapRes.REQ_ID_GET_PAPER_LIST:
			callbackName = "GetPaperListResult";
			getList("ReturnValue",
					(SoapObject) result.getProperty(callbackName));
			Logs.i("--- tag " + this.getTag());
			return;
		case SoapRes.REQ_ID_GET_IMAGELIST_TOP:
			callbackName = "getNewStickResult";

			topImage = true;
			break;
		case SoapRes.REQ_ID_GET_IMAGELIST:
		case SoapRes.REQ_ID_GET_NEWS_TOP_MORE_BY_ID:
			callbackName = "getImageNewsListResult";
			break;
		}

		getNewsEntities((SoapObject) result.getProperty(callbackName));
	}

	private void getList(String listname, SoapObject resultList) {
		try {
			Object propertyValue = null;
			SoapObject userList = ((SoapObject) resultList
					.getProperty(listname));
			SoapObject inviteUserInfoDetail;
			NewsEntity entity = null;
			EntityObj en;
			for (int i = 0; i < userList.getPropertyCount(); i++) {
				en = new EntityObj();
				inviteUserInfoDetail = (SoapObject) userList.getProperty(i);
				en.setTag(inviteUserInfoDetail.getName());
				Logs.i(" === inviteUserInfoDetail.toString() "
						+ inviteUserInfoDetail.toString());
				String item = inviteUserInfoDetail.toString()
						.replaceAll("; ", ";").replaceFirst("anyType\\{", "")
						.replaceAll("\\}", "");
				Logs.i("--- item" + item);
				String[] items = item.split(";");
				String[] props;
				boolean skip = false;
				for (String prop : items) {
					Logs.i("--- prop1" + prop);
					props = prop.split("=");
					if (props.length > 1) {

						if (props[1].toLowerCase().contains("anytype")) {
							continue;
						} else if (props[0].equalsIgnoreCase("IF")) {
							try {
								Log.i("aaaa", props[1] + "aa");
								if (props[1].trim() != ""
										&& !TextUtils.isEmpty((String) props[1]
												.trim())) {
									// DecimalFormat df = new
									// DecimalFormat("#.##");
									// props[1] =
									// df.format(Double.parseDouble((String)
									// props[1]));
									props[1] = loadIF(props[1]);
								} else {
									props[1] = "";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						en.fieldContents.put(props[0], props[1]);

					}
				}
				if (!skip) {
					if (getResult() != null) {
						((Map) getResult()).put(en.get("JournalId")==null?"":en.get("JournalId").toString(),
								en.get("ViewedCount")==null?"":en.get("ViewedCount").toString());
					}
					objs.add(en);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String loadIF(String IF) {
		float x1 = 0.0f;
		try {
			x1 = Float.parseFloat(IF);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IF = String.format("%.2f", x1);
		return IF;
	}

	private void getNewsEntities(SoapObject resultList) {
		Object propertyValue = null;
		SoapObject userList = ((SoapObject) resultList
				.getProperty("newsSourceList"));
		SoapObject inviteUserInfoDetail;
		NewsEntity entity = null;
		try {
			for (int i = 0; i < userList.getPropertyCount(); i++) {
				inviteUserInfoDetail = (SoapObject) userList.getProperty(i);
				entity = new NewsEntity();
				EntityObj obj = new EntityObj();
				propertyValue = inviteUserInfoDetail.getProperty("Title");
				if (!"anyType{}".equals(propertyValue)) {
					entity.setTitle(propertyValue.toString());
					obj.fieldContents.put("Title", propertyValue.toString());
				}
				propertyValue = inviteUserInfoDetail.getProperty("ImgURL");
				if (!"anyType{}".equals(propertyValue)) {
					entity.imgUrl = propertyValue.toString();
					obj.fieldContents.put("Title", propertyValue.toString());
				}
				propertyValue = inviteUserInfoDetail.getProperty("newSummary");
				propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
						.toString() : "";
				entity.newSummary = propertyValue.toString();
				propertyValue = inviteUserInfoDetail.getProperty("ID");
				if (!"anyType{}".equals(propertyValue)) {
					entity.setId(propertyValue.toString());
				}
				propertyValue = inviteUserInfoDetail.getProperty("Date");
				propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
						.toString() : "";
				if (!"anyType{}".equals(propertyValue)) {
					entity.setDate(propertyValue.toString());
				}
				Logs.i("--- topImage " + topImage + " " + entity.getTitle());
				if (topImage) {
					entity.newStick = true;
				}

				propertyValue = inviteUserInfoDetail.getProperty("special");
				propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
						.toString() : "";
				entity.stickMsg = propertyValue.toString().toLowerCase();

				ens.add(entity);
				this.objs.add(EntityUtil.convert(entity));
			}
		} catch (Exception e) {
		}
	}
}
