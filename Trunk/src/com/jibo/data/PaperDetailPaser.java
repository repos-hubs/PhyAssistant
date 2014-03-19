package com.jibo.data;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.jibo.app.DetailsData;
import com.jibo.base.src.EntityObj;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.PaperDetailEntity;
import com.jibo.util.Logs;

/**
 * 论文详细信息
 * @description 
 * @author will
 * @create 2013-3-13 下午6:34:06
 */
public class PaperDetailPaser extends SoapDataPaser {
	public PaperDetailEntity entity;
	public static PaperDetailEntity OBJ;
	
	
//	@Override
//	public List<EntityObj> getObjs() {
//		return super.getObjs();
//	}


	@Override
	public void paser(SoapSerializationEnvelope response) {

		entity = new PaperDetailEntity();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_RESEARCH_DETAIL);

		String date;
		Object objDate;
		PropertyInfo propertyInfo;
		propertyInfo = new PropertyInfo();
		int i = 0;
		do {
			try {
				if(i>=detail.getPropertyCount()){
					break;
				}
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if(propertyInfo.name.equals("ReturnValue")) {
					SoapObject valueListObj = (SoapObject) detail.getProperty("ReturnValue");
					for(int j=0;j<valueListObj.getPropertyCount();j++) {
						valueListObj.getPropertyInfo(j, propertyInfo);
						objDate = valueListObj.getProperty(j);
						date = objDate==null?"":objDate.toString();
						if (date.equals("anyType{}"))
							date = "";

						if (propertyInfo.name.equals("Id")) {
							entity.id = date;
						} else if (propertyInfo.name.equals("Title")) {
							entity.title = date;
						} else if (propertyInfo.name.equals("JournalId")) {
							entity.journalID = date;
						} else if (propertyInfo.name.equals("PublicDate")) {
							entity.publicDate = date;
						} else if (propertyInfo.name.equals("KeyWords")) {
							SoapObject kwListObj = (SoapObject) valueListObj.getProperty("KeyWords");
							ArrayList<String> kwList = new ArrayList<String>();
							for(int k=0;k<kwListObj.getPropertyCount();k++) {
								String str = kwListObj.getProperty(k).toString();
								kwList.add(str);
							}
							entity.keyWords = kwList;
						} else if (propertyInfo.name.equals("TopRank")) {
							entity.topRank = date;
						} else if (propertyInfo.name.equals("Rank")) {
							entity.rank = date;
						} else if (propertyInfo.name.equals("IsFreeFullText")) {
							entity.isFreeFullText = Boolean.parseBoolean(date);
						} else if (propertyInfo.name.equals("Comments")) {
							entity.comments = date;
						} else if (propertyInfo.name.equals("CommentsType")) {
							entity.commentsType = date;
						} else if (propertyInfo.name.equals("VeiwedCount")) {
							entity.veiwedCount = date;
						} else if (propertyInfo.name.equals("Abstract")) {
							entity.abstarct = date;
						} else if (propertyInfo.name.equals("Authors")) {
							SoapObject authorsListObj = (SoapObject) valueListObj.getProperty("Authors");
							ArrayList<String> authorList = new ArrayList<String>();
							for(int k=0;k<authorsListObj.getPropertyCount();k++) {
								String str = authorsListObj.getProperty(k).toString();
								authorList.add(str);
							}
							entity.authors = authorList;
						} else if (propertyInfo.name.equals("SourceURL")) {
							entity.sourceURL = date;
						} else if (propertyInfo.name.equals("FreeFullTextURL")) {
							entity.freeFullTextURL = date;
						} else if (propertyInfo.name.equals("PdfUrl")) {
							entity.pdfURL = date;
						} else if (propertyInfo.name.equals("IF")) {
							entity.IF = date;
						} else if (propertyInfo.name.equals("Affiliations")) {
							SoapObject affiliationsListObj = (SoapObject) valueListObj.getProperty("Affiliations");
							ArrayList<String> affiList = new ArrayList<String>();
							for(int k=0;k<affiliationsListObj.getPropertyCount();k++) {
								String str = affiliationsListObj.getProperty(k).toString();
								affiList.add(str);
							}
							entity.affiliations = affiList;
						} else if (propertyInfo.name.equals("JournalName")) {
							entity.journalName = date;
						}  else if (propertyInfo.name.equals("JournalAbbrName")) {
							entity.journalAbbrName = date;
						}else if (propertyInfo.name.equals("CLC")) {
							SoapObject clcListObj = (SoapObject) valueListObj.getProperty("CLC");
							ArrayList<String> clcList = new ArrayList<String>();
							for(int k=0;k<clcListObj.getPropertyCount();k++) {
								String str = clcListObj.getProperty(k).toString();
								clcList.add(str);
							}
							entity.CLC = clcList;
						} else if (propertyInfo.name.equals("PublicationType")) {
							SoapObject listObj = (SoapObject) valueListObj.getProperty("PublicationType");
							ArrayList<String> list = new ArrayList<String>();
							for(int k=0;k<listObj.getPropertyCount();k++) {
								String str = listObj.getProperty(k).toString();
								list.add(str);
							}
							entity.pubTypes = list;
						} else if (propertyInfo.name.equals("DOI")) {
							entity.DOI = date;
						} else if (propertyInfo.name.equals("Language")) {
							entity.language = date;
						} else if (propertyInfo.name.equals("Mesh")) {
							SoapObject listObj = (SoapObject) valueListObj.getProperty("Mesh");
							ArrayList<String> list = new ArrayList<String>();
							for(int k=0;k<listObj.getPropertyCount();k++) {
								String str = listObj.getProperty(k).toString();
								list.add(str);
							}
							entity.mesh = list;
						} else if (propertyInfo.name.equals("Substances")) {
							SoapObject listObj = (SoapObject) valueListObj.getProperty("Substances");
							ArrayList<String> list = new ArrayList<String>();
							for(int k=0;k<listObj.getPropertyCount();k++) {
								String str = listObj.getProperty(k).toString();
								list.add(str);
							}
							entity.substances = list;
						}else if (propertyInfo.name.equals("Category")) {
							SoapObject listObj = (SoapObject) valueListObj.getProperty("Category");
							ArrayList<String> list = new ArrayList<String>();
							for(int k=0;k<listObj.getPropertyCount();k++) {
								String str = listObj.getProperty(k).toString();
								list.add(str);
							}
							entity.categorys = list;
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
		
//		EntityObj rt = new EntityObj();
//		try {rt.fieldContents.put("Id", entity.id);
//		List<EntityObj> ens = DetailsData.getEntities();
//		int m = ens.lastIndexOf(rt);
//		
//			Logs.i("== m "+m+" "+rt.fieldContents);
//			if (m < 0) {
//				return;
//			}
//			ens.get(m).fieldContents.put("newsDetail",
//					entity);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
