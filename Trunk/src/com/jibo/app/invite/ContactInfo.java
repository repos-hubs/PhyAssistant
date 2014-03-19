package com.jibo.app.invite;

public class ContactInfo {
	public String name = "";
	public String email = "";
	public String phone = "";
	public String sort_key = "";
	public boolean loaded;
	public String mark;
	
	public ContactInfo(boolean loaded) {
		super();
		this.loaded = loaded;
	}
	public ContactInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[name "+name+" phone "+ phone+" email "+email+"]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ContactInfo other = (ContactInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}

