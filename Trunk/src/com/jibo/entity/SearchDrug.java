package com.jibo.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SearchDictionary.
 * 
 * ҩƷ������
 * 
 */
public class SearchDrug implements java.io.Serializable {

    private String drugId;
    private String key;
    private String changeDate;

    public SearchDrug() {
    }

    public SearchDrug(String drugId, String key, String changeDate) {
        this.drugId = drugId;
        this.key = key;
        this.changeDate = changeDate;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

}