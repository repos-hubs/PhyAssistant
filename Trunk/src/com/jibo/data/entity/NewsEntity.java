package com.jibo.data.entity;

import com.jibo.base.src.EntityObj;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 新闻实体bean
 * 
 * @author simon
 * 
 */
public class NewsEntity  implements Parcelable {
	public NewsDetail newsDetail = new NewsDetail();
	public int seq;
	public int totalCount;
	public int newsCount;
	public String id;
	public String title;
	public String date;
	public String source = "";
	public String time;
	public String content;
	public String typeID;
	public String newSource;
	public boolean newStick;
	public String stickMsg = "";
	public String imgUrl = "";
	public String newSummary = "";

	public NewsEntity() {
		super();
	}
	public NewsEntity(String pid) {
		id = pid;
	}
	public NewsEntity(int pid) {
		seq = pid;
	}

	public NewsEntity(String id, String title, String date, String source,
			String time, String typeID, String content, String newSource) {
		super();
		this.id = id;
		this.title = title;
		this.date = date;
		this.source = source;
		this.time = time;
		this.typeID = typeID;
		this.content = content;
		this.newSource = newSource;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(int newsCount) {
		this.newsCount = newsCount;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}

	public String getNewSource() {
		return newSource;
	}

	public void setNewSource(String newSource) {
		this.newSource = newSource;
	}

	public static final Parcelable.Creator<NewsEntity> CREATOR = new Creator<NewsEntity>() {
		public NewsEntity createFromParcel(Parcel source) {
			Log.i("dd", "createFromParcel:" + source);

			NewsEntity entity = new NewsEntity();
			entity.id = source.readString();
			entity.title = source.readString();
			entity.date = source.readString();
			entity.source = source.readString();
			entity.time = source.readString();
			entity.content = source.readString();
			entity.typeID = source.readString();
			entity.newSource = source.readString();
entity.newStick = (Boolean) source.readValue(NewsEntity.class.getClassLoader());
entity.imgUrl = (String) source.readValue(NewsEntity.class.getClassLoader());
entity.newSummary = (String) source.readValue(NewsEntity.class.getClassLoader());
entity.stickMsg = (String) source.readValue(NewsEntity.class.getClassLoader());
			return entity;
		}

		public NewsEntity[] newArray(int size) {
			return new NewsEntity[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(date);
		dest.writeString(source);
		dest.writeString(time);
		dest.writeString(content);
		dest.writeString(typeID);
		dest.writeString(newSource);
		dest.writeValue(newStick);
		dest.writeValue(imgUrl);
		dest.writeValue(newSummary);
		dest.writeValue(stickMsg);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewsEntity other = (NewsEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
