package com.jibo.app.invite;

import java.util.List;

import android.content.Context;

import com.jibo.util.SharedPreferenceUtil;

public class Data {
	public static List<ContactInfo> info;
	public static boolean inviting = false; 
	public static List<ContactInfo> cInfos;
	public static String username;
	public static String keyProfessionalDate = "professionalDate";
	public static String getUsername(Context ctx){
		if(username==null){
			SharedPreferenceUtil.getValue(
				ctx.getApplicationContext(), "GBAPP", "USER_NAME", String.class).toString();
		}
		return username;
	}
}
