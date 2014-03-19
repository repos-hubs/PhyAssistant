package com.jibo.data.entity;

import java.util.ArrayList;
/**显示医保药物目录相关数据
 * @author peter.pan
 * */
public class DrugReimbursementsEntity {
	public int iRecordLength;
	public ArrayList<String> productID;
	public ArrayList<String> national;
	public ArrayList<String> formulation;
	public ArrayList<String> reimbursementRate;
	public ArrayList<String> type;
	public ArrayList<String> categoryOfUse;
	public ArrayList<String> pediatricSpecific;
	public ArrayList<String> regional;
	public ArrayList<String> source;   
	public DrugReimbursementsEntity()
	{
		productID =new ArrayList<String>() ;
		national=new ArrayList<String>() ;
		formulation=new ArrayList<String>() ;
		reimbursementRate=new ArrayList<String>() ;
		type=new ArrayList<String>() ;
		categoryOfUse=new ArrayList<String>() ;
		pediatricSpecific=new ArrayList<String>() ;
		regional=new ArrayList<String>() ;
		source=new ArrayList<String>() ;   
	}
}
