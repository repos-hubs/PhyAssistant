package com.jibo.data.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jibo.common.Constant;


/**
 * ASHF bean
 * @author peter.pan
 *
 */
public class AshfEntity implements android.os.Parcelable{
	
	public String productId = "";
	public String generalNameCn ="";
	public String generalNameEn ="";
	public String brandName = "";
	/**分类*/
	public String categray;
	/**剂型*/
	public String routes;
	/**给药途径*/
	public String forms;
	/**适应症*/
	public String indication ;
	public String indication_pre ;
	/**禁忌症*/
	public String contraindication;
	/**用法用量 */
	public String dosage;
	public String dosage_pre;
	/**用法用量 成人*/
	public String dosageAdults;
	/**用法用量 儿童*/
	public String dosagePediatric;
	/**用法用量 特殊*/
	public String dosageSpecial;
	
	/**不良反应*/
	public  String ADR;

	/**相互作用*/
	public String interactions;
	public String interactions_pre;
	/**特殊人群*/
	public String spefipop;
	public String spefipop_pre;
	/**注意事项*/
	public String caution;
	public String caution_pre;
	
	
	public static final android.os.Parcelable.Creator<AshfEntity> CREATOR = new Creator<AshfEntity>() {
		public AshfEntity createFromParcel(android.os.Parcel source) {
			AshfEntity entity = new AshfEntity();
			entity.productId = source.readString();
			entity.generalNameCn =source.readString();
			entity.generalNameEn =source.readString();
			entity.brandName = source.readString();
			entity.categray= source.readString();
			entity.routes= source.readString();
			entity.forms= source.readString();
			entity.indication = source.readString();
			entity.indication_pre = source.readString();
			entity.contraindication= source.readString();
			entity.dosage= source.readString();
			entity.dosage_pre= source.readString();
			entity.dosageAdults= source.readString();
			entity.dosagePediatric= source.readString();
			entity.dosageSpecial= source.readString();
			entity.ADR= source.readString();
			entity.interactions= source.readString();
			entity.interactions_pre= source.readString();
			entity.spefipop= source.readString();
			entity.spefipop_pre= source.readString();
			entity.caution= source.readString();
			entity.caution_pre= source.readString();
			return entity;
		}

		public AshfEntity[] newArray(int size) {
			return new AshfEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(android.os.Parcel dest, int flags) {

		dest.writeString(productId);
		dest.writeString(generalNameCn);
		dest.writeString(generalNameEn);
		dest.writeString(brandName);			
		dest.writeString(categray);
		dest.writeString(routes);
		dest.writeString(forms);
		dest.writeString(indication);	
		dest.writeString(indication_pre);			
		dest.writeString(contraindication);
		dest.writeString(dosage);
		dest.writeString(dosage_pre);
		dest.writeString(dosageAdults);
		dest.writeString(dosagePediatric);
		dest.writeString(dosageSpecial);
		dest.writeString(ADR);
		dest.writeString(interactions);		
		dest.writeString(interactions_pre);	
		dest.writeString(spefipop);	
		dest.writeString(spefipop_pre);	
		dest.writeString(caution);	
		dest.writeString(caution_pre);	
	}
	
	
	public void paserJson(String s)
	{
		if(s!=null)
		{			
			try {
				JSONObject obj=new JSONObject(s);
				brandName =obj.optString("brands", "");
				categray=obj.optString("introduction", "") ;				
				routes=obj.optString("routes", "");
				forms=obj.optString("forms","");
				indication =obj.optString("uses", "");
				indication_pre =obj.optString("uses_pre", "");
				contraindication=obj.optString("contraindications", "");
				dosage=obj.optString("dosage", "");
				dosage_pre=obj.optString("dosage_pre", "");
				dosageAdults =obj.optString("adults", "");
				dosagePediatric="";
				dosageSpecial=obj.optString("popuHisimple", "");
				ADR=obj.optString("ad", "");
				interactions=obj.optString("interactions", "")	;
				interactions_pre=obj.optString("interactions_pre", "")	;
				spefipop=obj.optString("spefipop", "")	;
				spefipop_pre=obj.optString("spefipop_pre", "")	;
				caution=obj.optString("cautions", "")	;
				caution_pre=obj.optString("cautions_pre", "")	;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
