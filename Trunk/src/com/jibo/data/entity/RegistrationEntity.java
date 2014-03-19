package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 注册信息实体bean
 * 
 * @author simon
 * 
 */
public class RegistrationEntity implements Parcelable {

	private String methodType;
	private String islicenseNumber_check;
	private String name;
	private String licenseNumber;
	private String year;
	private String month;
	private String day;
	private String hospitalRegion;
	private String hospitalCity;
	private String hospitalId;
	private String hospitalName;
	private String departments;
	private String bigDepartments;
	private String profileTitle;
	private String medicalSchoolRegion;
	private String medicalSchoolOther;
	private String medicalSchool;
	private String yearOfGarduation;
	private String specialty;
	private String subcategory;
	private String eMail;
	private String contactNumber;
	private String machineId;
	private String gbUserName;
	private String gbPassword;
	private String inviteCode;

	public RegistrationEntity() {
		this.name = "";
		this.licenseNumber = "";
		this.year = "";
		this.month = "";
		this.day = "";
		this.hospitalRegion = "";
		this.hospitalCity = "";
		this.hospitalId = "";
		this.hospitalName = "";
		this.departments = "";
		this.bigDepartments = "";
		this.profileTitle = "";
		this.medicalSchoolRegion = "";
		this.medicalSchoolOther = "";
		this.medicalSchool = "";
		this.yearOfGarduation = "";
		this.specialty = "";
		this.subcategory = "";
		this.eMail = "";
		this.contactNumber = "";
		this.machineId = "";
		this.gbUserName = "";
		this.gbPassword = "";
		this.inviteCode = "";
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getIslicenseNumber_check() {
		return islicenseNumber_check;
	}

	public void setIslicenseNumber_check(String islicenseNumber_check) {
		this.islicenseNumber_check = islicenseNumber_check;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getHospitalRegion() {
		return hospitalRegion;
	}

	public void setHospitalRegion(String hospitalRegion) {
		this.hospitalRegion = hospitalRegion;
	}

	public String getHospitalCity() {
		return hospitalCity;
	}

	public void setHospitalCity(String hospitalCity) {
		this.hospitalCity = hospitalCity;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getBigDepartments() {
		return bigDepartments;
	}

	public void setBigDepartments(String bigDepartments) {
		this.bigDepartments = bigDepartments;
	}

	public String getProfileTitle() {
		return profileTitle;
	}

	public void setProfileTitle(String profileTitle) {
		this.profileTitle = profileTitle;
	}

	public String getMedicalSchoolRegion() {
		return medicalSchoolRegion;
	}

	public void setMedicalSchoolRegion(String medicalSchoolRegion) {
		this.medicalSchoolRegion = medicalSchoolRegion;
	}

	public String getMedicalSchoolOther() {
		return medicalSchoolOther;
	}

	public void setMedicalSchoolOther(String medicalSchoolOther) {
		this.medicalSchoolOther = medicalSchoolOther;
	}

	public String getMedicalSchool() {
		return medicalSchool;
	}

	public void setMedicalSchool(String medicalSchool) {
		this.medicalSchool = medicalSchool;
	}

	public String getYearOfGarduation() {
		return yearOfGarduation;
	}

	public void setYearOfGarduation(String yearOfGarduation) {
		this.yearOfGarduation = yearOfGarduation;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	public String getGbUserName() {
		return gbUserName;
	}

	public void setGbUserName(String gbUserName) {
		this.gbUserName = gbUserName;
	}

	public String getGbPassword() {
		return gbPassword;
	}

	public void setGbPassword(String gbPassword) {
		this.gbPassword = gbPassword;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public static final Parcelable.Creator<RegistrationEntity> CREATOR = new Creator<RegistrationEntity>() {
		public RegistrationEntity createFromParcel(Parcel source) {
			Log.i("dd", "createFromParcel:" + source);

			RegistrationEntity entity = new RegistrationEntity();

			entity.methodType = source.readString();
			entity.islicenseNumber_check = source.readString();
			entity.name = source.readString();
			entity.licenseNumber = source.readString();
			entity.year = source.readString();
			entity.month = source.readString();
			entity.day = source.readString();
			entity.hospitalRegion = source.readString();
			entity.hospitalCity = source.readString();
			entity.hospitalId = source.readString();
			entity.hospitalName = source.readString();
			entity.departments = source.readString();
			entity.bigDepartments = source.readString();

			entity.profileTitle = source.readString();

			entity.medicalSchoolRegion = source.readString();
			entity.medicalSchoolOther = source.readString();
			entity.medicalSchool = source.readString();
			entity.yearOfGarduation = source.readString();
			entity.specialty = source.readString();
			entity.subcategory = source.readString();

			entity.eMail = source.readString();
			entity.contactNumber = source.readString();
			entity.machineId = source.readString();

			entity.gbUserName = source.readString();
			entity.gbPassword = source.readString();
			entity.inviteCode = source.readString();
			return entity;
		}

		public RegistrationEntity[] newArray(int size) {
			return new RegistrationEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(methodType);
		dest.writeString(islicenseNumber_check);
		dest.writeString(name);
		dest.writeString(licenseNumber);
		dest.writeString(year);
		dest.writeString(month);
		dest.writeString(day);
		dest.writeString(hospitalRegion);
		dest.writeString(hospitalCity);
		dest.writeString(hospitalId);
		dest.writeString(hospitalName);
		dest.writeString(departments);
		dest.writeString(bigDepartments);

		dest.writeString(profileTitle);

		dest.writeString(medicalSchoolRegion);
		dest.writeString(medicalSchoolOther);
		dest.writeString(medicalSchool);
		dest.writeString(yearOfGarduation);
		dest.writeString(specialty);
		dest.writeString(subcategory);

		dest.writeString(eMail);
		dest.writeString(contactNumber);
		dest.writeString(machineId);

		dest.writeString(gbUserName);
		dest.writeString(gbPassword);
		dest.writeString(inviteCode);
	}

}
