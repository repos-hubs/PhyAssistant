package com.jibo.data.entity;

import java.util.ArrayList;
/**��ʾ����ҩ��Ŀ¼�������
 * @author peter.pan
 * */
public class DrugEDLEntity {
	public int iRecordLength;
	public ArrayList<String> productID;
	public ArrayList<String> id;
	public ArrayList<String> national;
	public ArrayList<String> formulation;
	public ArrayList<String> regional;
	public ArrayList<String> categoryOfUse;
	public ArrayList<String> source;
	
	public DrugEDLEntity()
	{
		productID=new ArrayList<String> ();
		id=new ArrayList<String> ();
		national=new ArrayList<String> ();
		formulation=new ArrayList<String> ();
		regional=new ArrayList<String> ();
		categoryOfUse=new ArrayList<String> ();
		source=new ArrayList<String> ();
	}
}
