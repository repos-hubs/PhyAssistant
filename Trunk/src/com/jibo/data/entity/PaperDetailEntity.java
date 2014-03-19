package com.jibo.data.entity;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 论文详细信息
 * @description 
 * @author will
 * @create 2013-3-13 下午6:37:45
 */
public class PaperDetailEntity  implements Parcelable {
	public String id;
	public String title = "";
	public String journalID;
	public String publicDate;
	public ArrayList<String> keyWords;
	public String topRank;
	public String rank;
	public boolean isFreeFullText;
	public String comments;
	public String commentsType;
	public String veiwedCount;
	public String abstarct;
	public ArrayList<String> authors;
	public String publicationType;
	public String sourceURL;
	public String freeFullTextURL;
	public String pdfURL;
	public String IF;
	public ArrayList<String> affiliations;
	public String journalName;
	public String journalAbbrName;
	public ArrayList<String> CLC;
	public ArrayList<String> pubTypes;
	public String DOI;
	public String language;
	public ArrayList<String> mesh;
	public ArrayList<String> substances;
	public ArrayList<String> categorys;

	public PaperDetailEntity() {
		super();
	}
	public PaperDetailEntity(String id) {
		this.id = id;
	}

	public static final Parcelable.Creator<PaperDetailEntity> CREATOR = new Creator<PaperDetailEntity>() {
		@SuppressWarnings("unchecked")
		public PaperDetailEntity createFromParcel(Parcel source) {
			PaperDetailEntity entity = new PaperDetailEntity();
			entity.id = source.readString();
			entity.title = source.readString();
			entity.journalID = source.readString();
			entity.publicDate = source.readString();
			entity.keyWords = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.topRank = source.readString();
			entity.rank = source.readString();
			entity.isFreeFullText = (Boolean)source.readValue(PaperDetailEntity.class.getClassLoader());
			entity.comments = source.readString();
			entity.commentsType = source.readString();
			entity.veiwedCount = source.readString();
			entity.abstarct = source.readString();
			entity.authors = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.publicationType = source.readString();
			entity.sourceURL = source.readString();
			entity.freeFullTextURL = source.readString();
			entity.pdfURL = source.readString();
			entity.IF = source.readString();
			entity.affiliations = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.journalName = source.readString();
			entity.journalAbbrName = source.readString();
			entity.CLC = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.pubTypes = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.DOI = source.readString();
			entity.language = source.readString();
			entity.mesh = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.substances = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			entity.categorys = source.readArrayList(PaperDetailEntity.class.getClassLoader());
			return entity;
		}

		public PaperDetailEntity[] newArray(int size) {
			return new PaperDetailEntity[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(journalID);
		dest.writeString(publicDate);
		dest.writeList(keyWords);
		dest.writeString(topRank);
		dest.writeString(rank);
		dest.writeValue(isFreeFullText);
		dest.writeString(comments);
		dest.writeString(commentsType);
		dest.writeString(veiwedCount);
		dest.writeString(abstarct);
		dest.writeList(authors);
		dest.writeString(publicationType);
		dest.writeString(sourceURL);
		dest.writeString(freeFullTextURL);
		dest.writeString(pdfURL);
		dest.writeString(IF);
		dest.writeList(affiliations);
		dest.writeString(journalName);
		dest.writeString(journalAbbrName);
		dest.writeList(CLC);
		dest.writeList(pubTypes);
		dest.writeString(DOI);
		dest.writeString(language);
		dest.writeList(mesh);
		dest.writeList(substances);
		dest.writeList(categorys);
	}
	@Override
	public int describeContents() {
		return 0;
	}

}
