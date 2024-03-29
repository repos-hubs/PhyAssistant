package com.jibo.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ahfsdrug.
 * 
 * 药品AHFS数据关系表
 * 
 */
public class AhfsDrugInfo implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pid;//药品id
    private String drugName;//药品名
    private String shortName;
    private String changeFields;
    private String state;
    private String updateTime;
    private String fileId;//对应ahfs文件名id

    public AhfsDrugInfo() {
    }

    public AhfsDrugInfo(String pid, String drugName, String shortName, String changeFields, String state, String updateTime, String fileId) {
        this.pid = pid;
        this.drugName = drugName;
        this.shortName = shortName;
        this.changeFields = changeFields;
        this.state = state;
        this.updateTime = updateTime;
        this.fileId = fileId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getChangeFields() {
        return changeFields;
    }

    public void setChangeFields(String changeFields) {
        this.changeFields = changeFields;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

}
