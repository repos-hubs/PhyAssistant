package com.jibo.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table AdminRouteInfo.
 * 
 * ҩƷ��ҩ;��
 */
public class AdminRouteInfo implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
    private String nameEn;
    private String nameCn;
    private String changeDate;

    public AdminRouteInfo() {
    }

    public AdminRouteInfo(String id) {
        this.id = id;
    }

    public AdminRouteInfo(String id, String nameEn, String nameCn, String changeDate) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameCn = nameCn;
        this.changeDate = changeDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

}
