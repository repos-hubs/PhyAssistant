package com.jibo.app.updatespot;

import java.util.Calendar;
import java.util.Date;

import com.jibo.GBApplication;
import com.jibo.util.SharedPreferenceUtil;

public class AllSpot extends UpdateSpot {

	public AllSpot(SoapOld soapInfo) {
		super(soapInfo);
		// TODO Auto-generated constructor stub
	}

	public AllSpot() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isupdateSpot() {
		// TODO Auto-generated method stub

		Object date = SharedPreferenceUtil.getValue(GBApplication.gbapp,
				"GBApp", "allpapersupdatedate", Long.class);
		if (date == null) {

		} else {
			Calendar day = Calendar.getInstance();
			day.setTime(new Date(Long.parseLong(date.toString())));
			Calendar today = Calendar.getInstance();
			if (today.get(Calendar.DAY_OF_YEAR) != day
					.get(Calendar.DAY_OF_YEAR)) {
				Object count = SharedPreferenceUtil.getValue(
						GBApplication.gbapp, "GBApp", "allpapersupdatecount",
						Long.class);
				if(asycResult==null||asycResult.size()==0){
					return false;
				}
				Long iCount = Long.parseLong(this.asycResult.get(
						"ReturnValue").toString());
				if (iCount > Long.parseLong(count.toString())) {
					SharedPreferenceUtil.putValue(GBApplication.gbapp, "GBApp",
							"allpapersupdatedate", System.currentTimeMillis());
					SharedPreferenceUtil.putValue(GBApplication.gbapp, "GBApp",
							"allpapersupdatecount", iCount);

					return true;
				}else if(iCount == Integer.parseInt(count.toString())){
					return false;
				}
			}

		}
		return false;
	}

}
