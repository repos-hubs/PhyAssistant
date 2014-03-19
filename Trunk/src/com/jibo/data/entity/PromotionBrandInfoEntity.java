package com.jibo.data.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jibo.common.Constant;


/**
 * ÍÆ¹ãÆ·ÅÆÊµÌåbean
 * @author simon
 *
 */
public class PromotionBrandInfoEntity{
	
	public String brandId;
	public String productId = "";
	public String generalName = "";
	public String zyName = "";
	public String enName = "";
	public String pyName = "";
	public String indication = "";
	public String contraindication = "";
	public String brandName = "";
	public String brandNameEn = "";
	public String mncId = "";
	public String otherInfo = "";
	
	
	public  String pediatric_use ;
	public  String geriatric_use ;
	public  String pregnancydesc ;
	public  String formulationCn;
	public  String dosing;
	public  String overDosage;
	public  ArrayList<String> specList;
	public  String manufacturerNameCn;
	public  String manufacturerNameEn;
	
	/**²»Á¼·´Ó¦*/
	public  String ADR;
	/**Ïà»¥×÷ÓÃ*/
	public  String drugInteraction;
	public void paserJson()
	{
		if(otherInfo!=null)
		{
			
			try {
				JSONObject obj=new JSONObject(otherInfo);
				pediatric_use=obj.optString("pediatricuse", Constant.SPACE);
				geriatric_use=obj.optString("geriatricuse", Constant.SPACE);
				pregnancydesc=obj.optString("pregnantwomen", Constant.SPACE);
				formulationCn=obj.optString("formulation_cn", Constant.SPACE);
				manufacturerNameCn=obj.optString("manufacturer_name_cn", Constant.SPACE);
				manufacturerNameEn=obj.optString("manufacturer_name_en", Constant.SPACE);
				dosing=obj.optString("dosing", Constant.SPACE);
				ADR=obj.optString("adr", Constant.SPACE);
				drugInteraction=obj.optString("drug_interaction", Constant.SPACE);
				overDosage =obj.optString("overdosage", Constant.SPACE);
				specList=new ArrayList<String> ();
				String s =obj.optString("spec1", null);
				if(!s.equals("null"))
					specList.add(s);
				s =obj.optString("spec2", null);
				if(!s.equals("null"))
					specList.add(s);
				s =obj.optString("spec3", null);
				if(!s.equals("null"))
					specList.add(s);
				s =obj.optString("spec4", null);
				if(!s.equals("null"))
					specList.add(s);
				s =obj.optString("spec5", null);
				if(!s.equals("null"))
					specList.add(s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
