package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.CustomerInfoEntity;

public class CustomerInfoPaser extends SoapDataPaser {
	private CustomerInfoEntity entity;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("GetCustomerInfo_newResult");
		
		if("true".equals(detail.getProperty("valid").toString())) {
			entity = new CustomerInfoEntity();
			SoapObject resultInfo = (SoapObject) detail.getProperty("customerinfo");
			
			entity.setName(resultInfo.getProperty("UserName").toString());
			entity.setLicenseNumber(resultInfo.getProperty("LicenseNumber").toString());
			entity.setEmail(resultInfo.getProperty("Email").toString());
			entity.setContactNumber(resultInfo.getProperty("ContactNumber").toString());
			entity.setSchoolRegion(resultInfo.getProperty("SchoolRegion").toString());
			entity.setSchoolCity(resultInfo.getProperty("SchoolCity").toString());
			entity.setSchoolName(resultInfo.getProperty("SchoolName").toString());
			entity.setGraduateYear(resultInfo.getProperty("GraduationYear").toString());
			entity.setHospitalRegion(resultInfo.getProperty("HospitalRegion").toString());
			entity.setHospitalCity(resultInfo.getProperty("HospitalCity").toString());
			entity.setHospitalID(resultInfo.getProperty("HospitalID").toString());
			entity.setDepartment(resultInfo.getProperty("Department").toString());
			entity.setBirthdayYear(resultInfo.getProperty("BirthdayYear").toString());
			entity.setBirthdayMonth(resultInfo.getProperty("BirthdayMonthy").toString());
			entity.setBirthdayDay(resultInfo.getProperty("BirthdayDay").toString());
			entity.setSpecialty(resultInfo.getProperty("Specialty").toString());
			entity.setSubCategory(resultInfo.getProperty("Subcategory").toString());
			entity.setUsername(resultInfo.getProperty("gbUserName").toString());
			entity.setPassword(resultInfo.getProperty("gbPassword").toString());
			entity.setBigDepartment(resultInfo.getProperty("bigDepartments").toString());
			entity.setHospitalName(resultInfo.getProperty("hospitalName").toString());
			entity.setProfessionalTitle(resultInfo.getProperty("professional_title").toString());
			entity.setCustomerID(resultInfo.getProperty("customerId").toString());
			entity.setDoctorID(resultInfo.getProperty("doctorId").toString());
		}
	}
	public CustomerInfoEntity getEntity() {
		return entity;
	}
	public void setEntity(CustomerInfoEntity entity) {
		this.entity = entity;
	}
	
}
