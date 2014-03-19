package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class NewsCountPaser extends SoapDataPaser {
 private String newsCount;
 @Override
 public void paser(SoapSerializationEnvelope response) {
  SoapObject result = (SoapObject) response.bodyIn;
  Object detail = result.getProperty("getNewsCountResult");
  newsCount = detail.toString();
 }
 public String getNewsCount() {
  return newsCount;
 }
 public void setNewsCount(String newsCount) {
  this.newsCount = newsCount;
 }
}