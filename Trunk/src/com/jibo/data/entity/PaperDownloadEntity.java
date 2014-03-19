package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 数据库表PagerDownload对应实体类
 * @description 
 * @author will
 * @create 2013-3-7 下午1:55:45
 */
public class PaperDownloadEntity implements Parcelable {

	private String paperID;
	private String url;
	private String imageUrl;
	private String fileName;
	private String state;
	private String title;
	private String remarks;
	private String others;
	private String username;
	public static final String PENDING = "Pending", DOWNLOADING = "Downloading", READ = "Open", PAUSE = "Pause", FAILURE = "Failure";
	private String periodicalTitle;
	private String IFCount;
	private String date;
	
	public PaperDownloadEntity() {
		super();
	}

	public PaperDownloadEntity(String paperID, String url, String imageUrl, String fileName, 
			String state, String title, String remarks, String others, String username) {
		super();
		this.paperID = paperID;
		this.url = url;
		this.imageUrl = imageUrl;
		this.fileName = fileName;
		this.state = state;
		this.title = title;
		this.remarks = remarks;
		this.others = others;
		this.username = username;
	}

	public static final Parcelable.Creator<PaperDownloadEntity> CREATOR = new Creator<PaperDownloadEntity>() {
		public PaperDownloadEntity createFromParcel(Parcel source) {
			PaperDownloadEntity entity = new PaperDownloadEntity();
			entity.paperID = source.readString();
			entity.url = source.readString();
			entity.imageUrl = source.readString();
			entity.fileName = source.readString();
			entity.state = source.readString();
			entity.title = source.readString();
			entity.remarks = source.readString();
			entity.others = source.readString();
			entity.username = source.readString();
			entity.periodicalTitle = source.readString();
			entity.IFCount = source.readString();
			entity.date = source.readString();
			return entity;
		}

		public PaperDownloadEntity[] newArray(int size) {
			return new PaperDownloadEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(paperID);
		dest.writeString(url);
		dest.writeString(imageUrl);
		dest.writeString(fileName);
		dest.writeString(state);
		dest.writeString(title);
		dest.writeString(remarks);
		dest.writeString(others);
		dest.writeString(username);
		dest.writeString(periodicalTitle);
		dest.writeString(IFCount);
		dest.writeString(date);
	}

	public String getPaperID() {
		return paperID;
	}

	public void setPaperID(String paperID) {
		this.paperID = paperID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPeriodicalTitle() {
		return periodicalTitle;
	}

	public void setPeriodicalTitle(String periodicalTitle) {
		this.periodicalTitle = periodicalTitle;
	}

	public String getIFCount() {
		return IFCount;
	}

	public void setIFCount(String iFCount) {
		IFCount = iFCount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
