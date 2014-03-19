package com.jibo.entity;

/**
 * 药品显示列表item，这里做封装，为了实现联系厂商信息和药品信息混排
 *
 */
public class DrugListItem {
	//联系厂商
	public ManufutureBrandInfo brandInfo;
	//药品信息
	public DrugInfo  drugInfo;
	//是否为药品
	public boolean isDrug;
	
	public DrugListItem(ManufutureBrandInfo brandInfo, DrugInfo drugInfo,
			boolean isDrug) {
		super();
		this.brandInfo = brandInfo;
		this.drugInfo = drugInfo;
		this.isDrug = isDrug;
	}
	
}
