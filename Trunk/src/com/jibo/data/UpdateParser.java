package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.base.src.EntityObj;

public class UpdateParser extends SoapDataPaser {

	public Object result;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		SoapObject rt = (SoapObject) response.bodyIn;

		String callbackName = null;
		callbackName = "GetPaperUpdatedCountResult";
		SoapObject obj = ((SoapObject) rt.getProperty(callbackName));
		EntityObj ej = new EntityObj();
		String s = obj.toString().substring(8,obj.toString().lastIndexOf(";"));
		String[] as = s.split(";");
		for (int i = 0; i < as.length; i++) {
			String[] eqAs = as[i].split("=");
			ej.fieldContents.put(eqAs[0].trim(), eqAs[1].trim());
		}
		result = ej.fieldContents;
	}

}
