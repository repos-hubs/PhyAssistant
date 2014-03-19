package com.jibo.data.entity;
/**
 * 药物相互作用entity
 * @author peter.pan
 * */
public class DrugInteractionEntity {
	public int recordLength;
	/**产品Ids*/
	public String[] productId;
	/**制作厂商Ids*/
	public String[] manufacturerId;
	/**制作厂商英文名*/
	public String[] manufacturerNameEn;
	/**制作厂商英文名*/
	public String[] manufacturerNameCn;
	public String[] interaction;
}
