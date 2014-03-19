package com.jibo.update;

import com.api.android.GBApp.R;
import com.jibo.common.UpdatePackageResource;

public class UpdatePackageFactory {
	public static int getPckgName(String name) {
		int result = 0;
		if(UpdatePackageResource.FOLDER_DRUG.equals(name)) {
			result = R.string.drug;
		} else if(UpdatePackageResource.FOLDER_TOOL.equals(name)) {
			result = R.string.tool;
		}  else if(UpdatePackageResource.FOLDER_CALCULATOR.equals(name)) {
			result = R.string.calculator;
		}
		return result;
	}
	public static int getPackageID(String name) {
		int result = 0;
		if(UpdatePackageResource.FOLDER_DRUG.equals(name)) {
			result = UpdatePackageResource.PCKG_DRUG;
		} else if(UpdatePackageResource.FOLDER_TOOL.equals(name)) {
			result = UpdatePackageResource.PCKG_TOOL;
		} else if(UpdatePackageResource.FOLDER_CALCULATOR.equals(name)) {
			result = UpdatePackageResource.PCKG_CALCULATOR;
		}
		return result;
	}
	
	public static boolean updateData(String folderName, String path) {
		UpdatePackageInterface updateInterface = null;
		if(UpdatePackageResource.FOLDER_DRUG.equals(folderName)) {
			updateInterface = new UpdateDrugData();
		} else if(UpdatePackageResource.FOLDER_TOOL.equals(folderName)) {
			updateInterface = new UpdateToolData();
		} else if(UpdatePackageResource.FOLDER_CALCULATOR.equals(folderName)) {
			updateInterface = new UpdateCalculatorData();
		}
		
		return updateInterface.update(path);
	}
	
}
