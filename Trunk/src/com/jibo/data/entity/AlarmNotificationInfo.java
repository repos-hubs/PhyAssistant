package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 通知提醒服务器有更新内容
 * 
 * @author simon
 * 
 */
public class AlarmNotificationInfo implements Parcelable {

	public int rescode;
	public String error;
	public String updateMsg;
	public String updateInfo;
	public String pid;
	public String pType;
	public String newsId;
	public String nType;

	public AlarmNotificationInfo() {
		super();
	}

	public static final Parcelable.Creator<AlarmNotificationInfo> CREATOR = new Creator<AlarmNotificationInfo>() {
		public AlarmNotificationInfo createFromParcel(Parcel source) {
			Log.i("simon", "createFromParcel:" + source);
			AlarmNotificationInfo entity = new AlarmNotificationInfo();
			entity.rescode = source.readInt();
			entity.error = source.readString();
			entity.updateMsg = source.readString();
			entity.updateInfo = source.readString();
			entity.pid = source.readString();
			entity.pType = source.readString();
			entity.newsId = source.readString();
			entity.nType = source.readString();
			
			return entity;
		}

		public AlarmNotificationInfo[] newArray(int size) {
			return new AlarmNotificationInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(rescode);
		dest.writeString(error);
		dest.writeString(updateMsg);
		dest.writeString(updateInfo);
		dest.writeString(pid);
		dest.writeString(pType);
		dest.writeString(newsId);
		dest.writeString(nType);
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
