package com.jibo.data.entity;

/**
 * ���ҳʵ��bean
 * 
 * @author simon
 * 
 */
public class AdvertisingEntity implements java.io.Serializable {

	private static final long serialVersionUID = -7829150360098635683L;

	public String userName;
	public String companyName;
	public String imageUrl;

	public AdvertisingEntity() {
		super();
	}

	public AdvertisingEntity(String userName, String companyName,
			String imageUrl) {
		super();
		this.userName = userName;
		this.imageUrl = imageUrl;
		this.companyName = companyName;
	}

}
