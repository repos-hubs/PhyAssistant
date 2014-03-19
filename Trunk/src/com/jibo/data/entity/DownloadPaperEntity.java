package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 医学研究下载论文状态实体类
 * @author will
 * @create 2013-3-1 上午9:47:14
 */
public class DownloadPaperEntity implements Parcelable {

	private String id;
	private String title;
	private String notes;
	private int progressRate;
	private int imageId = -1;
	private String state;
	private String fileName;
	private String url;
	private String periodicalTitle;
	private String IFCount;
	private String date;

	public DownloadPaperEntity() {
		super();
	}

	public DownloadPaperEntity(String title, String notes, int progressRate, int imageId, String state, String fileName) {
		super();
		this.title = title;
		this.notes = notes;
		this.progressRate = progressRate;
		this.imageId = imageId;
		this.state = state;
		this.fileName = fileName;
	}

	public static final Parcelable.Creator<DownloadPaperEntity> CREATOR = new Creator<DownloadPaperEntity>() {
		public DownloadPaperEntity createFromParcel(Parcel source) {
			DownloadPaperEntity entity = new DownloadPaperEntity();

			entity.id = source.readString();
			entity.title = source.readString();
			entity.notes = source.readString();
			entity.progressRate = source.readInt();
			entity.imageId = source.readInt();
			entity.state = source.readString();
			entity.fileName = source.readString();
			entity.url = source.readString();
			entity.periodicalTitle = source.readString();
			entity.IFCount = source.readString();
			entity.date = source.readString();
			return entity;
		}

		public DownloadPaperEntity[] newArray(int size) {
			return new DownloadPaperEntity[size];
		}

	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(notes);
		dest.writeInt(progressRate);
		dest.writeInt(imageId);
		dest.writeString(state);
		dest.writeString(fileName);
		dest.writeString(url);
		dest.writeString(periodicalTitle);
		dest.writeString(IFCount);
		dest.writeString(date);
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getProgressRate() {
		return progressRate;
	}

	public void setProgressRate(int progressRate) {
		this.progressRate = progressRate;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public static Parcelable.Creator<DownloadPaperEntity> getCreator() {
		return CREATOR;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
