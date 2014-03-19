package com.jibo.data.entity;

import com.jibo.common.Constant;
import com.jibo.data.entity.DosingInfoEntity.DosingBrandInfo;
import com.jibo.data.entity.DosingInfoEntity.DosingBrands;
import com.jibo.data.entity.DosingInfoEntity.Specs;

public class DosingData {
	public static final String TAG = "DosingData";
	


    public DosingInfoEntity mDosingInfo;

	private static Dosings mRetrieveDosings;

	public void FillDosingInfoData(String drugId, int page) throws Exception {
		boolean isEmpty = false;
		int length, brandsCount, specsCount;
		DosingInfoEntity.DosingBrandInfo dosingBrandInfo;
		DosingInfoEntity.Specs specs;

		mRetrieveDosings = new Dosings();
		//mRetrieveDosings.GetDosingsResult(drugId, page);

		length = mRetrieveDosings.getDosingsLength();
		if (length <= 0) {
			length  = 1;
			isEmpty = true;
		}

		mDosingInfo = new DosingInfoEntity();
		mDosingInfo.recordLength       = mRetrieveDosings.getDosingsLength();
		mDosingInfo.productId          = new String[length];
		mDosingInfo.manufacturerId     = new String[length];
		mDosingInfo.manufacturerNameEn = new String[length];
		mDosingInfo.manufacturerNameCn = new String[length];
		mDosingInfo.brandsCount        = new String[length];
		mDosingInfo.dosingBrands       = new DosingInfoEntity.DosingBrands[length];

		if (isEmpty) {
			mDosingInfo.productId[0]          = Constant.SPACE;
			mDosingInfo.manufacturerId[0]     = Constant.SPACE;
			mDosingInfo.manufacturerNameEn[0] = Constant.SPACE;
			mDosingInfo.manufacturerNameCn[0] = Constant.SPACE;
			mDosingInfo.brandsCount[0]        = Constant.SPACE;
			return;
		}

		for (int i=0; i<mDosingInfo.recordLength; i++) {
			mDosingInfo.productId[i]          = mRetrieveDosings.getProductId()[i];
			mDosingInfo.manufacturerId[i]     = mRetrieveDosings.getManufacturerId()[i];
			mDosingInfo.manufacturerNameEn[i] = mRetrieveDosings.getManufacturerNameEn()[i];
			mDosingInfo.manufacturerNameCn[i] = mRetrieveDosings.getManufacturerNameCn()[i];
			mDosingInfo.brandsCount[i]        = mRetrieveDosings.getBrandsCount()[i];

			brandsCount = new Integer(Integer.parseInt(mDosingInfo.brandsCount[i]));
			mDosingInfo.dosingBrands[i] = new DosingBrands();
			mDosingInfo.dosingBrands[i].dosingBrandInfo = new DosingBrandInfo[brandsCount];
			mDosingInfo.dosingBrands[i].specs           = new Specs[brandsCount];

			for (int j=0; j<brandsCount; j++) {
				dosingBrandInfo = new DosingBrandInfo();
				dosingBrandInfo.manufacturerId = mRetrieveDosings.getManufacturerId(i, j);
				dosingBrandInfo.brandNameEn    = mRetrieveDosings.getBrandNameEn(i, j);
				dosingBrandInfo.brandNameCn    = mRetrieveDosings.getBrandNameCn(i, j);
				dosingBrandInfo.specsCount     = mRetrieveDosings.getSpecsCount(i, j);

				specsCount   = new Integer(Integer.parseInt(dosingBrandInfo.specsCount));
				specs        = new Specs();
				specs.spec   = new String[specsCount];
				specs.dosing = new String[specsCount];
				for (int k=0; k<specsCount; k++) {
					specs.spec[k]   = mRetrieveDosings.getSpecsString(i, j)[k];
					specs.dosing[k] = mRetrieveDosings.getDosing(i, j)[k];
				}

				mDosingInfo.dosingBrands[i].dosingBrandInfo[j] = dosingBrandInfo;
				mDosingInfo.dosingBrands[i].specs[j]           = specs;
			}
		}
	}


	
}
