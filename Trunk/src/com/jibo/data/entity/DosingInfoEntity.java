package com.jibo.data.entity;

import java.util.ArrayList;
public class DosingInfoEntity {

//	public ArrayList<String> productId;
//	public ArrayList<String> manufacturerId;
//	public ArrayList<String> manufacturerNameEn;
//	public ArrayList<String> manufacturerNameCn;
//	public ArrayList<String> brandsCount;
//	public ArrayList<DosingBrands> dosingBrands;
//
//	public DosingInfoEntity()
//	{
//		productId =new ArrayList<String> ();
//		manufacturerId =new ArrayList<String> ();
//		manufacturerNameEn =new ArrayList<String> ();
//		manufacturerNameCn =new ArrayList<String> ();
//		brandsCount=new ArrayList<String> ();
//		dosingBrands =new ArrayList<DosingBrands> ();
//	}
	public int recordLength;
	public String[] productId; 
	public String[] manufacturerId;
	public String[] manufacturerNameEn;
	public String[] manufacturerNameCn;
	public String[] brandsCount;
	public DosingBrands[] dosingBrands;
	
	public static class DosingBrands {
		
		public DosingBrandInfo[] dosingBrandInfo;
    	public Specs[] specs;
//    	public DosingBrands()
//		{
//			dosingBrandInfo =new ArrayList<DosingBrandInfo> ();
//			specs =new ArrayList<Specs> ();
//		}
//		public ArrayList<DosingBrandInfo> dosingBrandInfo;
//		public ArrayList<Specs> specs;
	}

	public static class DosingBrandInfo {
		public String manufacturerId;
		public String brandNameEn;
		public String brandNameCn;
		public String specsCount;
	}

	public static class Specs {
		public String[] spec;
		public String[] dosing;
	}
}
