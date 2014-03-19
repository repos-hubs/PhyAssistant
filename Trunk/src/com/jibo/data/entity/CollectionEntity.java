package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @description 医学研究collection实体类
 * @author will
 * @update 2013-2-27 上午10:57:55
 */
public class CollectionEntity implements Parcelable {

	private String id;
	private String title;
	private String imageUrl;
	private int imageId = -1;

	public CollectionEntity() {
		super();
	}

	public CollectionEntity(String title, String imagePath) {
		super();
		this.title = title;
		this.imageUrl = imagePath;
	}
	
	public CollectionEntity(String title, int imageId) {
		super();
		this.title = title;
		this.imageId = imageId;
	}

	public static final Parcelable.Creator<CollectionEntity> CREATOR = new Creator<CollectionEntity>() {
		public CollectionEntity createFromParcel(Parcel source) {
			CollectionEntity entity = new CollectionEntity();

			entity.id = source.readString();
			entity.title = source.readString();
			entity.imageUrl = source.readString();
			entity.imageId = source.readInt();
			return entity;
		}

		public CollectionEntity[] newArray(int size) {
			return new CollectionEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(imageUrl);
		dest.writeInt(imageId);
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
}
