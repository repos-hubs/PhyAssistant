package com.jibo.data.entity;

public class GetArticleInfoBeans {
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
	public String getAuthorEntities() {
		return AuthorEntities;
	}
	public void setAuthorEntities(String authorEntities) {
		AuthorEntities = authorEntities;
	}
	public String getDateAndVolume() {
		return DateAndVolume;
	}
	public void setDateAndVolume(String dateAndVolume) {
		DateAndVolume = dateAndVolume;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getDrugsCount() {
		return DrugsCount;
	}
	public void setDrugsCount(String drugsCount) {
		DrugsCount = drugsCount;
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public String getKeyWords() {
		return KeyWords;
	}
	public void setKeyWords(String keyWords) {
		KeyWords = keyWords;
	}
	String ID;
	String Title;
	String Authors;
	String JournalName;    	
    String AuthorEntities;
    String DateAndVolume;
    String Abstract;
    String DrugsCount;
    String Source;
    String KeyWords;
}
