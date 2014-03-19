package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 登录信息实体bean
 * 
 * @author simon
 * 
 */
public class LoginEntity implements Parcelable {

	/**用户id*/
	private String customerId;
	/**登录账号*/
	private String gbUserName;
	/**登录密码*/
	private String gbPassword;
	/**昵称*/
	private String UserName;
	/**执业证号*/
	private String LicenseNumber;
	/**邮箱*/
	private String Email;
	/**联系号码*/
	private String ContactNumber;
	/**医院所在省份或直辖市*/
	private String HospitalRegion;
	/**医院所在城市*/
	private String HospitalCity;
	/**医院名称*/
	private String hospitalName;
	/**大类别*/
	private String bigDepartments;
	/**小类别(科室)*/
	private String Department;
	/**身份*/
	private String job;
	/**职称*/
	private String professional_title;
	/**医师编号id，这个值是服务器返回，通过填写执业证号获得*/
	private String doctorId;
	/**邀请码*/
	private String inviteCode;
	/**广告图片下载地址*/
	private String imagePath;
	/**邀请码过期时间*/
	private String inviteCodeExpiredDate;
	
	/**以下为废弃字段*/
	private String MachineID;
	private String SchoolRegion;
	private String SchoolCity;
	private String SchoolName;
	private String GraduationYear;
	private String companyName;
	private String BirthdayYear;
	private String BirthdayMonthy;
	private String BirthdayDay;
	private String Specialty;
	private String Subcategory;
	private String userId;
	private String HospitalID;
	

	public LoginEntity() {
		super();
		this.MachineID = "";
		this.UserName= "";
		this.LicenseNumber= "";
		this.Email= "";
		this.ContactNumber= "";
		this.SchoolRegion= "";
		this.SchoolCity= "";
		this.SchoolName= "";
		this.GraduationYear= "";
		this.HospitalRegion= "";
		this.HospitalCity= "";
		this.HospitalID= "";
		this.Department= "";
		this.BirthdayYear= "";
		this.BirthdayMonthy= "";
		this.BirthdayDay= "";
		this.Specialty= "";
		this.Subcategory= "";
		this.gbUserName= "";
		this.gbPassword= "";
		this.bigDepartments= "";
		this.hospitalName= "";
		this.job = "";
		this.professional_title= "";
		this.customerId= "";
		this.doctorId= "";
		this.inviteCode= "";
		this.companyName= "";
		this.imagePath= "";
		this.inviteCodeExpiredDate = "";
	}

	public static final Parcelable.Creator<LoginEntity> CREATOR = new Creator<LoginEntity>() {
		public LoginEntity createFromParcel(Parcel source) {
			Log.i("dd", "createFromParcel:" + source);

			LoginEntity entity = new LoginEntity();
			entity.MachineID = source.readString();
			entity.UserName = source.readString();
			entity.LicenseNumber = source.readString();
			entity.Email = source.readString();
			entity.ContactNumber = source.readString();
			entity.SchoolRegion = source.readString();
			entity.SchoolCity = source.readString();
			entity.SchoolName = source.readString();
			entity.GraduationYear = source.readString();
			entity.HospitalRegion = source.readString();
			entity.HospitalCity = source.readString();
			entity.HospitalID = source.readString();
			entity.Department = source.readString();
			entity.BirthdayYear = source.readString();
			entity.BirthdayMonthy = source.readString();
			entity.BirthdayDay = source.readString();
			entity.Specialty = source.readString();
			entity.Subcategory = source.readString();
			entity.gbUserName = source.readString();
			entity.gbPassword = source.readString();
			entity.bigDepartments = source.readString();
			entity.hospitalName = source.readString();
			entity.job = source.readString();
			entity.professional_title = source.readString();
			entity.customerId = source.readString();
			entity.doctorId = source.readString();
			entity.inviteCode = source.readString();
			entity.companyName = source.readString();
			entity.imagePath = source.readString();
			entity.userId = source.readString();
			entity.inviteCodeExpiredDate = source.readString();
			return entity;
		}

		public LoginEntity[] newArray(int size) {
			return new LoginEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(MachineID);
		dest.writeString(UserName);
		dest.writeString(LicenseNumber);
		dest.writeString(Email);
		dest.writeString(ContactNumber);
		dest.writeString(SchoolRegion);
		dest.writeString(SchoolCity);
		dest.writeString(SchoolName);
		dest.writeString(GraduationYear);
		dest.writeString(HospitalRegion);
		dest.writeString(HospitalCity);
		dest.writeString(HospitalID);
		dest.writeString(Department);
		dest.writeString(BirthdayYear);
		dest.writeString(BirthdayMonthy);
		dest.writeString(BirthdayDay);
		dest.writeString(Specialty);
		dest.writeString(Subcategory);
		dest.writeString(gbUserName);
		dest.writeString(gbPassword);
		dest.writeString(bigDepartments);
		dest.writeString(hospitalName);
		dest.writeString(job);
		dest.writeString(professional_title);
		dest.writeString(customerId);
		dest.writeString(doctorId);
		dest.writeString(inviteCode);
		dest.writeString(companyName);
		dest.writeString(imagePath);
		dest.writeString(userId);
		dest.writeString(inviteCodeExpiredDate);
	}

	public String getMachineID() {
		return MachineID;
	}

	public void setMachineID(String machineID) {
		MachineID = machineID;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getLicenseNumber() {
		return LicenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		LicenseNumber = licenseNumber;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getContactNumber() {
		return ContactNumber;
	}

	public void setContactNumber(String contactNumber) {
		ContactNumber = contactNumber;
	}

	public String getSchoolRegion() {
		return SchoolRegion;
	}

	public void setSchoolRegion(String schoolRegion) {
		SchoolRegion = schoolRegion;
	}

	public String getSchoolCity() {
		return SchoolCity;
	}

	public void setSchoolCity(String schoolCity) {
		SchoolCity = schoolCity;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}

	public String getGraduationYear() {
		return GraduationYear;
	}

	public void setGraduationYear(String graduationYear) {
		GraduationYear = graduationYear;
	}

	public String getHospitalRegion() {
		return HospitalRegion;
	}

	public void setHospitalRegion(String hospitalRegion) {
		HospitalRegion = hospitalRegion;
	}

	public String getHospitalCity() {
		return HospitalCity;
	}

	public void setHospitalCity(String hospitalCity) {
		HospitalCity = hospitalCity;
	}

	public String getHospitalID() {
		return HospitalID;
	}

	public void setHospitalID(String hospitalID) {
		HospitalID = hospitalID;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getBirthdayYear() {
		return BirthdayYear;
	}

	public void setBirthdayYear(String birthdayYear) {
		BirthdayYear = birthdayYear;
	}

	public String getBirthdayMonthy() {
		return BirthdayMonthy;
	}

	public void setBirthdayMonthy(String birthdayMonthy) {
		BirthdayMonthy = birthdayMonthy;
	}

	public String getBirthdayDay() {
		return BirthdayDay;
	}

	public void setBirthdayDay(String birthdayDay) {
		BirthdayDay = birthdayDay;
	}

	public String getSpecialty() {
		return Specialty;
	}

	public void setSpecialty(String specialty) {
		Specialty = specialty;
	}

	public String getSubcategory() {
		return Subcategory;
	}

	public void setSubcategory(String subcategory) {
		Subcategory = subcategory;
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

	public String getBigDepartments() {
		return bigDepartments;
	}

	public void setBigDepartments(String bigDepartments) {
		this.bigDepartments = bigDepartments;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getProfessional_title() {
		return professional_title;
	}

	public void setProfessional_title(String professional_title) {
		this.professional_title = professional_title;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInviteCodeExpiredDate() {
		return inviteCodeExpiredDate;
	}

	public void setInviteCodeExpiredDate(String inviteCodeExpiredDate) {
		this.inviteCodeExpiredDate = inviteCodeExpiredDate;
	}
	

}
