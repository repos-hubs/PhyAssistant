package com.jibo.data.entity;

import java.util.ArrayList;

public class AcademicEntity {
	public int iRecordLength;
	public ArrayList<String> id;
	public ArrayList<String> productID;
	public ArrayList<String> title;
	public ArrayList<String> authors;
	public ArrayList<String> journalName;
	public AcademicEntity()
	{
		id =new ArrayList<String>();
		productID =new ArrayList<String>();
		title =new ArrayList<String>();
		authors =new ArrayList<String>();
		journalName =new ArrayList<String>();
	}
}
