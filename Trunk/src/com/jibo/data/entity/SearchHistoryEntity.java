package com.jibo.data.entity;

public class SearchHistoryEntity {
	public String time;
	public String title;
	public String category;
	public String isLatest;
	public String getIsLatest() {
		return isLatest;
	}
	public void setIsLatest(String isLatest) {
		this.isLatest = isLatest;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String tile) {
		this.title = tile;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
}
