package com.jibo.data.entity;

/**
 * 登录账号信息实体bean  用于账号历史记录
 * @author simon
 *
 */
public class LoginConfigEntity implements java.io.Serializable{
	
	private static final long serialVersionUID = -7829150360098635683L;  
	
	public String userName;
	public String passWord;
	public String isSave;
	public String isAuto;
	
	
	
	public LoginConfigEntity() {
		super();
	}
	
	public LoginConfigEntity(String userName, String passWord, String isSave,
			String isAuto) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.isSave = isSave;
		this.isAuto = isAuto;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getIsSave() {
		return isSave;
	}
	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}
	public String getIsAuto() {
		return isAuto;
	}
	public void setIsAuto(String isAuto) {
		this.isAuto = isAuto;
	}
	
	
	
}
