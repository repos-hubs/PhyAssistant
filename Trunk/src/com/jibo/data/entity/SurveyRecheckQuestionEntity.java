package com.jibo.data.entity;

import java.util.ArrayList;

public class SurveyRecheckQuestionEntity {
	private String id;
	private String title;
	private String content;
	private String answer;
	private String type;
	private ArrayList<SurveyRecheckQuestionItemEntity> questionItemList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<SurveyRecheckQuestionItemEntity> getQuestionItemList() {
		return questionItemList;
	}
	public void setQuestionItemList(
			ArrayList<SurveyRecheckQuestionItemEntity> questionItemList) {
		this.questionItemList = questionItemList;
	}
	
}
