package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 厂商专辑包列表
 * @author will
 * @create 2013-6-8 上午10:20:59
 */
public class SpecialCollectionListEntity  implements Parcelable {
	public String key; 
	public String companyName;
	public String department;
	public String name;
	public String iconUrl;
	public String downloadLink;
	public String activeStamp;
	public String invalidStamp;
	

	public SpecialCollectionListEntity() {
		super();
	}
	
	public SpecialCollectionListEntity(String key, String companyName, String deparment, String name,
			String iconUrl, String downloadLink, String activeStamp, String invalidStamp) {
		super();
		this.name = name;
		this.companyName = companyName;
		this.department = deparment;
		this.key = key;
		this.iconUrl = iconUrl;
		this.downloadLink = downloadLink;
		this.activeStamp = activeStamp;
		this.invalidStamp = invalidStamp;
	}

	public static final Parcelable.Creator<SpecialCollectionListEntity> CREATOR = new Creator<SpecialCollectionListEntity>() {
		public SpecialCollectionListEntity createFromParcel(Parcel source) {
			SpecialCollectionListEntity entity = new SpecialCollectionListEntity();
			entity.key = source.readString();
			entity.companyName = source.readString();
			entity.department = source.readString();
			entity.name = source.readString();
			entity.iconUrl = source.readString();
			entity.downloadLink = source.readString();
			entity.activeStamp = source.readString();
			entity.invalidStamp = source.readString();
			return entity;
		}

		public SpecialCollectionListEntity[] newArray(int size) {
			return new SpecialCollectionListEntity[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(key);
		dest.writeString(companyName);
		dest.writeString(department);
		dest.writeString(name);
		dest.writeString(iconUrl);
		dest.writeString(downloadLink);
		dest.writeString(activeStamp);
		dest.writeString(invalidStamp);
	}
	@Override
	public int describeContents() {
		return 0;
	}

}
