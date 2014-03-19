package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 新闻类别，科室实体bean
 * @author simon
 *
 */
public class NewsCategoriesEntity implements Parcelable{

	public String name;
	public String newsCount;
	public String newsTime; 
	public String bigCategory;
	
	
	public NewsCategoriesEntity() {
		super();
	}
	
	public NewsCategoriesEntity(String name, String newsCount) {
		super();
		this.name = name;
		this.newsCount = newsCount;
	}
	
	public NewsCategoriesEntity(String name, String newsCount, String bigCategory) {
		super();
		this.name = name;
		this.newsCount = newsCount;
		this.bigCategory = bigCategory;
	}

	public NewsCategoriesEntity(String name, String newsCount, String newsTime,
			String bigCategory) {
		super();
		this.name = name;
		this.newsCount = newsCount;
		this.newsTime = newsTime;
		this.bigCategory = bigCategory;
	}
	
	public static final Parcelable.Creator<NewsCategoriesEntity> CREATOR = new Creator<NewsCategoriesEntity>() {
		public NewsCategoriesEntity createFromParcel(Parcel source) {
			Log.i("dd", "createFromParcel:" + source);

			NewsCategoriesEntity entity = new NewsCategoriesEntity();
			entity.name = source.readString();
			entity.newsCount = source.readString();
			entity.newsTime = source.readString();
			entity.bigCategory = source.readString();
			return entity;
		}

		public NewsCategoriesEntity[] newArray(int size) {
			return new NewsCategoriesEntity[size];
		}

	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(String newsCount) {
		this.newsCount = newsCount;
	}

	public String getNewsTime() {
		return newsTime;
	}

	public void setNewsTime(String newsTime) {
		this.newsTime = newsTime;
	}

	public String getBigCategory() {
		return bigCategory;
	}

	public void setBigCategory(String bigCategory) {
		this.bigCategory = bigCategory;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(newsCount);
		dest.writeString(newsTime);
		dest.writeString(bigCategory);
	}

}
