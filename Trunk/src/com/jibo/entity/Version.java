package com.jibo.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table version.
 */
public class Version {

    private String versionCode;
    private String patchVersionCode;

    public Version() {
    }

    public Version(String versionCode, String patchVersionCode) {
        this.versionCode = versionCode;
        this.patchVersionCode = patchVersionCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getPatchVersionCode() {
        return patchVersionCode;
    }

    public void setPatchVersionCode(String patchVersionCode) {
        this.patchVersionCode = patchVersionCode;
    }

}
