package com.jibo.data.entity;

import java.io.Serializable;

import com.jibo.common.Constant;
/**药品基本信息数据
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class SimpleDrugInfoEntity implements Serializable{
	
	public String id=Constant.SPACE;
	/**中文名*/
	public String nameEn=Constant.SPACE;
	/**中文名*/
	public String nameCn=Constant.SPACE;
	public String atc=Constant.SPACE;
	/**品牌名*/
	public String brandName=Constant.SPACE;
	
	public int flag;//0：药品；1：品牌
}
