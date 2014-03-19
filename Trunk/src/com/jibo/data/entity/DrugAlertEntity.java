package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 用户安全 实体bean ____mapping 表DrugAlertNews
 * 
 * @author simon
 * 
 */
public class DrugAlertEntity implements Parcelable {

	private String id;
	private String title;
	private String typeId;
	private String date;
	private String time;
	private String source;
	private String content;
	private String productNameCN;
	private String category;

	public DrugAlertEntity() {
		super();
	}

	public DrugAlertEntity(String title, String typeId, String date,
			String time, String source, String content, String productNameCN,
			String category) {
		super();
		this.title = title;
		this.typeId = typeId;
		this.date = date;
		this.time = time;
		this.source = source;
		this.content = content;
		this.productNameCN = productNameCN;
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProductNameCN() {
		return productNameCN;
	}

	public void setProductNameCN(String productNameCN) {
		this.productNameCN = productNameCN;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public static final Parcelable.Creator<DrugAlertEntity> CREATOR = new Creator<DrugAlertEntity>() {
		public DrugAlertEntity createFromParcel(Parcel source) {
			Log.i("dd", "createFromParcel:" + source);

			DrugAlertEntity entity = new DrugAlertEntity();

			entity.id = source.readString();
			entity.title = source.readString();
			entity.typeId = source.readString();
			entity.date = source.readString();
			entity.time = source.readString();
			entity.source = source.readString();
			entity.content = source.readString();
			entity.productNameCN = source.readString();
			entity.category = source.readString();
			return entity;
		}

		public DrugAlertEntity[] newArray(int size) {
			return new DrugAlertEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(typeId);
		dest.writeString(date);
		dest.writeString(time);
		dest.writeString(source);
		dest.writeString(content);
		dest.writeString(productNameCN);
		dest.writeString(category);
	}

}
