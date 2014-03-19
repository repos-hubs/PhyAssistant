package com.jibo.app.updatespot;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.jibo.GBApplication;
import com.jibo.util.SharedPreferenceUtil;

public class JournalSpot extends UpdateSpot {
	public JournalSpot(SoapOld soapInfo) {
		super(soapInfo);
		// TODO Auto-generated constructor stub
	}

	public JournalSpot() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isupdateSpot() {
		// TODO Auto-generated method stub
		Object date = SharedPreferenceUtil.getValue(GBApplication.gbapp,
				"GBApp", "subsc_papersupdatedate", Long.class);
		if (date == null) {

		} else {
			Calendar day = Calendar.getInstance();
			day.setTime(new Date(Long.parseLong(date.toString())));
			Calendar today = Calendar.getInstance();
			if (today.get(Calendar.DAY_OF_YEAR) != day
					.get(Calendar.DAY_OF_YEAR)) {
				Object count = SharedPreferenceUtil.getValue(
						GBApplication.gbapp, "GBApp",
						"subsc_papersupdatecount", Long.class);
				long sum = 0;
				if(asycResult==null||asycResult.size()==0){
					return false;
				}
				for (Object c : this.asycResult.values()) {
					if (c == null)
						continue;
					sum += Long.parseLong(c.toString());
				}

				Long iCount = sum;
				if (iCount > Long.parseLong(count.toString())) {
					SharedPreferenceUtil.putValue(GBApplication.gbapp, "GBApp",
							"subsc_papersupdatedate",
							System.currentTimeMillis());
					SharedPreferenceUtil.putValue(GBApplication.gbapp, "GBApp",
							"subsc_papersupdatecount", iCount);

					return true;
				} else if (iCount == Integer.parseInt(count.toString())) {
					return false;
				}
			}

		}
		return false;

	}
}
