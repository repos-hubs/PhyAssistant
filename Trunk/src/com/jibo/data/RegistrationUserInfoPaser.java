package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * ÓÃ»§×¢²áresponse½âÎö
 * 
 * @author simon
 * 
 */
public class RegistrationUserInfoPaser extends SoapDataPaser {

 private boolean valid;
 private String errmsg;
 private String city;
 private String customerId;
 private String nullTag = "anyType{}";

 @Override
 public void paser(SoapSerializationEnvelope response) {

  SoapObject result = (SoapObject) response.bodyIn;
  SoapObject detail = (SoapObject) result
    .getProperty(SoapRes.RESULT_PROPERTY_REGISTRATION_USER_INFO);

  PropertyInfo propertyInfo = new PropertyInfo();

  for (int i = 0; i < detail.getPropertyCount(); i++) {
   SoapObject soapChilds = (SoapObject) detail.getProperty(i);
   if (soapChilds.toString().equals(nullTag))
    continue;
   detail.getPropertyInfo(i, propertyInfo);
   if (propertyInfo.name.equals("rescode")) {
    if (soapChilds.getProperty("rescode").toString().equals("200")) {
     this.valid = true;
//     city = soapChilds.getProperty("user_address")
//       .toString().equals(nullTag) ? "" : soapChilds
//       .getProperty("user_address").toString();
    } else {
     this.errmsg = soapChilds.getProperty("error").toString()
       .equals(nullTag) ? "" : soapChilds.getProperty(
       "error").toString();
     break;
    }
   } 
   else if (propertyInfo.name.equals("customerinfo")) {
    String strValue = soapChilds.getProperty("HospitalCity")
      .toString();
    if ("".equals(strValue) || strValue.equals(nullTag))
     strValue = "";
    this.city = strValue;
    String customerId = soapChilds.getProperty("customerId")
      .toString();
    if ("".equals(customerId) || customerId.equals(nullTag))
     strValue = "";
    this.customerId = customerId;
   }
  }
  bSuccess = true;  
 }

 public boolean isSuccess() {
  return valid;
 }

 public String getErrorMsg() {
  return errmsg;
 }

 public String getCity() {
  return city;
 }
 public String getCustId(){
  return customerId;
 }

}