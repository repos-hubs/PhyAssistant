package com.jibo.base.src;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

import com.jibo.util.Logs;

public class EntityObj implements Parcelable{
	public String tag;
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public EntityObj(String id) {
		super();
		fieldContents.put("id", id);
	}
	public EntityObj(EntityObj obj) {
		super();
		this.tag = obj.tag;
		this.fieldContents = obj.fieldContents;
	}

	public Map<String, Object> fieldContents = new HashMap<String, Object>();
	public String get(String key){
		Object rlt = fieldContents.get(key);
		return rlt==null?null:rlt.toString();
	}
	public EntityObj(Map<String, Object> fieldContents) {
		super();
		this.fieldContents = fieldContents;
	}
	public EntityObj() {
		// TODO Auto-generated constructor stub
	}
	public Object getObject(String key){		
		return fieldContents.get(key);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fieldContents == null) ? 0 : fieldContents.hashCode());
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
		EntityObj other = (EntityObj) obj;
		String tpid = getId();
		if (fieldContents == null) {
			if (other.fieldContents != null)
				return false;
		} else {
			if(fieldContents.get(tpid)==null){
				return super.equals(obj);
			}
			if (!fieldContents.get(tpid).equals(other.fieldContents.get(tpid)))
			return false;}
		return true;
	}
	public String getId() {
		String tpid = "Id";
		Object Id = fieldContents.get("Id");
		if(Id==null){
			tpid = "id";
		}
		return tpid;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}	public static final Parcelable.Creator<EntityObj> CREATOR = new Creator<EntityObj>() {
		public EntityObj createFromParcel(Parcel source) {
			return new EntityObj(source.readHashMap(EntityObj.class
					.getClassLoader()));
		}

		@Override
		public EntityObj[] newArray(int size) {
			// TODO Auto-generated method stub
			return new EntityObj[size];
		}
	};
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
		
			Logs.i("--- size "+fieldContents.size());
		for(Entry<String,Object> en: fieldContents.entrySet()){
			Logs.i("--- key value "+en.getKey()+" "+en.getValue());
		}
		dest.writeMap(fieldContents);
		}
	
	
}
