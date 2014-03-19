package com.jibo.common;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

/**
 * SharedPreferences管理类
 * */
public class SharedPreferencesMgr {

	private static Context context;
	private static final String KEY_userID = "userID";

	public SharedPreferencesMgr(Context context) {
		this.context = context;
	}

	private static final String KEY_ApplicationUsedCount = "applicationUsedCount";
	public static final String SP_ACCOUNTMANAGEMENT = "ACCOUNTMANAGEMENT"; // data
	public static final String SETTING_INFOS = "SETTING_INFOS";

	public static final String UNZIPNOTSUCCESS = "UNZIPNOTSUCCESS";

	public static final String LICENSE = "LICENSENUMBER";

	public static final String ISMODIFY = "ISMODIFY";
	public static final String GBMAINVERSIONUPDATEDB = "GBAVERSION";
	public static final String GBALOADEDDB = "GBALOADEDDB";

	public static final String CURRENTCODE = "CURRENTCODE";

	public static final String SP_PROFILE_PREF = "GBAAcademicProfile";

	public static final String SP_GBAPP = "GBAPP";

	// -------key--------
	public static final String KEY_DOWNLOAD_SUCCESS = "DOWNLOADINGSUCCESS";

	public static final String KEY_DOWNLOADINGDBCODE = "DOWNLOADINGDBCODE";

	public static final String KEY_ACCOUNTFIRST = "ACCOUNTFIRST";

	public static final String KEY_UPDATETIME = "UPDATETIME";

	public static final String KEY_FIRST_LAUNCHED = "isFirst";
	
	public static final String KEY_FIRST_LAUNCHED_HOME = "isFirstHome";

	public static final String KEY_UPDATE_APK = "update_apk";

	public static final String KEY_LOADED_DB = "LOADEDDB";

	public static final String KEY_UNZIP_SUCCESS = "UNZIPNOTSUCCESS";

	public static final String KEY_NEWCODE = "NEWCODE";

	public static final String KEY_IS_SAVED = "GBAISSAVED";

	public static final String KEY_VERIFYPROFILE = "VERIFYPROFILE";

	public static final String KEY_PROFILE = "profile";

	public static final String KEY_TA = "TA";

	public static final String NULL = "";
	public static final String VER_EVE_DATE = "versionDate";

	public static final String submitFlag = "submitFlag";

	// 登录界面
	public static final String KEY_ISSAVE = "ISSAVE";
	public static final String KEY_ISAUTO = "ISAUTO";
	public static final String KEY_USER_NAME = "USER_NAME";
	public static final String KEY_SAVEPASSWORD = "SAVEPASSWORD";
	public static final String KEY_ISNEW = "ISNEW";
	public static final String KEY_ACCESSTOKEN = "accessToken";
	public static final String KEY_ACCESSTOKENSECRET = "accessTokenSecret";
	public static final String KEY_REGION = "region";
	public static final String KEY_DEPARTMENT = "department";
	private static final String KEY_LICENSE_NUMBER = "license_number";
	private static final String KEY_PHOTO_PATH = "photoPath";
	private static final String CHECK_INFO = "checkInfo";
	private static final String KEY_DRUGID = "DRUGID";
	private static final String KEY_HOSPITALREGION = "HospitalRegion";
	private static final String KEY_HAVE_CHECKED = "has_checked";
	private static final String KEY_DATA_IS_REGISTERED = "data_is_registered";
	private static final String KEY_NEWS_IS_REGISTERED = "news_is_registered";
	private static final String KEY_ALERT_IS_REGISTERED = "alert_is_registered";

	private static final String KEY_DATA_IS_BINDED = "data_is_binded";
	private static final String KEY_NEWS_IS_BINDED = "news_is_binded";
	private static final String KEY_ALERT_IS_BINDED = "alert_is_binded";
	public static final String fileName = "GBADATA.zip";
	public static final String KEY_LastPushId = "lastPushId";
	public static final String KEY_IsSetAlarm = "isSetAlarm";

	private static final String KEY_JOB = "key_job";
	private static final String KEY_LicenseCheckStatus = "licenseCheckStatus";

	/** 是否是第一次search */
	private static final String KEY_IS_FIRST_SEARCH = "search_is_first";
	
	/** 数据包下载ln提醒次数及日期 */
	private static final String KEY_LN_COUNT = "lnCount";

	public static boolean getIsFirstSearch() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		boolean isFirst = sPrefs.getBoolean(KEY_IS_FIRST_SEARCH, true);
		return isFirst;
	}

	public static void setFirstSearch(boolean isFirstSearch) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(KEY_IS_FIRST_SEARCH, isFirstSearch).commit();
	}

	public static int getNewCode() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		int code = sPrefs.getInt(KEY_NEWCODE, 0);
		return code;
	}

	public static void setNewCode(int newCode) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_NEWCODE, newCode).commit();
	}

	public static int isErrorProfile() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getInt(KEY_VERIFYPROFILE, 0);
	}

	// 1：null, 2: error, 3: right, 0:not to verify
	public static void setErrorProfile(int isVerified) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_VERIFYPROFILE, isVerified).commit();
	}

	public static boolean isSaveProfile() {
		SharedPreferences proPrefs = context.getSharedPreferences(
				SP_PROFILE_PREF, 0);
		return proPrefs.getBoolean(KEY_IS_SAVED, false);
	}

	public static void saveApplicationUsedCount(int count) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_ApplicationUsedCount, count).commit();
	}

	public static int getApplicationUsedCount() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getInt(KEY_ApplicationUsedCount, 0);
	}

	public static boolean isLoadedDB() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getBoolean(KEY_LOADED_DB, false);
	}

	public static void setLoadedDB(boolean success) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(KEY_LOADED_DB, success).commit();
	}

	public static boolean isFirstLaunched() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getBoolean(KEY_FIRST_LAUNCHED, true);
	}

	public static void setFirstLaunch(boolean launch) {
		SharedPreferences sp = context.getSharedPreferences(SP_GBAPP, 0);
		sp.edit().putBoolean(KEY_FIRST_LAUNCHED, launch).commit();
	}
	
	public static boolean isFirstLaunchedHome() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getBoolean(KEY_FIRST_LAUNCHED_HOME, true);
	}

	public static void setFirstLaunchHome(boolean launch) {
		SharedPreferences sp = context.getSharedPreferences(SP_GBAPP, 0);
		sp.edit().putBoolean(KEY_FIRST_LAUNCHED_HOME, launch).commit();
	}

	public static boolean isDownloadingSuccess() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getBoolean(KEY_DOWNLOAD_SUCCESS, false);
	}

	public static void setDownloadingSuccess(boolean success) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(KEY_DOWNLOAD_SUCCESS, success).commit();
	}

	public static String getUpdateTime() {
		SharedPreferences sp = context.getSharedPreferences(
				SP_ACCOUNTMANAGEMENT, 0);
		return sp != null ? sp.getString(KEY_UPDATETIME, NULL) : null;
	}

	public static void setUpdateTime(String updateTime) {
		SharedPreferences sp = context.getSharedPreferences(
				SP_ACCOUNTMANAGEMENT, 0);
		sp.edit().putString(KEY_UPDATETIME, updateTime).commit();
	}

	public static String getSettingTA() {
		SharedPreferences sp = context.getSharedPreferences(
				SP_ACCOUNTMANAGEMENT, 0);
		return sp != null ? sp.getString(KEY_TA, NULL) : null;
	}

	public static void setSettingTA(String ta) {
		SharedPreferences sp = context.getSharedPreferences(
				SP_ACCOUNTMANAGEMENT, 0);
		sp.edit().putString(KEY_TA, ta).commit();
	}

	public static void setFirstAccount(boolean isFirst) {
		SharedPreferences sp = context.getSharedPreferences(
				SP_ACCOUNTMANAGEMENT, 0);
		sp.edit().putBoolean(KEY_ACCOUNTFIRST, isFirst).commit();
	}

	public static boolean getFirstAccount() {
		SharedPreferences sp = context.getSharedPreferences(
				SP_ACCOUNTMANAGEMENT, 0);
		return sp.getBoolean(KEY_ACCOUNTFIRST, true);
	}

	// Prime Zhang DB Code.
	public static int getDownloadingDBCode() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getInt(KEY_DOWNLOADINGDBCODE, 0);
	}

	public static void setDownloadingDBCode(int downloadingCode) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_DOWNLOADINGDBCODE, downloadingCode).commit();
	}

	public static boolean getIsSave() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getBoolean(KEY_ISSAVE, false);
	}

	public static boolean getIsAuto() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getBoolean(KEY_ISAUTO, true);
	}

	public static String getUserName() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getString(KEY_USER_NAME, "");
	}

	public static String getPassword() {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		return sPrefs.getString(KEY_SAVEPASSWORD, "");
	}

	public static void setIsNew(boolean isNew) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putBoolean(KEY_ISNEW, isNew).commit();
		;
	}

	public static void setAcademicProfileIsSaved(boolean flag) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putBoolean(KEY_IS_SAVED, flag).commit();
		;
	}

	public static void saveIsSave(boolean isSave) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putBoolean(KEY_ISSAVE, isSave).commit();
	}

	public static void saveIsAuto(boolean isAuto) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putBoolean(KEY_ISAUTO, isAuto).commit();
	}

	public static void savePassword(String password) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_SAVEPASSWORD, password).commit();
	}

	public static void saveDept(String department) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_DEPARTMENT, department).commit();
	}

	public static void saveUserName(String username) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_USER_NAME, username).commit();
	}

	public static void saveLicenseNumber(String license) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_LICENSE_NUMBER, license).commit();
	}
	
	public static String getLicenseNumber() {
		String licenseNumber = context.getSharedPreferences(SP_GBAPP, 0)
				.getString(KEY_LICENSE_NUMBER, "");
		// if(licenseNumber == null || licenseNumber.length() == 0){
		// String userName = getUserName();
		// Log.i("GBA", "Taking License Number by network.");
		// Registration reg = new Registration();
		// if(userName != null)
		// reg.getLisenceNumber(userName);
		// else
		// return Util.NULL;
		// licenseNumber = reg.getLicense();
		// }
		return licenseNumber;
	}
	
	public static void saveLicenseCheckInfo(String str) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(CHECK_INFO, str).commit();
	}
	public static String getLicenseCheckInfo() {
		return context.getSharedPreferences(SP_GBAPP,0)
				.getString(CHECK_INFO, "");	
	}
	public static void saveLicensePhoto(String photoBase64String) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_PHOTO_PATH, photoBase64String).commit();
	}
	public static String getLicensePhoto() {
		return context.getSharedPreferences(SP_GBAPP,0)
				.getString(KEY_PHOTO_PATH, "");	
	}
	
	public static void saveDrugId(String drugId) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_DRUGID, drugId).commit();
	}


	/** 获取执业证号验证状态 */
	public static String getLicenseNumberCheckStatus() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_LicenseCheckStatus, "");
	}

	/** 设置执业证号验证状态 */
	public static void setLicenseNumberCheckStatus(String value) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_LicenseCheckStatus, value).commit();
	}

	/** 获取医院区域 */
	public static String getHospitalRegion() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_HOSPITALREGION, null);
	}

	/** 设置医院区域 */
	public static void setHospitalRegion(String s) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_HOSPITALREGION, s).commit();
	}

	public static String getDrugId() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(KEY_DRUGID,
				"");
	}

	public static String getJob() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(KEY_JOB, "");
	}

	public static void setJob(String job) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_JOB, job).commit();
	}

	public static String getProfile() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(KEY_PROFILE,
				"");
	}

	public static void setProfile(String profile) {
		context.getSharedPreferences(SP_GBAPP, 0).edit()
				.putString(KEY_PROFILE, profile).commit();
	}

	public static void setEveVersionDate(String date) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(VER_EVE_DATE, date).commit();
		;
	}

	public static String getEveVersionDate() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				VER_EVE_DATE, "");
	}

	public static final String KEY_accessToken_SINA = "accessToken_SINA";

	public static final String KEY_ExpiresIn_SINA = "expiresIn_SINA";

	public static final String KEY_UID_SINA = "sina_uid";

	public static final String KEY_accessToken_QQ = "accessToken_QQ";

	public static final String KEY_ExpiresIn_QQ = "expiresIn_QQ";

	public static final String KEY_OpenId_QQ = "qq_openId";

	public static final String KEY_Current_Weibo_PlatForm = "current_Weibo_Platform";

	public static final String KEY_IS_Weibo_Login = "is_weibo_login";

	public static final String KEY_IS_Bind_Success = "is_Bind_Success";

	public static int getCurrentWeiboPlatform() {
		return context.getSharedPreferences(SP_GBAPP, 0).getInt(
				KEY_Current_Weibo_PlatForm, 0);
	}

	public static void setCurrentWeiboPlatForm(int platForm) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_Current_Weibo_PlatForm, platForm).commit();
	}

	public static boolean getIsWeiboLogin() {
		return context.getSharedPreferences(SP_GBAPP, 0).getBoolean(
				KEY_IS_Weibo_Login, false);
	}

	public static void setIsWeiboLogin(boolean isWeiboLogin) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(KEY_IS_Weibo_Login, isWeiboLogin).commit();
	}

	//
	// public static boolean getIsBindSuccess() {
	// return context.getSharedPreferences(SP_GBAPP, 0).getBoolean(
	// KEY_IS_Bind_Success, false);
	// }
	//
	// public static void setIsBindSuccess(boolean isBindSuccess) {
	// SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
	// sPrefs.edit().putBoolean(KEY_IS_Bind_Success, isBindSuccess).commit();
	// }

	public static String getAccessToken_SINA() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_accessToken_SINA, "");
	}

	public static String getExpiresIn_SINA() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_ExpiresIn_SINA, "");
	}

	public static String getUid_SINA() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_UID_SINA, "");
	}

	public static void setAccessToken_SINA(String date) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_accessToken_SINA, date).commit();
	}

	public static void setExpiresIn_SINA(String expires_in) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_ExpiresIn_SINA, expires_in).commit();
	}

	public static void setUid_SINA(String expires_in) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_UID_SINA, expires_in).commit();
	}

	public static String getAccessToken_QQ() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_accessToken_QQ, "");
	}

	public static String getExpiresIn_QQ() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_ExpiresIn_QQ, "");
	}

	public static String getOpenId_QQ() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_OpenId_QQ, "");
	}

	public static void setAccessToken_QQ(String date) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_accessToken_QQ, date).commit();
	}

	public static void setExpiresIn_QQ(String expires_in) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_ExpiresIn_QQ, expires_in).commit();
	}

	public static void setOpenId_QQ(String expires_in) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_OpenId_QQ, expires_in).commit();
	}

	public static void putSubmitFlag(boolean value) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(submitFlag, value).commit();
	}

	public static void saveUserID(String userID) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_userID, userID).commit();
	}

	public static String getUserID() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(KEY_userID,
				"");
	}

	public static void putDeadLine(String deadline) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString("Deadline", deadline).commit();
	}

	public static void putMsgContent(String msgcontent) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString("MsgContent", msgcontent).commit();
	}

	public static String getMsgContent() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				"MsgContent", "");
	}

	public static String getDeadline() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString("Deadline",
				"");
	}

	public static void putSharingTitle(String msgcontent) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString("share_title", msgcontent).commit();
	}

	public static String getSharingTitle() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				"share_title", "");
	}

	public static void putSharingContent(String content) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString("share_content", content).commit();
	}

	public static String getSharingContent() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				"share_content", "");
	}// SETTING_INFOS

	public static String getSurveyTopic() {
		return context.getSharedPreferences(SETTING_INFOS, 0).getString(
				"TOPIC", "");
	}//

	public static String getDept() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_DEPARTMENT, "");
	}

	public static void putVersion(String version) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString("version", version).commit();
	}

	public static String getVersion() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString("version",
				"");
	}

	public static final String KEY_NAME = "name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_Contact_Number = "contactNb";
	public static final String KEY_CITY = "city";
	public static final String KEY_HospitalName = "hospitalName";
	public static final String KEY_BigDepartments = "bigDepartments";
	public static final String KEY_InviteCode = "inviteCode";
	public static final String KEY_InteractionTips = "interactionTips";
	public static void setName(String name) {// 注意：这里是昵称，不是账号
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_NAME, name).commit();
	}

	public static String getName() {
		return context.getSharedPreferences(SP_GBAPP, 0)
				.getString(KEY_NAME, "");
	}

	public static void setEmail(String email) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_EMAIL, email).commit();
	}

	public static String getEmail() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(KEY_EMAIL,
				"");
	}

	public static void setContactNb(String contactNb) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_Contact_Number, contactNb).commit();
	}

	public static String getContactNb() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_Contact_Number, "");
	}

	public static void setRegion(String region) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_REGION, region).commit();
	}

	public static String getRegion() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(KEY_REGION,
				"");
	}

	public static void setCity(String city) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_CITY, city).commit();
	}

	public static String getCity() {
		return context.getSharedPreferences(SP_GBAPP, 0)
				.getString(KEY_CITY, "");
	}

	public static void setHospitalName(String hospitalName) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_HospitalName, hospitalName).commit();
	}

	public static String getHospitalName() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_HospitalName, "");
	}

	public static void setBigDepartments(String bigDepartments) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_BigDepartments, bigDepartments).commit();
	}

	public static String getBigDepartments() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_BigDepartments, "");
	}

	public static void setInviteCode(String inviteCode) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_InviteCode, inviteCode).commit();
	}

	public static String getInviteCode() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_InviteCode, "");
	}
	public static void setInteractionTips(String interactionTips) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_InteractionTips, interactionTips).commit();
	}

	public static String getInteractionTips() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_InteractionTips, "");
	}
	public static int getIntValue(String key, int def) {
		return context.getSharedPreferences(SP_GBAPP, 0).getInt(key, def);
	}

	public static void setIntValue(String key, int v) {
		context.getSharedPreferences(SP_GBAPP, 0).edit().putInt(key, v)
				.commit();
	}

	public static final String KEY_UpdateAPK = "fjskajfksajfksdjfk";

	public static void setLastPushId(String pushId) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_LastPushId, pushId).commit();
	}

	public static String getLastPushId() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_LastPushId, "-1");
	}

	public static void setIsSetAlarmClock(boolean value) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(KEY_IsSetAlarm, value).commit();
	}

	public static boolean getIsSetAlarmClock() {
		return context.getSharedPreferences(SP_GBAPP, 0).getBoolean(
				KEY_IsSetAlarm, false);
	}

	public static final String KEY_NEWS_UPDATE_COUNT = "NewsUpdateCount";

	public static final String KEY_SURVEYS_UPDATE_COUNT = "SurveysUpdateCount";

	public static final String KEY_DRUGALERTS_UPDATE_COUNT = "DrugAlertsUpdateCount";

	public static final String KEY_ExpiredDate = "inviteCodeExpiredDate";
	
	public static final String KEY_PushLock = "pushLock";

	public static int getNewsUpdateCount() {
		return context.getSharedPreferences(SP_GBAPP, 0).getInt(
				KEY_NEWS_UPDATE_COUNT, 0);
	}

	public static void setNewsUpdateCount(int i) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_NEWS_UPDATE_COUNT, i).commit();
	}

	public static int getSurveysUpdateCount() {
		return context.getSharedPreferences(SP_GBAPP, 0).getInt(
				KEY_SURVEYS_UPDATE_COUNT, 0);
	}

	public static void setSurveysUpdateCount(int i) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_SURVEYS_UPDATE_COUNT, i).commit();
	}

	public static int getDrugAlertsUpdateCount() {
		return context.getSharedPreferences(SP_GBAPP, 0).getInt(
				KEY_DRUGALERTS_UPDATE_COUNT, 0);
	}

	public static void setDrugAlertsUpdateCount(int i) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putInt(KEY_DRUGALERTS_UPDATE_COUNT, i).commit();
	}

	public static void setInviteCodeExpiredDate(String inviteCodeExpiredDate) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_ExpiredDate, inviteCodeExpiredDate)
				.commit();
	}

	public static String getInviteCodeExpiredDate() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_ExpiredDate, "");
	}

	public static boolean getPushIsLock() {
		return context.getSharedPreferences(SP_GBAPP, 0).getBoolean(
				KEY_PushLock, false);
	}

	public static void setPushIsLock(boolean isChecked) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putBoolean(KEY_PushLock, isChecked).commit();
	}
	
	public static void setLNCount(int LNCount, String LNDate) {
		SharedPreferences sPrefs = context.getSharedPreferences(SP_GBAPP, 0);
		sPrefs.edit().putString(KEY_LN_COUNT, LNCount + "&" + LNDate)
				.commit();
	}

	public static String getLNCount() {
		return context.getSharedPreferences(SP_GBAPP, 0).getString(
				KEY_LN_COUNT, "");
	}
}
