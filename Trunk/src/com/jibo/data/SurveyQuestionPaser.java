package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.SurveyQuestionEntity;
import com.jibo.data.entity.SurveyQuestionItemEntity;
import com.jibo.data.entity.SurveyRecheckQuestionEntity;
import com.jibo.data.entity.SurveyRecheckQuestionItemEntity;

public class SurveyQuestionPaser extends SoapDataPaser {
	private SurveyQuestionEntity surveyRecheckQuestion;
	private SurveyQuestionItemEntity surveyRecheckQuestionItem;
	private String propertyValue;

	private ArrayList<SurveyQuestionEntity> recheckQuestionList;
	private ArrayList<SurveyQuestionItemEntity> recheckItemList;
	
	private ArrayList<SurveyQuestionEntity> questionList;
	private ArrayList<SurveyQuestionItemEntity> questionItemList;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("NewGetSurveyQuestionResult");
		
		SoapObject recheckListObj = (SoapObject) detail.getProperty("ListRecheck");
		SoapObject questionListObj = (SoapObject) detail.getProperty("ListQuestion");
		
		questionList = new ArrayList<SurveyQuestionEntity>();
		recheckQuestionList = new ArrayList<SurveyQuestionEntity>();
		for (int i = 0; i < recheckListObj.getPropertyCount(); i++) {
			
			SoapObject recheckObj = (SoapObject) recheckListObj.getProperty(i);
			surveyRecheckQuestion = new SurveyQuestionEntity();
			recheckItemList = new ArrayList<SurveyQuestionItemEntity>();
			propertyValue = recheckObj.getProperty("qID").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyRecheckQuestion.setId(propertyValue);
			} else {
				surveyRecheckQuestion.setId("");
			}

			propertyValue = recheckObj.getProperty("qTitle").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyRecheckQuestion.setTitle(propertyValue);
			} else {
				surveyRecheckQuestion.setTitle("");
			}

			propertyValue = recheckObj.getProperty("qContent").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyRecheckQuestion.setContent(propertyValue);
			} else {
				surveyRecheckQuestion.setContent("");
			}

			propertyValue = recheckObj.getProperty("qType").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyRecheckQuestion.setType(propertyValue);
			} else {
				surveyRecheckQuestion.setType("");
			}

			propertyValue = recheckObj.getProperty("qAnswerRange").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyRecheckQuestion.setAnswer(propertyValue);
			} else {
				surveyRecheckQuestion.setAnswer("");
			}
			
			SoapObject itemObjLst = (SoapObject) recheckObj.getProperty("answerList");
			for (int k = 0; k < itemObjLst.getPropertyCount(); k++) {
				SoapObject itemObj = (SoapObject) itemObjLst.getProperty(k);
				surveyRecheckQuestionItem = new SurveyQuestionItemEntity();

				propertyValue = itemObj.getProperty("optionsID").toString();
				if (!"anyType{}".equals(propertyValue)) {
					surveyRecheckQuestionItem.setOptionID(propertyValue);
				} else {
					surveyRecheckQuestionItem.setOptionID("");
				}

				propertyValue = itemObj.getProperty("answer").toString();
				if (!"anyType{}".equals(propertyValue)) {
					surveyRecheckQuestionItem.setLabel(propertyValue);
				} else {
					surveyRecheckQuestionItem.setLabel("");
				}

				propertyValue = itemObj.getProperty("qJump").toString();
				if (!"anyType{}".equals(propertyValue)) {
					surveyRecheckQuestionItem.setJump(propertyValue);
				} else {
					surveyRecheckQuestionItem.setJump("");
				}

				recheckItemList.add(surveyRecheckQuestionItem);
			}

			surveyRecheckQuestion.setQuestionItemList(recheckItemList);
			recheckQuestionList.add(surveyRecheckQuestion);
		}
		
		for(int i=0; i<questionListObj.getPropertyCount();i++) {
			SoapObject questionObj = (SoapObject) questionListObj.getProperty(i);
			SurveyQuestionEntity surveyQuestion = new SurveyQuestionEntity();
			questionItemList = new ArrayList<SurveyQuestionItemEntity>();
			
			propertyValue = questionObj.getProperty("qID").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyQuestion.setId(propertyValue);
			} else {
				surveyQuestion.setId("");
			}

			propertyValue = questionObj.getProperty("qTitle").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyQuestion.setTitle(propertyValue);
			} else {
				surveyQuestion.setTitle("");
			}

			propertyValue = questionObj.getProperty("qContent").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyQuestion.setContent(propertyValue);
			} else {
				surveyQuestion.setContent("");
			}

			propertyValue = questionObj.getProperty("qType").toString();
			if (!"anyType{}".equals(propertyValue)) {
				surveyQuestion.setType(propertyValue);
			} else {
				surveyQuestion.setType("");
			}
			
			SoapObject questionAnswerLst = (SoapObject) questionObj.getProperty("answerList");
			for (int j = 0; j < questionAnswerLst.getPropertyCount(); j++) {
				SurveyQuestionItemEntity questionItem = new SurveyQuestionItemEntity();
				SoapObject itemObj = (SoapObject) questionAnswerLst.getProperty(j);

				propertyValue = itemObj.getProperty("optionsID").toString();
				if (!"anyType{}".equals(propertyValue)) {
					questionItem.setOptionID(propertyValue);
				} else {
					questionItem.setOptionID("");
				}

				propertyValue = itemObj.getProperty("answer").toString();
				if (!"anyType{}".equals(propertyValue)) {
					questionItem.setLabel(propertyValue);
				} else {
					questionItem.setLabel("");
				}

				propertyValue = itemObj.getProperty("qJump").toString();
				if (!"anyType{}".equals(propertyValue)) {
					questionItem.setJump(propertyValue);
				} else {
					questionItem.setJump("");
				}
				
				questionItemList.add(questionItem);
			}
			surveyQuestion.setQuestionItemList(questionItemList);
			questionList.add(surveyQuestion);
		}
	}

	public ArrayList<SurveyQuestionEntity> getRecheckQuestionList() {
		return recheckQuestionList;
	}

	public void setRecheckQuestionList(
			ArrayList<SurveyQuestionEntity> recheckQuestionList) {
		this.recheckQuestionList = recheckQuestionList;
	}

	public ArrayList<SurveyQuestionEntity> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(ArrayList<SurveyQuestionEntity> questionList) {
		this.questionList = questionList;
	}
	
}
