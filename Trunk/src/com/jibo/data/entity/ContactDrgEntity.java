package com.jibo.data.entity;

public class ContactDrgEntity {
private String drugName;
private String drugId;
private String drugPY;
private char pyChar;//Æ´ÒôÊ××ÖÄ¸
public String getDrugName() {
	return drugName;
}
public void setDrugName(String drugName) {
	this.drugName = drugName;
}
public String getDrugId() {
	return drugId;
}
public void setDrugId(String drugId) {
	this.drugId = drugId;
}
public String getDrugPY() {
	return drugPY;
}
public void setDrugPY(String drugPY) {
	this.drugPY = drugPY;
	this.setPyChar(drugPY.charAt(0));
}
public char getPyChar() {
	return pyChar;
}
public void setPyChar(char pyChar) {
	this.pyChar = pyChar;
}
}