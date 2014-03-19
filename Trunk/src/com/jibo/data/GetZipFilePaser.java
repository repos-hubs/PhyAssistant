package com.jibo.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class GetZipFilePaser extends SoapDataPaser {
	private String url = "";
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		Pattern p = Pattern.compile("http[^;]+");
		Matcher matcher = p.matcher(result.toString());
		System.out.println("result   "+result);
		if(matcher.find()) {
			url = matcher.group();
		}
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
