package com.jibo.common;

import android.util.Log;

/**
 * SOAP��س�����Դ
 * */ 
public class SoapRes {

	public static final String NAMESPACE = "http://www.pda.com/pda/";
	
//	public static final String URL = "http://61.152.104.167:8087/";
//	public static final String JOURANLURL = "http://61.152.104.167:8089/";
//	public static final String URLINTERACTION = "http://61.152.104.167:8088/druginteractionservice.asmx";
	public static final String URL = "http://192.168.0.60/PDAWebServices/";
	public static final String JOURANLURL = "http://192.168.0.60:9100/";
	public static final String URLINTERACTION = "http://192.168.0.45:8018/druginteractionservice.asmx";
	
	public static final String URLCustomer = URL + "CustomerService.asmx";
	public static final String URLDrug = URL + "DrugService.asmx";
	public static final String URLNews = URL + "NewsService.asmx";
	public static final String URLIMAGENews = URLNews + "?op=getImageNewsList";
	public static final String URLStickNews = URLNews + "?op=getNewStick";
	public static final String URLJOURANL= JOURANLURL+"/JournalWatch.asmx";
	public static final String URLRESEARCH = URLJOURANL+"?op=GetPaperList";
	public static final String URLGETSUNSCRIPTIONS = URLJOURANL+"?op=GetUsersPeriodicalInfoByUserId";
	public static final String UPDATE_PAPERS_COUNT = URLJOURANL+"?op=GetPaperUpdatedCount";
	public static final String URLResearchDetail = JOURANLURL+"/JournalWatch.asmx";
	public static final String URLNewsDetail = URLNews+"?op=GetNewsInfo";
	public static final String URLResearch = URL + "ResearchService.asmx";
	public static final String URLEvent = URL + "EventService.asmx";
	public static final String URLSurvey = URL + "SurveyService.asmx";
	public static final String URLNetwork = URL + "NetworkService.asmx";
	public static final String URLProfile = URL + "ProfileService.asmx";
	public static final String URLCalc = URL + "CalculationsService.asmx";
	public static final int REQ_ID_GET_CUSTOMER_INFO = 12;
	public static final String URLUSERLOGINFO = URL + "recordCustomerInfo.asmx"; // prime
	public static final int REQ_ID_RECORD_USER_INFO = 9;
	private static String METHOD_RECORD_USER_INFO = "recordUserInfoVersion";

	public static final int REQ_ID_VERSION = 10;
	public static String METHOD_VERSION = "getUpdate";
	public static final String URLVersion = URL + "version.asmx";

	// public static final String SOAP_ACTION_VERSION =NAMESPACE+METHOD_VERSION;

	public static final int REQ_ID_SEND_MAIL = 11;
	public static final String METHOD_SEND_MAIL = "SendshareMail";
	public static final String URLSendMail = URL + "SendMail.asmx";
	public static final String RESULT_PROPERTY_SEND_MAIL = "SendshareMailResult";

	public static final String METHOD_GET_CUSTOMER_INFO = "GetCustomerInfo_new";
	public static final String KEY_GET_CUSTOMER_USERNAME = "username";
	public static final String KEY_GET_CUSTOMER_PWD = "pwd";

	public static final int REQ_ID_GET_PROFILE = 13;
	public static final String GET_PROFILE = "GetProfile";
	public static final String KEY_GETPROFILE_DOCTORID = "doctorId";
	public static final String KEY_GETPROFILE_USERNAME = "username";

	public static final int REQ_ID_GET_COAUTHOR = 14;
	public static final String GET_COAUTHOR_BY_DOCTOR = "GetCoauthorsByDoctor";
	public static final String KEY_GET_COAUTHOR_DOCTORID = "doctorId";
	public static final String KEY_GET_COAUTHOR_INHOSPITAL = "inHospital";

	public static final int REQ_ID_RETRIEVE_ARTICLE = 15;
	public static final String RETRIEVE_ARTICLE_BY_DOCTOR = "RetrieveArticlesByDoctor";
	public static final String KEY_RETRIEVE_ARTICLE_DOCTORID = "doctorId";

	public static final int REQ_ID_FEED_BACK = 16;
	public static final String METHOD_USER_FEED_BACK = "userfeedback";
	public static final String KEY_FEED_BACK_USERID = "userid";
	public static final String KEY_FEED_BACK_CONTENT = "feedback";


	/** ��ȡ��Ӧapp�汾�Ķ�Ӧdb file�����ص�ַ */
	public static final int REQ_ID_NEW_VERSION = 17;
	public static String METHOD_NEW_VERSION = "getNewUpdate";
	public static final String URLNewVersion = URL + "version.asmx";
	public static final String KEY_NEW_P_VERSION = "p_version";

	/** ���app�Ƿ��и��£��и���ʱ�������ص�ַ */
	public static final int REQ_ID_APP_NEW_VERSION = 170;
	public static String METHOD_APP_NEW_VERSION = "SoftWareUpdateIOS";
	public static String RESULT_APP_NEW_VERSION = "SoftWareUpdateIOSResult";

	/** ���db and source files�Ƿ��и��£��и���ʱ�������ص�ַ */
	public static final int REQ_ID_DATA_NEW_VERSION = 171;
	public static String METHOD_DATA_NEW_VERSION = "getExDrugUpdateInfo";
	public static String RESULT_DATA_NEW_VERSION = "getExDrugUpdateInfoResult";

	public static final int REQ_ID_SYNC_HISTORY_NEW = 18;
	public static String METHOD_SYNC_HISTORY_NEW = "Getview_history_newTwo";

	public static final String URLDoc = URL + "txt/";

	public static final String KEY_EVENT_MAXDATE = "maxDate";
	public static final String KEY_EVENT_TYPE = "isType";
	public static final String KEY_EVENT_PAGENUM = "pageNum";
	public static final String KEY_RESEARCH_TAID = "thisTA";

	public static final int REQ_ID_CHECK_TA = 43;
	public static final String REQ_ID_CHECK_METHOD = "ReKwCount";
	public static final String KEY_RESEARCH_TA_ID = "TA_ID";
	public static final String KEY_RESEARCH_KW_ID = "Str_kw_id";
	public static final String KEY_RESEARCH_KW_COUNT = "Str_kwcount";

	public static final String KEY_RESEARCH_TA = "ta";
	public static final String KEY_REASEARCH_kw = "kw";
	public static final String KEY_REASEARCH_PS = "pageSize";
	public static final String KEY_REASEARCH_PN = "pageNum";

	public static final String KEY_FAVORITE_USERFAV = "userFavorite";
	public static final String KEY_FAVORITE_SPEC = "spec";

	public static final String KEY_RESEARCH_TAI = "TA";
	public static final String KET_RESEARCH_ARTID = "articleId";

	public static final String KEY_SURVEY_DEPT = "surdepartment";
	public static final String KEY_SURVEY_REGION = "surregion";
	public static final String KEY_SURVEY_UID = "uID";
	public static final String KEY_SURVEY_QID = "qID";
	public static final String KEY_SURVEY_QTYPE = "qType";
	public static final String KEY_SURVEY_AID = "aID";

	public static final String KEY_DEADLINE = "deadline";
	public static final String KEY_REGION = "region";
	public static final String KEY_DEPART = "department";

	public static final String KEY_EVENT_ID = "eventId";

	public static final String KEY_P_VERSION = "P_version";
	public static final String KEY_D_VERSION = "D_version";
	public static final String KEY_D_DRGID = "productId";
	public static final String KEY_D_TYPE = "type";

	public static final int REQ_ID_GETGOOLE = 233;
	public static final String REQ_ID_GETGOOLEMSG = "DrugInfoTranslation";
	public static String SOAP_ACTION_GETGOOGLEMSG = NAMESPACE
			+ REQ_ID_GETGOOLEMSG;

	public static final int REQ_ID_GETMSG = 522;
	public static final String REQ_ID_GETMESSAGE = "getMessageShare";
	public static String SOAP_ACTION_GETMSG = NAMESPACE + REQ_ID_GETMESSAGE;

	public static final int REQ_ID_RESEARCH_GETINFID = 23;
	public static final String REQ_ID_RESEARCH_GETINF = "GetArticleInfo";
	public static String SOAP_ACTION_GETINF = NAMESPACE
			+ REQ_ID_RESEARCH_GETINF;

	public static final int REQ_ID_RESEARCH_TART = 24;
	public static final String REQ_ID_RESEARCH_GETART = "getTaArticle";
	public static String SOAP_ACTION_GETTAART = NAMESPACE
			+ REQ_ID_RESEARCH_GETART;

	public static final int REQ_ID_FAVORITE = 523;
	public static final String REQ_ID_SYNCFAV_METHOD = "GetCustomer_Favorite";
	public static String SOAP_ACTION_SYNCFAV = NAMESPACE
			+ REQ_ID_SYNCFAV_METHOD;

	public static final int REQ_ID_RESEARCH_TA = 25;
	public static final String METHOD_NAME_GETTA = "getKWofTA";
	public static String SOAP_ACTION_GETTA = NAMESPACE + METHOD_NAME_GETTA;

	public static final int REQ_ID_SURVEY_SUBMIT = 26;
	public static final String METHOD_NAME_SUBMIT = "setSurveyResultNew";
	public static String SOAP_ACTION_SURVEYSUBMIT = NAMESPACE
			+ METHOD_NAME_SUBMIT;

	public static final int REQ_ID_SURVEY_GETQES = 27;
	private static final String METHOD_NAME_SURGETQES = "getQuestionNew";
	public static String SOAP_ACTION_SURVEYGETQES = NAMESPACE
			+ METHOD_NAME_SURGETQES;

	public static final int REQ_ID_EVENT_INF = 28;
	private static String METHOD_NAME_EVENTINF = "GetEventInfo";
	public static String SOAP_ACTION_EVENTINF = NAMESPACE
			+ METHOD_NAME_EVENTINF;

	public static final int REQ_ID_EVENT = 29;
	private static String METHOD_NAME_EVENT = "RetrieveEventsNew";
	public static final String SOAP_ACTION_EVENT = NAMESPACE
			+ METHOD_NAME_EVENT;

	public static final int REQ_ID_SYNC_VIEW_HISTORY = 30;
	private static String METHOD_NEW_VIEW_HISTORY = "Getview_history_new";
	public static final String KEY_NEW_VIEW_HISTORY = "userHistory";

	public static final int REQ_ID_GET_NEWS_COUNT = 31;
	public static final String METHOD_GET_NEWS_COUNT = "getNewsCount";
	public static final String KEY_SEARCH_DATE = "searchDate";
	public static final String KEY_SEARCH_TYPE = "searchType";

	public static final int REQ_ID_GET_CUSTOMER_DOWN = 32;
	public static final String METHOD_GET_CUSTOMER_DOWN = "getCustomer_DownAfterUpCategory";
	public static final String KEY_CUSTOMER_DOWN_USERNAME = "userName";
	public static final String KEY_CUSTOMER_DOWN_CATEGORY_ID = "categoryID";
	public static final String KEY_CUSTOMER_DOWN_PACKAGE_STR = "category_down";

	public static final int REQ_ID_GET_CATEGORY_UPDATE = 33;
	public static final String METHOD_GET_CATEGORY_UPDATE = "getCategoryUpdate";
	public static final String KEY_GET_CATEGORY_STR = "str";

	public static final int REQ_ID_GET_ZIP_FILE = 34;
	public static final String METHOD_GET_ZIP_FILE = "GetZipPath";
	public static final String KEY_GET_ZIP_FILE_STR = "FileName";
	public static final String KEY_GET_ZIP_FILE_TYPE = "Type";

	public static final int REQ_ID_MARKET = 35;
	public static final String METHOD_GET_MARKET_INFO = "GetCategoryInfo";
	public static final String KEY_MARKET_VERSION = "version";
	public static final String KEY_MARKET_DEVICE_ID = "DeviceId";
	public static final String KEY_MARKET_DATA = "Cate";

	public static final int REQ_ID_SURVEY = 36;
	public static final String METHOD_GET_SURVEY_INFO = "NewGetSurveyList";
	public static final String KEY_SURVEY_USERNAME = "userName";
	public static final String KEY_SURVEY_ID = "surveyID";
	public static final String KEY_SURVEY_PAGE_SIZE = "pageSize";
	public static final String KEY_SURVEY_PAGE_TYPE = "pageType";
	public static final String KEY_SURVEY_LAN = "language";

	public static final int REQ_ID_UPDATE_PAY_INFO = 37;
	public static final String METHOD_UPDATE_PAY_INFO = "NewupdateUserSurveyinfo";
	public static final String KEY_PAY_INFO_USERNAME = "userName";
	public static final String KEY_PAY_INFO_NAME = "Name";
	public static final String KEY_PAY_INFO_PHONE = "phone";
	public static final String KEY_PAY_INFO_REGION = "region";
	public static final String KEY_PAY_INFO_CITY = "city";
	public static final String KEY_PAY_INFO_HOSPITAL = "hospital";
	public static final String KEY_PAY_INFO_DPT = "department";
	public static final String KEY_PAY_INFO_TITLE = "professional";
	public static final String KEY_PAY_INFO_WAY = "payWay";
	public static final String KEY_PAY_INFO_ACCOUNT = "payAccount";

	public static final int REQ_ID_GET_SURVEY_RECHECK = 38;
	public static final String METHOD_GET_SURVEY_RECHECK = "NewGetSurveyQuestion";
	public static final String KEY_PAY_SURVEY_RECHECK_ID = "surveyID";
	public static final String KEY_PAY_SURVEY_RECHECK_LN = "language";

	public static final int REQ_ID_NEW_SURVEY_SUBMIT = 39;
	public static final String METHOD_GET_SURVEY_SUBMIT = "NewSubmitSurvey";
	public static final String KEY_PAY_SURVEY_SUBMIT_USERNAME = "userName";
	public static final String KEY_PAY_SURVEY_SUBMIT_ID = "surveyID";
	public static final String KEY_PAY_SURVEY_SUBMIT_LN = "language";
	public static final String KEY_PAY_SURVEY_SUBMIT_RR = "recheckResult";
	public static final String KEY_PAY_SURVEY_SUBMIT_RA = "recheckAll";
	public static final String KEY_PAY_SURVEY_SUBMIT_AA = "answerAll";

	public static final int REQ_ID_GET_SURVEY_HISTORY = 40;
	public static final String METHOD_GET_SURVEY_HISTORY = "NewGetSurveyHistory";
	public static final String KEY_GET_SURVEY_HISTORY_USERNAME = "userName";
	public static final String KEY_GET_SURVEY_HISTORY_RECORDID = "RecordID";
	public static final String KEY_GET_SURVEY_HISTORY_PAGESIZE = "pageSize";
	public static final String KEY_GET_SURVEY_HISTORY_PAGETYPE = "pageType";
	public static final String KEY_GET_SURVEY_HISTORY_LN = "language";

	public static final int REQ_ID_GET_PAY_INFO = 41;
	public static final String METHOD_GET_PAY_INFO = "NewGetUserSurveyinfo";
	public static final String KEY_GET_PAY_INFO_USERNAME = "userName";

	public static final int REQ_ID_UPDATE_INVITE_CODE = 42;
	public static final String METHOD_UPDATE_INVITE_CODE = "UpdateInviteCode2";
	public static final String KEY_USERNAME = "gbUserName";
	public static final String KEY_CODE = "inviteCode";

	private static boolean DEBUG = true;

	public static final String KEY_PAGESIZE = "PageSize";
	public static final String KEY_PAGENUM = "PageNum";
	public static final String KEY_PAGESIZE1 = "pageSize";
	public static final String KEY_PAGENUM1 = "pageNum";

	/**
	 * ��ҵ����
	 */
	/* -------------------start-------------------------- */
	// �����������Ƿ��и���
	public static final int REQ_ID_CHECK_NEWS_CATEGORIES = 202;
	public static final String METHOD_NAME_CHECK_NEWS_CATEGORIES = "getFlagUpdateBigCategory";
	public static final String RESULT_PROPERTY_CHECK_NEWS_CATEGORIES = "getFlagUpdateBigCategoryResult";

	// ��ȡ��������������
	public static final int REQ_ID_GET_NEWS_CATEGORIES_BY_BIGCATEGROY = 203;
	public static final String METHOD_NAME_GET_NEWS_CATEGORIES_BY_BIGCATEGROY = "GetNewsCategoriesNew";
	public static final String RESULT_PROPERTY_GET_NEWS_CATEGORIES_BY_BIGCATEGROY = "GetNewsCategoriesNewResult";

	/** ��ȡ�����б?ͨ��id������أ���ֹ��������ظ� */
	public static final int REQ_ID_GET_NEWS_TOP_20 = 204;
	public static final int REQ_ID_GET_NEWS_TOP_MORE_BY_ID = 205;
	public static final int REQ_ID_GET_NEWS_OF_CATEGORY_TOP_DATA = 206;
	public static final int REQ_ID_GET_NEWS_OF_CATEGORY_MORE_DATA = 207;
	public static final int REQ_ID_GET_NEWS_BY_SEARCH_VALUE = 208;
	public static final String METHOD_NAME_GET_NEWS_BYID_DESC = "getNewsSouce";
	public static final String RESULT_PROPERTY_GET_NEWS_BYID_DESC = "getNewsSouceResult";

	// // ��ݹؼ��ֽ��м�������
	// public static final int REQ_ID_GET_NEWS_BY_Search = 209;
	// public static final String METHOD_NAME_GET_NEWS_BY_Search =
	// "RetrieveNews";
	// public static final String RESULT_PROPERTY_GET_NEWS_BY_Search =
	// "RetrieveNewsResult";

	// �������id��ȡ��������
	public static final int REQ_ID_GET_NEWSDETAIL_BY_ID = 210;
	public static final String METHOD_NAME_GET_NEWSDETAIL_BY_ID = "GetNewsInfoSource";
	public static final String RESULT_PROPERTY_GET_NEWSDETAIL_BY_ID = "GetNewsInfoSourceResult";

	public static final int REQ_ID_NEWS_DETAIL = 211;
	public static final String METHOD_REQ_ID_NEWS_DETAIL = "GetNewsInfoSource";
	public static final String RESULT_REQ_ID_NEWS_DETAIL = "GetNewsInfoSourceResult";
	// soap key
	public static final String KEY_NEWS_TOTALROW = "totalRow";
	public static final String KEY_NEWS_BIGCATEGORY = "bigCategory";
	public static final String KEY_NEWS_CATEGORY = "category";
	public static final String KEY_NEWS_CATEGORYNUMBER = "categoryNumber";

	public static final String KEY_SEARCHVALUE = "searchValue";
	public static final String KEY_SINCE_ID = "since_id";
	public static final String KEY_MAX_ID = "max_id";
	public static final String KEY_COUNT = "count";
	public static final String KEY_NEWS_DETAIL_ID = "newsId";
	/* -------------------end---------------------------- */

	/**
	 * ��ҩ��ȫ 
	 */
	/* ----------------------start-------------------- */

	// ��ȡ����20���¼
	public static final int REQ_ID_GET_DRUGALERT_TOP_20 = 100;
	// ��ȡ�������
	public static final int REQ_ID_GET_DRUGALERTS_NEW_DATA = 101;
	// ��ȡ������
	public static final int REQ_ID_GET_DRUGALERTS_MORE_DATA = 102;
	// ָ��typeId��ȡ
	public static final int REQ_ID_GET_DRUGALERT_BY_TYPEID = 103;

	public static final String METHOD_NAME_GET_DRUGALERTS_NEW_DATA = "getAlertDataNew";

	public static final String RESULT_PROPERTY_GET_DRUGALERTS_NEW_DATA = "getAlertDataNewResult";

	// ��ȡͨ����ϸ����
	public static final int REQ_ID_GET_DRUGALERT_GET_DETAIL = 104;
	public static final String METHOD_NAME_GET_DETAIL = "getAlertDetail";
	public static final String RESULT_PROPERTY_GET_DETAIL = "getAlertDetailResult";

	// �������������
	public static final int REQ_ID_GET_DRUGALERT_NEWESTCOUNT = 105;
	public static final String METHOD_NAME_GET_DRUGALERT_NEWESTCOUNT = "getDrugAlertCount";
	public static final String RESULT_PROPERTY_GET_DRUGALERT_NEWESTCOUNT = "getDrugAlertCountResult";

	public static final String KEY_DRUGALERT_SOURCE = "Source";
	public static final String KEY_DRUGALERT_DATE = "New_Date";
	public static final String KEY_DRUGALERT_TYPEID = "TypeId";

	/* ----------------------end-------------------- */

	/**
	 * ע��
	 */
	/* ----------------------start-------------------- */
	// ����û����Ƿ����
	public static final int REQ_ID_CHECK_USERNAME_ISVALID = 301;
	public static final String METHOD_NAME_CHECK_USERNAME_ISVALID = "IsValidUser";
	public static final String RESULT_PROPERTY_CHECK_USERNAME_ISVALID = "IsValidUserResult";

	// �ύ�?
	// public static final int REQ_ID_REGISTRATION_SUBMIT_INFO = 302;
	// public static final String METHOD_NAME_REGISTRATION_SUBMIT_INFO =
	// "RegisterCustomerNew";
	// public static final String RESULT_PROPERTY_REGISTRATION_SUBMIT_INFO =
	// "RegisterCustomerNewResult";

	public static final int REQ_ID_REGISTRATION_SUBMIT_INFO = 302;
	public static final String METHOD_NAME_REGISTRATION_SUBMIT_INFO = "RegisterCustomerNewWithOutNull";
	public static final String RESULT_PROPERTY_REGISTRATION_SUBMIT_INFO = "RegisterCustomerNewWithOutNullResult";

	// �޸�����
	public static final int REQ_ID_UPDATE_PASSWORD = 303;
	public static final String METHOD_NAME_UPDATE_PASSWORD = "UpdateCustomerPassword";
	public static final String RESULT_PROPERTY_UPDATE_PASSWORD = "UpdateCustomerPasswordResult";
	// ��ȡ�û�ע����Ϣ
	// public static final int REQ_ID_GET_REGISTARTIONS_DATA = 304;
	// public static final String METHOD_NAME_GET_REGISTARTIONS_DATA =
	// "GetCustomerInfo";
	// public static final String RESULT_PROPERTY_GET_REGISTARTIONS_DATA =
	// "GetCustomerInfoResult";

	public static final int REQ_ID_GET_REGISTARTIONS_DATA = 304;
	public static final String METHOD_NAME_GET_REGISTARTIONS_DATA = "GetCustomerInfoWithInviteCode";
	public static final String RESULT_PROPERTY_GET_REGISTARTIONS_DATA = "GetCustomerInfoWithInviteCodeResult";

	public static final String KEY_REGISTRATION_USERNAME = "gbUsername";

	// �������
	public static final int REQ_ID_FORGOT_PWD = 309;
	public static final String METHOD_NAME_FORGOT_PWD = "ForgetPassword";
	public static final String RESULT_PROPERTY_FORGOT_PWD = "ForgetPasswordResult";

	// ͨ����Ż��������һ����� 
	public static final int REQ_ID_GET_PWD = 310;
	public static final String METHOD_NAME_GET_PWD = "getForgetPwdFeedbackInfo";
	public static final String RESULT_PROPERTY_GET_PWD = "getForgetPwdFeedbackInfoResult";

	// �������
	public static final int REQ_ID_FORGOT_PWD_SMS = 311;
	public static final String METHOD_NAME_FORGOT_PWD_SMS = "getForgetPwdFeedbackBySMS";
	public static final String RESULT_PROPERTY_FORGOT_PWD_SMS = "getForgetPwdFeedbackBySMSResult";
	public static final int REQ_ID_FORGOT_PWD_EMAIL = 312;
	public static final String METHOD_NAME_FORGOT_PWD_EMAIL = "getForgetPwdFeedbackByEmail";
	public static final String RESULT_PROPERTY_FORGOT_PWD_EMAIL = "getForgetPwdFeedbackByEmailResult";

	// �û�ע��
	public static final int REQ_ID_REGISTRATION_USER_INFO = 305;
	public static final String METHOD_NAME_REGISTRATION_USER_INFO = "RegisterCustomerWithPlain1";
	public static final String RESULT_PROPERTY_REGISTRATION_USER_INFO = "RegisterCustomerWithPlain1Result";

	// �޸��û�ע����Ϣ
	public static final int REQ_ID_UPDATE_USER_ALL_INFO = 306;
	public static final String METHOD_NAME_UPDATE_USER_ALL_INFO = "UpdateUserInfoWithLicensePictureAndProfession";
	public static final String RESULT_PROPERTY_UPDATE_USER_ALL_INFO = "UpdateUserInfoWithLicensePictureAndProfessionResult";

	// ��ȡ�û���Ϣ��֤״
	public static final int REQ_ID_CHECK_USER_INFO_STATUS = 313;
	public static final String METHOD_NAME_CHECK_USER_INFO_STATUS = "GetUserInfoVerifyStatus";
	public static final String RESULT_PROPERTY_CHECK_USER_INFO_STATUS = "GetUserInfoVerifyStatusResult";

	// ��д�û�ע����Ϣ(ҽԺ��������)
	public static final int REQ_ID_UPDATE_USER_HOSPITAL = 307;
	public static final String METHOD_NAME_UPDATE_USER_HOSPITAL = "UpdateCustomerWithPlain";
	public static final String RESULT_PROPERTY_UPDATE_USER_HOSPITAL = "UpdateCustomerWithPlainResult";

	// ��д�û�ע����Ϣ(����)
	public static final int REQ_ID_UPDATE_USER_DEPARTMENT = 308;
	public static final String METHOD_NAME_UPDATE_USER_DEPARTMENT = "UpdateDepartmentWithPlain";
	public static final String RESULT_PROPERTY_UPDATE_USER_DEPARTMENT = "UpdateDepartmentWithPlainResult";
	
	// ��ȡresearch��ϸҳ��
	public static final int REQ_ID_GET_RESEARCH_DETAIL = 600;
	public static final String METHOD_NAME_GET_RESEARCH_DETAIL = "GetPaperDetailById";
	public static final String RESULT_PROPERTY_GET_RESEARCH_DETAIL = "GetPaperDetailByIdResult";
	public static final String KEY_PAPER_ID = "paperId";
	
	// ������ȡ����
	public static final int REQ_ID_JOURNAL_SUBSCIBE = 601;
	public static final String METHOD_NAME_JOURNAL_SUBSCIBE = "UpdateUsersPeriodicalStatusByUserId";
	public static final String RESULT_PROPERTY_JOURNAL_SUBSCIBE = "UpdateUsersPeriodicalStatusByUserIdResult";
	public static final String KEY_SING = "sign";
	public static final String KEY_SUBSCIBE_USER_NAME = "userID";
	public static final String KEY_SUBSCIBE_PERIODICAL_ID = "periodicalId";
	public static final String KEY_SUBSCIBE_STATUS = "subscibeStatus";
	// ����ר����
	public static final int REQ_ID_SPECIAL_COLLECTION = 602;
	public static final String METHOD_NAME_SPECIAL_COLLECTION = "GetPaperSpecialAlbumByInviteCode";
	public static final String RESULT_PROPERTY_SPECIAL_COLLECTION = "GetPaperSpecialAlbumByInviteCodeResult";
	public static final String KEY_SPECIAL_USER_NAME = "userId";
	public static final String KEY_SPECIAL_INVITE_CODE = "inviteCode";
	public static final String KEY_SPECIAL_DEPARTMENT = "department";
	// ȫ������
	public static final int REQ_ID_DOWNLOAD_FULLTEXT = 603;
	public static final String METHOD_NAME_DOWNLOAD_FULLTEXT = "DownloadFullText";
	public static final String RESULT_PROPERTY_DOWNLOAD_FULLTEXT = "DownloadFullTextResult";
	public static final String KEY_FULLTEXT_USER_NAME = "userId";
	public static final String KEY_FULLTEXT_PAPER_ID = "paperId";
	public static final String KEY_FULLTEXT_CULTURE = "cultureInfo";
	// ����ȫ������
	public static final int REQ_ID_REQUEST_FULLTEXT = 604;
	public static final String METHOD_NAME_REQUEST_FULLTEXT = "AddUserFulltextDownloadRequest";
	public static final String RESULT_PROPERTY_REQUEST_FULLTEXT = "AddUserFulltextDownloadRequestResult";
	public static final String KEY_FULLTEXT_REQUEST_USER_NAME = "userId";
	public static final String KEY_FULLTEXT_REQUEST_PAPER_ID = "paperId";
	/* ----------------------end-------------------- */

	/**
	 * ��¼
	 */
	/* ----------------------start-------------------- */

	// public static final int REQ_ID_LOGIN_CHECK_USERNAME_AND_PASSWORD = 401;
	// public static final String METHOD_NAME_LOGIN_CHECK_USERNAME_AND_PASSWORD
	// = "GetCustomerInfo_new";
	// public static final String
	// RESULT_PROPERTY_LOGIN_CHECK_USERNAME_AND_PASSWORD =
	// "GetCustomerInfo_newResult";

	public static final int REQ_ID_LOGIN_CHECK_USERNAME_AND_PASSWORD = 401;
	public static final String METHOD_NAME_LOGIN_CHECK_USERNAME_AND_PASSWORD = "LoginCustomerWithPlain2";
	public static final String RESULT_PROPERTY_LOGIN_CHECK_USERNAME_AND_PASSWORD = "LoginCustomerWithPlain2Result";

	public static final int REQ_ID_GET_IMAGE_PATH = 402;
	public static final String METHOD_NAME_GET_IMAGE_PATH = "GetImagePath";
	public static final String RESULT_PROPERTY_GET_IMAGE_PATH = "GetImagePathResult";

	/** ΢����½ */
	public static final int REQ_ID_WEIBO_LOGIN = 403;
	public static final String METHOD_NAME_WEIBO_LOGIN = "RegisterCustomerByThread2";
	public static final String RESULT_PROPERTY_WEIBO_LOGIN = "RegisterCustomerByThread2Result";
	/** ע��deviceToken */
	public static final int REQ_ID_UPDATEUSERDEVICETOKEN = 404;
	public static final String METHOD_NAME_UPDATEUSERDEVICETOKEN = "UpdateUserDeviceToken";
	public static final String RESULT_PROPERTY_UPDATEUSERDEVICETOKEN = "UpdateUserDeviceTokenResult";

	public static final int REQ_ID_UPDATE_PAPERS_COUNT = 405;
	public static final String METHOD_NAME_UPDATE_PAPERS_COUNT = "GetPaperUpdatedCount";
	public static final String RESULT_PROPERTY_UPDATE_PAPERS_COUNT = "GetPaperUpdatedCountResult";
	

	/* ----------------------end-------------------- */

	public static final String KEY_RESEARCH_SEARCH_KWD = "keyword";
	public static final String KEY_RESEARCH_SEARCH_TIT = "inTitle";
	public static final String KEY_RESEARCH_SEARCH_AU = "inAuthor";
	public static final String KEY_RESEARCH_SEARCH_ENT = "inEntities";
	public static final String KEY_RESEARCH_SEARCH_PUB = "inPublication";
	public static final String KEY_RESEARCH_SEARCH_CATE = "inCategory";
	public static final String KEY_RESEARCH_SEARCH_IKEY = "inKeywords";
	public static final String KEY_RESEARCH_SEARCH_PS = "pageSize";
	public static final String KEY_RESEARCH_SESRCH_PNUM = "pageNum";

	public static final String KEY_EVENT_KEYWORD = "keyword";
	public static final String KEY_EVENT_SEARCH_TITLE = "inTitle";
	public static final String KEY_EVENT_SEARCH_REGION = "inRegion";
	public static final String KEY_EVENT_SEARCH_ORGAN = "inOrganizer";
	public static final String KEY_EVENT_SEARCH_CATEGORY = "inCategory";
	public static final String KEY_EVENT_SEARCH_PAGESIZE = "pageSize";
	public static final String KEY_EVENT_SEARCH_PAGNUM = "pageNum";

	public static final int REQ_ID_RESEARCH_SEARCH = 21;
	public static final String REQ_ID_RESEARCH_MD = "AdvancedRetrieveArticles";
	public static String SOAP_ACTION_GETRESEARCH = NAMESPACE
			+ REQ_ID_RESEARCH_MD;

	public static final int REQ_ID_EVENT_SEARCH = 22;
	public static final String REQ_ID_EVENT_SEARCH_MD = "AdvancedRetrieveEvents_new";
	public static String SOAP_ACTION_RETRIE = NAMESPACE
			+ REQ_ID_EVENT_SEARCH_MD;

	/** ��ҩ��Ŀ¼ */
	public static final int REQ_ID_DRUG_EDL = 500;
	// public static final String METHOD_ID_DRUG_EDL = "GetDrugEDLs";
	public static final String METHOD_ID_DRUG_EDL_BY_REGION = "GetDrugEDLsByRegion";
	// public static String SOAP_ACTION_DRUG_EDL = NAMESPACE +
	// METHOD_ID_DRUG_EDL_BY_REGION;

	/** ҽ���б� */
	public static final int REQ_ID_DRUG_REIMBURSEMENT = 501;
	public static final String METHOD_ID_DRUG_REIMBURSEMENT_BY_REGION = "GetDrugReimbursementsByRegion";
	public static String SOAP_ACTION_DRUG_REIMBURSEMENT = NAMESPACE
			+ METHOD_ID_DRUG_REIMBURSEMENT_BY_REGION;

	/** �۸� */
	public static final int REQ_ID_DRUG_MANUFACTURE_PRICE = 502;
	public static final String METHOD_ID_DRUG_MANUFACTURE_PRICE_BY_REGION = "GetPricingsByRegion";
	public static String SOAP_ACTION_DRUG_MANUFACTURE_PRICE = NAMESPACE
			+ METHOD_ID_DRUG_MANUFACTURE_PRICE_BY_REGION;

	/** �۸� */
	public static final int REQ_ID_DRUG_ACADEMIC = 503;
	public static final String METHOD_ID_DRUG_ACADEMIC = "GetAcademicActivities_new";
	public static String SOAP_ACTION_DRUG_ACADEMIC = NAMESPACE
			+ METHOD_ID_DRUG_ACADEMIC;
	/** ���� */

	public static final String URLInvite = URL
			+ "customerservice.asmx?op=inviteUser";
	public static final String URLGETInvite = URL
			+ "customerservice.asmx?op=getInviteUser";
	public static final int REQ_ID_INVITE = 505;
	public static final int REQ_ID_GET_INVITE = 506;
	public static final String METHOD_ID_INVITE_FRIEND = "inviteUser";
	public static String SOAP_ACTION_METHOD_ID_INVITE_FRIEND = NAMESPACE
			+ METHOD_ID_INVITE_FRIEND;
	public static final String METHOD_ID_GET_INVITE_FRIEND = "getInvitedUser";
	public static String SOAP_ACTION_METHOD_ID_GET_INVITE_FRIEND = NAMESPACE
			+ METHOD_ID_GET_INVITE_FRIEND;
	private static String METHOD_PAPER_LIST = "GetPaperList";
	private static String METHOD_GetUsersPeriodicalInfoByUserId = "GetUsersPeriodicalInfoByUserId";
	public static final int REQ_ID_RETRIEVE_COAUTHOR_ARTICLE = 504;
	public static final String METHOD_RETRIEVE_COAUTHOR_ARTICLE = "RetrieveCoauthorArticles";
	public static final String KEY_DOCTOR_ID = "doctorId";
	public static final String KEY_COAUTHOR_ID = "coauthorId";
	public static final String KEY_INVITE_GBUSERNAME = "gbUserName";

	public static final String KEY_INVITE_USERNAME = "name";
	public static final String KEY_INVITE_EMAIL = "email";
	public static final String KEY_INVITE_PHONE = "phone";
	public static final int REQ_ID_GET_IMAGELIST = 507;
	public static final int REQ_ID_GET_IMAGELIST_TOP = 508;
	public static final String METHOD_ID_GET_IMAGELIST = "getImageNewsList";

	private static final String METHOD_ID_GET_IMAGELIST_TOP = "getNewStick";
	public static final int REQ_ID_GET_PAPER_LIST = 509;
	public static final int REQ_ID_GetUsersPeriodicalInfoByUserId = 510;
//ҩ���໥����
	//public static final String URLINTERACTION = JOURANLURL+"/druginteractionservice.asmx";
		public static final int REQ_ID_GET_INTERACTIONFIND = 701;
		public static final String METHOD_NAME_INTERACTIONFIND = "FindDrugInteractionWithProductInfo";
		public static final String RESULT_PROPERTY_INTERACTIONS = "FindDrugInteractionWithProductInfoResult";
		public static final String COUNT = "count";
		public static final String LASTSYNCSTAMP = "lastSyncStamp";
		public static final int REQ_ID_GET_INTERACTION = 702;
		public static final String METHOD_NAME_INTERACTION = "SynchronizeDrugInteraction";
		public static final String RESULT_PROPERTY_INTERACTION = "SynchronizeDrugInteractionResult";
		public static final String CULTUREINFO = "cultureInfo";
		public static final String PIDS = "pids";
		public static final int REQ_ID_GET_INTERACTIONRELATIONSHIP = 703;
		public static final String METHOD_NAME_INTERACTIONRELATIONSHIP = "SynchronizeDrugInteractionRelationship";
		public static final String RESULT_PROPERTY_INTERACTIONRELATIONSHIP = "SynchronizeDrugInteractionRelationshipResult";
		public static final int REQ_ID_GET_DRUGPRODUCT = 704;
		public static final String METHOD_NAME_DRUGPRODUCT = "SynchronizeDrugProduct";
		public static final String RESULT_PROPERTY_DRUGPRODUCT = "SynchronizeDrugProductResult";
	public static void debug(String tag, String msg) {
		if (DEBUG)
			Log.d(tag, msg);
	}

	/**
	 * ��ݷ���id��ȡ����str
	 * 
	 * @return ����str
	 * */
	public static String getMethod(int id) {
		switch (id) {
		case REQ_ID_GetUsersPeriodicalInfoByUserId:
			return METHOD_GetUsersPeriodicalInfoByUserId;
		case REQ_ID_GET_PAPER_LIST:
			return METHOD_PAPER_LIST ;
		case REQ_ID_VERSION:
			return METHOD_VERSION;
		case REQ_ID_SEND_MAIL:
			return METHOD_SEND_MAIL;
			// ��ҩ��ȫ>>>>>>>>>>
		case REQ_ID_GET_DRUGALERT_TOP_20:
		case REQ_ID_GET_DRUGALERTS_NEW_DATA:
		case REQ_ID_GET_DRUGALERTS_MORE_DATA:
		case REQ_ID_GET_DRUGALERT_BY_TYPEID:
			return METHOD_NAME_GET_DRUGALERTS_NEW_DATA;
		case REQ_ID_GET_DRUGALERT_GET_DETAIL:
			return METHOD_NAME_GET_DETAIL;
		case REQ_ID_GET_DRUGALERT_NEWESTCOUNT:
			return METHOD_NAME_GET_DRUGALERT_NEWESTCOUNT;

			// ��ҵ����>>>>>>>>
		case REQ_ID_CHECK_NEWS_CATEGORIES:
			return METHOD_NAME_CHECK_NEWS_CATEGORIES;
		case REQ_ID_GET_NEWS_CATEGORIES_BY_BIGCATEGROY:
			return METHOD_NAME_GET_NEWS_CATEGORIES_BY_BIGCATEGROY;
		case REQ_ID_GET_NEWS_TOP_20:
		case REQ_ID_GET_NEWS_OF_CATEGORY_TOP_DATA:
		case REQ_ID_GET_NEWS_OF_CATEGORY_MORE_DATA:
		case REQ_ID_GET_NEWS_BY_SEARCH_VALUE:
			return METHOD_NAME_GET_NEWS_BYID_DESC;
		case REQ_ID_GET_NEWSDETAIL_BY_ID:
			return METHOD_NAME_GET_NEWSDETAIL_BY_ID;
			// ע��>>>>>>>>>>>
		case REQ_ID_CHECK_USERNAME_ISVALID:
			return METHOD_NAME_CHECK_USERNAME_ISVALID;
		case REQ_ID_REGISTRATION_SUBMIT_INFO:
			return METHOD_NAME_REGISTRATION_SUBMIT_INFO;
		case REQ_ID_UPDATE_PASSWORD:
			return METHOD_NAME_UPDATE_PASSWORD;
		case REQ_ID_GET_REGISTARTIONS_DATA:
			return METHOD_NAME_GET_REGISTARTIONS_DATA;
		case REQ_ID_REGISTRATION_USER_INFO:
			return METHOD_NAME_REGISTRATION_USER_INFO;
		case REQ_ID_UPDATE_USER_ALL_INFO:
			return METHOD_NAME_UPDATE_USER_ALL_INFO;
		case REQ_ID_UPDATE_USER_HOSPITAL:
			return METHOD_NAME_UPDATE_USER_HOSPITAL;
		case REQ_ID_UPDATE_USER_DEPARTMENT:
			return METHOD_NAME_UPDATE_USER_DEPARTMENT;
		case REQ_ID_FORGOT_PWD:
			return METHOD_NAME_FORGOT_PWD;
		case REQ_ID_FORGOT_PWD_SMS:
			return METHOD_NAME_FORGOT_PWD_SMS;
		case REQ_ID_FORGOT_PWD_EMAIL:
			return METHOD_NAME_FORGOT_PWD_EMAIL;
		case REQ_ID_CHECK_USER_INFO_STATUS:
			return METHOD_NAME_CHECK_USER_INFO_STATUS;
			// ͨ���ʼ����߶���ȡ������ add by Terry
		case REQ_ID_GET_PWD:
			return METHOD_NAME_GET_PWD;
			// ��¼>>>>>>>>
		case REQ_ID_LOGIN_CHECK_USERNAME_AND_PASSWORD:
			return METHOD_NAME_LOGIN_CHECK_USERNAME_AND_PASSWORD;
		case REQ_ID_GET_CUSTOMER_INFO:
			return METHOD_GET_CUSTOMER_INFO;
		case REQ_ID_GET_IMAGE_PATH:
			return METHOD_NAME_GET_IMAGE_PATH;

		case REQ_ID_WEIBO_LOGIN:
			return METHOD_NAME_WEIBO_LOGIN;
			// else
			case REQ_ID_UPDATE_PAPERS_COUNT:
			return METHOD_NAME_UPDATE_PAPERS_COUNT;
			// else
		case REQ_ID_GET_PROFILE:
			return GET_PROFILE;
		case REQ_ID_GET_COAUTHOR:
			return GET_COAUTHOR_BY_DOCTOR;
		case REQ_ID_RETRIEVE_ARTICLE:
			return RETRIEVE_ARTICLE_BY_DOCTOR;
		case REQ_ID_EVENT:
			return METHOD_NAME_EVENT;
		case REQ_ID_EVENT_INF:
			return METHOD_NAME_EVENTINF;
		case REQ_ID_SURVEY_GETQES:
			return METHOD_NAME_SURGETQES;
		case REQ_ID_SURVEY_SUBMIT:
			return METHOD_NAME_SUBMIT;
		case REQ_ID_RESEARCH_TA:
			return METHOD_NAME_GETTA;
		case REQ_ID_RESEARCH_TART:
			return REQ_ID_RESEARCH_GETART;
		case REQ_ID_RESEARCH_GETINFID:
			return REQ_ID_RESEARCH_GETINF;
		case REQ_ID_FEED_BACK:
			return METHOD_USER_FEED_BACK;
		case REQ_ID_SYNC_VIEW_HISTORY:
			return METHOD_NEW_VIEW_HISTORY;
		case REQ_ID_RESEARCH_SEARCH:
			return REQ_ID_RESEARCH_MD;
		case REQ_ID_EVENT_SEARCH:
			return REQ_ID_EVENT_SEARCH_MD;
		case REQ_ID_DRUG_EDL:
			return METHOD_ID_DRUG_EDL_BY_REGION;
		case REQ_ID_DRUG_REIMBURSEMENT:
			return METHOD_ID_DRUG_REIMBURSEMENT_BY_REGION;
		case REQ_ID_DRUG_MANUFACTURE_PRICE:
			return METHOD_ID_DRUG_MANUFACTURE_PRICE_BY_REGION;
		case REQ_ID_DRUG_ACADEMIC:
			return METHOD_ID_DRUG_ACADEMIC;
		case REQ_ID_RETRIEVE_COAUTHOR_ARTICLE:
			return METHOD_RETRIEVE_COAUTHOR_ARTICLE;
		case REQ_ID_GET_NEWS_COUNT:
			return METHOD_GET_NEWS_COUNT;
		case REQ_ID_GETMSG:
			return REQ_ID_GETMESSAGE;
		case REQ_ID_FAVORITE:
			return REQ_ID_SYNCFAV_METHOD;
		case REQ_ID_GET_CUSTOMER_DOWN:
			return METHOD_GET_CUSTOMER_DOWN;
		case REQ_ID_NEW_VERSION:
			return METHOD_NEW_VERSION;
		case REQ_ID_APP_NEW_VERSION:
			return METHOD_APP_NEW_VERSION;
		case REQ_ID_DATA_NEW_VERSION:
			return METHOD_DATA_NEW_VERSION;
		case REQ_ID_RECORD_USER_INFO:
			return METHOD_RECORD_USER_INFO;
		case REQ_ID_SYNC_HISTORY_NEW:
			return METHOD_SYNC_HISTORY_NEW;
		case REQ_ID_GET_CATEGORY_UPDATE:
			return METHOD_GET_CATEGORY_UPDATE;
		case REQ_ID_GET_ZIP_FILE:
			return METHOD_GET_ZIP_FILE;
		case REQ_ID_GETGOOLE:
			return REQ_ID_GETGOOLEMSG;
		case REQ_ID_MARKET:
			return METHOD_GET_MARKET_INFO;
		case REQ_ID_SURVEY:
			return METHOD_GET_SURVEY_INFO;
		case REQ_ID_UPDATE_PAY_INFO:
			return METHOD_UPDATE_PAY_INFO;
		case REQ_ID_GET_SURVEY_RECHECK:
			return METHOD_GET_SURVEY_RECHECK;
		case REQ_ID_NEW_SURVEY_SUBMIT:
			return METHOD_GET_SURVEY_SUBMIT;
		case REQ_ID_GET_SURVEY_HISTORY:
			return METHOD_GET_SURVEY_HISTORY;
		case REQ_ID_GET_PAY_INFO:
			return METHOD_GET_PAY_INFO;
		case REQ_ID_UPDATE_INVITE_CODE:
			return METHOD_UPDATE_INVITE_CODE;
		case REQ_ID_CHECK_TA:
			return REQ_ID_CHECK_METHOD;
		case REQ_ID_INVITE:
			return METHOD_ID_INVITE_FRIEND;
		case REQ_ID_GET_INVITE:
			return METHOD_ID_GET_INVITE_FRIEND;
		case REQ_ID_GET_IMAGELIST:
			return METHOD_ID_GET_IMAGELIST;
		case REQ_ID_GET_IMAGELIST_TOP:
			return METHOD_ID_GET_IMAGELIST_TOP;
		case REQ_ID_GET_NEWS_TOP_MORE_BY_ID:
			return METHOD_ID_GET_IMAGELIST;
		case REQ_ID_NEWS_DETAIL:
			return METHOD_REQ_ID_NEWS_DETAIL;
		case REQ_ID_GET_RESEARCH_DETAIL:
			return METHOD_NAME_GET_RESEARCH_DETAIL;
		case REQ_ID_JOURNAL_SUBSCIBE:
			return METHOD_NAME_JOURNAL_SUBSCIBE;
			
		case REQ_ID_GET_INTERACTIONFIND:
			return METHOD_NAME_INTERACTIONFIND;
		case REQ_ID_GET_INTERACTION:
			return METHOD_NAME_INTERACTION;
		case REQ_ID_SPECIAL_COLLECTION:
			return METHOD_NAME_SPECIAL_COLLECTION;
		case REQ_ID_DOWNLOAD_FULLTEXT:
			return METHOD_NAME_DOWNLOAD_FULLTEXT;
		case REQ_ID_GET_INTERACTIONRELATIONSHIP:
			return METHOD_NAME_INTERACTIONRELATIONSHIP;
		case REQ_ID_GET_DRUGPRODUCT:
			return METHOD_NAME_DRUGPRODUCT;
		case REQ_ID_REQUEST_FULLTEXT:
			return METHOD_NAME_REQUEST_FULLTEXT;
		}
		return "";
	}
}
