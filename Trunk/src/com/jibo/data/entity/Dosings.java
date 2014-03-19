package com.jibo.data.entity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

public class Dosings {
	//Change part begin 
	private String LOG_TAG = "Dosings";
    private String METHOD_NAME = "GetDosings";
    private String SOAP_ACTION = "http://www.pda.com/pda/GetDosings";
    
    private String retrieveCond() {
    	return "drugId";
    }
    
    private String retrieveResult() {
    	return "GetDosingsResult";
    }
    
    public class dosingInfo {
    	int iRecordLength;
    	String[] productId; 
    	String[] manufacturerId;
    	String[] manufacturerNameEn;
    	String[] manufacturerNameCn;
    	String[] brandsCount;
    	dosingBrands[] dosingBrands;
    };
    
    public class dosingBrands {
    	dosingBrandInfo[] dosingBrandInfo;
    	specs[] specs;
    }

    public class dosingBrandInfo {
    	String manufacturerId;
    	String brandNameEn;
    	String brandNameCn;
    	String specsCount;
    }

    public class specs {
    	String[] spec;
    	String[] dosing;
    }

    public dosingInfo mDosingInfo;
    //Change part end

//    public void GetDosingsResult(String condition,int index) throws Exception {
//	   	
//		try {
//			SoapObject rpc = new SoapObject(commonfunction.NAMESPACE, METHOD_NAME);
//			rpc.addProperty(retrieveCond(), condition);
//
//			AndroidHttpTransport ht = new AndroidHttpTransport(commonfunction.URLDrug);
//			ht.debug = true;
//
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//					SoapEnvelope.VER11);
//			
//			envelope.bodyOut = rpc;
//			envelope.dotNet = true;
//			envelope.setOutputSoapObject(rpc);
//
//			ht.call(SOAP_ACTION, envelope);
//			
//			commonfunction.debug(LOG_TAG, "DUMP>> " + ht.requestDump);
//			commonfunction.debug(LOG_TAG, "DUMP<< " + ht.responseDump);
//
//			SoapObject result = (SoapObject) envelope.bodyIn;
//			SoapObject detail = (SoapObject) result.getProperty(retrieveResult());
//			
//			parseResult(detail,index);
//			return;
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//			throw e;
//		}
//	}

    private static final String INVALID_DATA            = "anyType{}";
    private static final String NULL_STRING             = "";

	private static final String FLAG_PRODUCT_ID           = "ProductID=";
	private static final String FLAG_MANUFACTURER_ID      = "ManufacturerID=";
	private static final String FLAG_MANUFACTURER_NAME_EN = "ManufacturerName_EN=";
	private static final String FLAG_MANUFACTURER_NAME_CN = "ManufacturerName_CN=";
	private static final String FLAG_BRANDS_COUNT         = "BrandsCount=";

	private static final String FLAG_BRAND_NAME_EN = "BrandName_EN=";
	private static final String FLAG_BRAND_NAME_CN = "BrandName_CN=";
	private static final String FLAG_SPECS_COUNT   = "SpecsCount=";
	private static final String FLAG_DOSING_SPEC   = "DosingSpec=";
	private static final String FLAG_SPECS         = "Spec=";
	private static final String FLAG_DOSING        = "Dosing=";

	private void parseResult(SoapObject detail,int index) {
		int brandsCount, specsCount;
		String data;

		dosingBrandInfo dosingBrandInfo;
		specs specs;

		mDosingInfo = new dosingInfo();
	    mDosingInfo.iRecordLength      = 0;
	    mDosingInfo.productId          = new String[500];
	    mDosingInfo.manufacturerId     = new String[500];
	    mDosingInfo.manufacturerNameEn = new String[500];
	    mDosingInfo.manufacturerNameCn = new String[500];
	    mDosingInfo.brandsCount        = new String[500];
	    mDosingInfo.dosingBrands       = new dosingBrands[500];

		do{
			try{
				data = detail.getProperty(mDosingInfo.iRecordLength).toString();

				mDosingInfo.productId[mDosingInfo.iRecordLength] = data.substring(
						data.indexOf(FLAG_PRODUCT_ID)+FLAG_PRODUCT_ID.length(), 
						data.indexOf("; ", data.indexOf(FLAG_PRODUCT_ID)));
				mDosingInfo.manufacturerId[mDosingInfo.iRecordLength] = data.substring(
						data.indexOf(FLAG_MANUFACTURER_ID)+FLAG_MANUFACTURER_ID.length(), 
						data.indexOf("; ", data.indexOf(FLAG_MANUFACTURER_ID)));
				mDosingInfo.manufacturerNameEn[mDosingInfo.iRecordLength] = data.substring(
						data.indexOf(FLAG_MANUFACTURER_NAME_EN)+FLAG_MANUFACTURER_NAME_EN.length(), 
						data.indexOf("; ", data.indexOf(FLAG_MANUFACTURER_NAME_EN)));
				mDosingInfo.manufacturerNameCn[mDosingInfo.iRecordLength] = data.substring(
						data.indexOf(FLAG_MANUFACTURER_NAME_CN)+FLAG_MANUFACTURER_NAME_CN.length(), 
						data.indexOf("; ", data.indexOf(FLAG_MANUFACTURER_NAME_CN)));
				mDosingInfo.brandsCount[mDosingInfo.iRecordLength] = data.substring(
						data.indexOf(FLAG_BRANDS_COUNT)+FLAG_BRANDS_COUNT.length(), 
						data.indexOf("; ", data.indexOf(FLAG_BRANDS_COUNT)));
				data = data.substring(data.indexOf(FLAG_BRANDS_COUNT)+FLAG_BRANDS_COUNT.length());

				if (mDosingInfo.productId[mDosingInfo.iRecordLength].equals(INVALID_DATA))
					mDosingInfo.productId[mDosingInfo.iRecordLength] = NULL_STRING;
				if (mDosingInfo.manufacturerId[mDosingInfo.iRecordLength].equals(INVALID_DATA))
					mDosingInfo.manufacturerId[mDosingInfo.iRecordLength] = NULL_STRING;
				if (mDosingInfo.manufacturerNameEn[mDosingInfo.iRecordLength].equals(INVALID_DATA))
					mDosingInfo.manufacturerNameEn[mDosingInfo.iRecordLength] = NULL_STRING;
				if (mDosingInfo.manufacturerNameCn[mDosingInfo.iRecordLength].equals(INVALID_DATA))
					mDosingInfo.manufacturerNameCn[mDosingInfo.iRecordLength] = NULL_STRING;
				if (mDosingInfo.brandsCount[mDosingInfo.iRecordLength].equals(INVALID_DATA))
					mDosingInfo.brandsCount[mDosingInfo.iRecordLength] = "0";

				brandsCount = new Integer(Integer.parseInt(mDosingInfo.brandsCount[mDosingInfo.iRecordLength]));

				mDosingInfo.dosingBrands[mDosingInfo.iRecordLength] = new dosingBrands();
				mDosingInfo.dosingBrands[mDosingInfo.iRecordLength].dosingBrandInfo = new dosingBrandInfo[brandsCount];
			    mDosingInfo.dosingBrands[mDosingInfo.iRecordLength].specs           = new specs[brandsCount];

			    for (int i=0; i<brandsCount; i++) {
			    	dosingBrandInfo = new dosingBrandInfo();
			    	dosingBrandInfo.manufacturerId = data.substring(
							data.indexOf(FLAG_MANUFACTURER_ID)+FLAG_MANUFACTURER_ID.length(), 
							data.indexOf("; ", data.indexOf(FLAG_MANUFACTURER_ID)));

			    	dosingBrandInfo.brandNameEn = data.substring(
							data.indexOf(FLAG_BRAND_NAME_EN)+FLAG_BRAND_NAME_EN.length(), 
							data.indexOf("; ", data.indexOf(FLAG_BRAND_NAME_EN)));

			    	dosingBrandInfo.brandNameCn = data.substring(
							data.indexOf(FLAG_BRAND_NAME_CN)+FLAG_BRAND_NAME_CN.length(), 
							data.indexOf("; ", data.indexOf(FLAG_BRAND_NAME_CN)));

			    	dosingBrandInfo.specsCount = data.substring(
							data.indexOf(FLAG_SPECS_COUNT)+FLAG_SPECS_COUNT.length(), 
							data.indexOf("; ", data.indexOf(FLAG_SPECS_COUNT)));

					if (dosingBrandInfo.manufacturerId.equals(INVALID_DATA))
						dosingBrandInfo.manufacturerId = NULL_STRING;
					if (dosingBrandInfo.brandNameEn.equals(INVALID_DATA))
						dosingBrandInfo.brandNameEn = NULL_STRING;
					if (dosingBrandInfo.brandNameCn.equals(INVALID_DATA))
						dosingBrandInfo.brandNameCn = NULL_STRING;
					if (dosingBrandInfo.specsCount.equals(INVALID_DATA))
						dosingBrandInfo.specsCount = "0";

					data = data.substring(data.indexOf(FLAG_SPECS_COUNT)+FLAG_SPECS_COUNT.length());

					specsCount   = new Integer(Integer.parseInt(dosingBrandInfo.specsCount));
			    	specs        = new specs();
			    	specs.spec   = new String[specsCount];
			    	specs.dosing = new String[specsCount];
					for (int j=0; j<specsCount; j++) {
						data = data.substring(data.indexOf(FLAG_DOSING_SPEC)+FLAG_DOSING_SPEC.length());

						specs.spec[j]   = data.substring(
								data.indexOf(FLAG_SPECS)+FLAG_SPECS.length(), 
								data.indexOf("; ", data.indexOf(FLAG_SPECS)));
						specs.dosing[j] = data.substring(
								data.indexOf(FLAG_DOSING)+FLAG_DOSING.length(), 
								data.indexOf("; ", data.indexOf(FLAG_DOSING)));
						if (specs.spec[j].equals(INVALID_DATA))
							specs.spec[j] = NULL_STRING;
						if (specs.dosing[j].equals(INVALID_DATA))
							specs.dosing[j] = NULL_STRING;

						data = data.substring(data.indexOf(FLAG_DOSING)+FLAG_DOSING.length());
					}

					mDosingInfo.dosingBrands[mDosingInfo.iRecordLength].dosingBrandInfo[i] = dosingBrandInfo;
				    mDosingInfo.dosingBrands[mDosingInfo.iRecordLength].specs[i]           = specs;
			    }
			}catch (Exception e){
				break;
			}
			mDosingInfo.iRecordLength++;
		}while(data != null);
	}

	public int getDosingsLength() {
		return mDosingInfo.iRecordLength;
	}

	public String[] getProductId() {
		return mDosingInfo.productId;
	}

	public String[] getManufacturerId() {
		return mDosingInfo.manufacturerId;
	}

	public String[] getManufacturerNameEn() {
		return mDosingInfo.manufacturerNameEn;
	}

	public String[] getManufacturerNameCn() {
		return mDosingInfo.manufacturerNameCn;
	}

	public String[] getBrandsCount() {
		return mDosingInfo.brandsCount;
	}

	public dosingBrands[] getDosingBrands() {
		return mDosingInfo.dosingBrands;
	}

	public dosingBrandInfo[] getDosingBrandInfo(int index) {
		return mDosingInfo.dosingBrands[index].dosingBrandInfo;
	}

	public String getManufacturerId(int i1, int i2) {
		return mDosingInfo.dosingBrands[i1].dosingBrandInfo[i2].manufacturerId;
	}

	public String getBrandNameEn(int i1, int i2) {
		return mDosingInfo.dosingBrands[i1].dosingBrandInfo[i2].brandNameEn;
	}

	public String getBrandNameCn(int i1, int i2) {
		return mDosingInfo.dosingBrands[i1].dosingBrandInfo[i2].brandNameCn;
	}

	public String getSpecsCount(int i1, int i2) {
		return mDosingInfo.dosingBrands[i1].dosingBrandInfo[i2].specsCount;
	}

	public specs[] getSpecs(int index) {
		return mDosingInfo.dosingBrands[index].specs;
	}

	public String[] getSpecsString(int i1, int i2) {
		return mDosingInfo.dosingBrands[i1].specs[i2].spec;
	}

	public String[] getDosing(int i1, int i2) {
		return mDosingInfo.dosingBrands[i1].specs[i2].dosing;
	}
}

