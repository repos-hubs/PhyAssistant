package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ¶©ÔÄ
 * @description 
 * @author will
 * @create 2013-3-20 ÏÂÎç5:05:11
 */
public class JournalSubscribeEntity  implements Parcelable {
	public String resCode; //  200Îªsuccess
	public String errorMsg;
	

	public JournalSubscribeEntity() {
		super();
	}

	public static final Parcelable.Creator<JournalSubscribeEntity> CREATOR = new Creator<JournalSubscribeEntity>() {
		public JournalSubscribeEntity createFromParcel(Parcel source) {
			JournalSubscribeEntity entity = new JournalSubscribeEntity();
			entity.resCode = source.readString();
			entity.errorMsg = source.readString();
			return entity;
		}

		public JournalSubscribeEntity[] newArray(int size) {
			return new JournalSubscribeEntity[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(resCode);
		dest.writeString(errorMsg);
	}
	@Override
	public int describeContents() {
		return 0;
	}

}
