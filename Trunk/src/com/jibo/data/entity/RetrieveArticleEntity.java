package com.jibo.data.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RetrieveArticleEntity  implements Parcelable{
	private String id;
	private String title;
	private String authors;
	private String journalName;
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
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getJournalName() {
		return journalName;
	}
	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}
	
	public static final Parcelable.Creator<RetrieveArticleEntity> CREATOR = new Creator<RetrieveArticleEntity>() {  
		public RetrieveArticleEntity createFromParcel(Parcel source) {
			RetrieveArticleEntity articleEntity = new RetrieveArticleEntity();  
			articleEntity.setId(source.readString());
			articleEntity.setTitle(source.readString());
			articleEntity.setAuthors(source.readString());
			articleEntity.setJournalName(source.readString());
			return articleEntity;  
		}
		
		@Override  
		public RetrieveArticleEntity[] newArray(int size) {  
		return new RetrieveArticleEntity[size];  
		}  
	}; 
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);  
		dest.writeString(title);
		dest.writeString(authors);
		dest.writeString(journalName);
	}
}
