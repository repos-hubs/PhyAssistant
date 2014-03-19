package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DownloadProgressInfo  implements Parcelable{

	public long fileSize;// 文件大小
	private long complete;// 完成度
	private String urlstring;// 下载器标识
	public DownloadProgressInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DownloadProgressInfo(long fileSize, long complete, String urlstring) {
		super();
		this.fileSize = fileSize;
		this.complete = complete;
		this.urlstring = urlstring;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getComplete() {
		return complete;
	}
	public void setComplete(long complete) {
		this.complete = complete;
	}
	public String getUrlstring() {
		return urlstring;
	}
	public void setUrlstring(String urlstring) {
		this.urlstring = urlstring;
	}
	@Override
	    public String toString() {
         return "LoadInfo [fileSize=" + fileSize + ", complete=" + complete
	                 + ", urlstring=" + urlstring + "]";
     }
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<DownloadProgressInfo> CREATOR = new Creator<DownloadProgressInfo>() {
		@SuppressWarnings("unchecked")
		public DownloadProgressInfo createFromParcel(Parcel source) {
			DownloadProgressInfo entity = new DownloadProgressInfo();
			entity.fileSize = source.readLong();
			entity.complete = source.readLong();
			entity.urlstring = source.readString();
			return entity;
		}

		public DownloadProgressInfo[] newArray(int size) {
			return new DownloadProgressInfo[size];
		}

	};
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(fileSize);
		dest.writeLong(complete);
		dest.writeString(urlstring);
	}


}
