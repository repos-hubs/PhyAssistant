package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 论文收藏实体类
 * @description 
 * @author will
 * @create 2013-3-6 上午10:08:01
 */
public class PaperFavoriteEntity implements Parcelable {

	private String id;
	private String title;

	public PaperFavoriteEntity() {
		super();
	}

	public PaperFavoriteEntity(String title, String notes, int progressRate, int imageId, String state) {
		super();
		this.title = title;
	}

	public static final Parcelable.Creator<PaperFavoriteEntity> CREATOR = new Creator<PaperFavoriteEntity>() {
		public PaperFavoriteEntity createFromParcel(Parcel source) {
			PaperFavoriteEntity entity = new PaperFavoriteEntity();

			entity.id = source.readString();
			entity.title = source.readString();
			return entity;
		}

		public PaperFavoriteEntity[] newArray(int size) {
			return new PaperFavoriteEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
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

	public static Parcelable.Creator<PaperFavoriteEntity> getCreator() {
		return CREATOR;
	}
	
}
