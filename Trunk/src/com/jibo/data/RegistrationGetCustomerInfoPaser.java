package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.RegistrationEntity;



/**
 * 获取用户注册信息
 * @author simon
 *
 */
public class RegistrationGetCustomerInfoPaser extends SoapDataPaser {

	private RegistrationEntity entity;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		entity= new RegistrationEntity();
		
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_REGISTARTIONS_DATA);
		
		String date;
    	PropertyInfo propertyInfo;
    	
    	propertyInfo = new PropertyInfo();
    	int i = 0;
    	do {
    		try{
    			detail.getPropertyInfo(i, propertyInfo);
    			date = detail.getProperty(i).toString();
    			if(date.equals("anyType{}"))
					date = "";
    			
    			if(propertyInfo.name.equals("MachineID"))
    			{
    				entity.setMachineId(date);
    			}else if(propertyInfo.name.equals("UserName")){
    				entity.setName(date);
    			}else if(propertyInfo.name.equals("LicenseNumber")){
    				entity.setLicenseNumber(date);
    			}else if(propertyInfo.name.equals("Email")){
    				entity.seteMail(date);
    			}else if(propertyInfo.name.equals("ContactNumber")){
    				entity.setContactNumber(date);
    			}else if(propertyInfo.name.equals("SchoolRegion")){
    				entity.setMedicalSchoolRegion(date);
    			}else if(propertyInfo.name.equals("SchoolCity")){
    				entity.setMedicalSchool(date);
    			}else if(propertyInfo.name.equals("SchoolName")){
    				entity.setMedicalSchool(date);
    			}else if(propertyInfo.name.equals("GraduationYear")){
    				entity.setYearOfGarduation(date);
    			}else if(propertyInfo.name.equals("HospitalRegion")){
    				entity.setHospitalRegion(date);
    			}else if(propertyInfo.name.equals("HospitalCity")){
    				entity.setHospitalCity(date);
    			}else if(propertyInfo.name.equals("HospitalID")){
    				entity.setHospitalId(date);
    			}else if(propertyInfo.name.equals("Department")){
    				entity.setDepartments(date);
    			}else if(propertyInfo.name.equals("bigDepartments")){
    				entity.setBigDepartments(date);
    			}else if(propertyInfo.name.equals("BirthdayYear")){
    				entity.setYear(date);
    			}else if(propertyInfo.name.equals("BirthdayMonthy")){
    				entity.setMonth(date);
    			}else if(propertyInfo.name.equals("BirthdayDay")){
    				entity.setDay(date);
    			}else if(propertyInfo.name.equals("Specialty")){
    				entity.setSpecialty(date);
    			}else if(propertyInfo.name.equals("Subcategory")){
    				entity.setSubcategory(date);
    			}else if(propertyInfo.name.equals("gbUserName")){
    				entity.setGbUserName(date);
    			}else if(propertyInfo.name.equals("gbPassword")){
    				entity.setGbPassword(date);
    			}else if(propertyInfo.name.equals("hospitalName")){
    				entity.setHospitalName(date);
    			}else if(propertyInfo.name.equals("professional_title")){
    				entity.setProfileTitle(date);
    			}
    			else if(propertyInfo.name.equals("inviteCode")){
    				entity.setInviteCode(date);
    			}
    		}catch (Exception e){
    			e.printStackTrace();
    			return;
    		}
    		i++;
		}while(date != null);
		bSuccess =true;
	}
	
	public RegistrationEntity getInfo() {
		return entity;
	}
}

