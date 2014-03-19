package com.jibo.data.entity;

public class ResearchBean {
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getAuthors() {
		return Authors;
	}
	public void setAuthors(String authors) {
		Authors = authors;
	}
	public String getJournalName() {
		return JournalName;
	}
	public void setJournalName(String journalName) {
		JournalName = journalName;
	}
	private String ID;
	private String Title;
	private String Authors;
	private String JournalName;
}
