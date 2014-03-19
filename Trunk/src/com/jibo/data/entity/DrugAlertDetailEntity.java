package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


/**
 * 用户安全详细内容 实体bean ____mapping 表DrugAlertDetail
 * 
 * @author simon
 * 
 */
public class DrugAlertDetailEntity implements Parcelable {


	private String id;
	private String typeId;
	private String detailId;
	private String detailTitle;
	private String detailContent;

	public DrugAlertDetailEntity() {
		super();
	}
	
	public DrugAlertDetailEntity(String id, String detailId,
			String detailTitle, String detailContent) {
		super();
		this.id = id;
		this.detailId = detailId;
		this.detailTitle = detailTitle;
		this.detailContent = detailContent;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getDetailTitle() {
		return detailTitle;
	}

	public void setDetailTitle(String detailTitle) {
		this.detailTitle = detailTitle;
	}

	public String getDetailContent() {
		return detailContent;
	}

	public void setDetailContent(String detailContent) {
		this.detailContent = detailContent;
	}

	public static final Parcelable.Creator<DrugAlertDetailEntity> CREATOR = new Creator<DrugAlertDetailEntity>() {
		public DrugAlertDetailEntity createFromParcel(Parcel source) {
			Log.i("dd", "createFromParcel:" + source);

			DrugAlertDetailEntity entity = new DrugAlertDetailEntity();
			entity.id = source.readString();
			entity.typeId = source.readString();
			entity.detailId = source.readString();
			entity.detailTitle = source.readString();
			entity.detailContent = source.readString();
			return entity;
		}

		public DrugAlertDetailEntity[] newArray(int size) {
			return new DrugAlertDetailEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(typeId);
		dest.writeString(detailId);
		dest.writeString(detailTitle);
		dest.writeString(detailContent);
	}

}
