package com.jibo.data.entity;

public class CoauthorEntity {
	private String doctorID;
	private String customerID;
	private String coauthorName;
	private String hospitalName;
	private String coPaperCount;
	public String getCoauthorName() {
		return coauthorName;
	}
	public void setCoauthorName(String coauthorName) {
		this.coauthorName = coauthorName;
	}
	public String getDoctorID() {
		return doctorID;
	}
	public void setDoctorID(String doctorID) {
		this.doctorID = doctorID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getCoPaperCount() {
		return coPaperCount;
	}
	public void setCoPaperCount(String coPaperCount) {
		this.coPaperCount = coPaperCount;
	}
}
