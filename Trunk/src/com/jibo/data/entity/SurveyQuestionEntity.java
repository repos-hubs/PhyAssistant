package com.jibo.data.entity;

import java.util.ArrayList;

public class SurveyQuestionEntity{
	private String id;
	private String title;
	private String content;
	private String type;
	private ArrayList<SurveyQuestionItemEntity> questionItemList;
	private String answer;
	private String value;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		if(value==null) value="";
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<SurveyQuestionItemEntity> getQuestionItemList() {
		return questionItemList;
	}
	public void setQuestionItemList(ArrayList<SurveyQuestionItemEntity> questionItemList) {
		this.questionItemList = questionItemList;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
