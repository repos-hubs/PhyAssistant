package com.jibo.data;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.MfgrPricePaser;

/**
 * 使用工厂模式对不同请求ID的SOAP数据进行解析 * @author peter.pan
 * */
public class SoapPaserFactory {

	public static Object paserData(int id, SoapSerializationEnvelope response) {
		SoapDataPaser paser = null;
		switch (id) {
		case SoapRes.REQ_ID_VERSION:
			paser = new VersionDataParser();
			break;
		case SoapRes.REQ_ID_SEND_MAIL:
			paser = new EmailAdancedPaser();
			break;
		// 用药安全
		case SoapRes.REQ_ID_GET_DRUGALERT_TOP_20:
		case SoapRes.REQ_ID_GET_DRUGALERTS_NEW_DATA:
		case SoapRes.REQ_ID_GET_DRUGALERTS_MORE_DATA:
			paser = new DrugAlertDataPaser();
			break;
		case SoapRes.REQ_ID_GET_DRUGALERT_GET_DETAIL:
			paser = new DrugAlertDetailListPaser();
			break;
		case SoapRes.REQ_ID_GET_DRUGALERT_BY_TYPEID:
			paser = new DrugAlertByTypeIdPaser();
			break;
		case SoapRes.REQ_ID_GET_DRUGALERT_NEWESTCOUNT:
			paser = new DrugAlertNewestCountPaser();
			break;

		/** 行业新闻 */
		case SoapRes.REQ_ID_GET_NEWS_TOP_20:// 最新动态最新20条
			// 最新动态加载更多
		case SoapRes.REQ_ID_GET_NEWS_OF_CATEGORY_TOP_DATA:// 类别下新闻获取更新或者最新20条
		case SoapRes.REQ_ID_GET_NEWS_OF_CATEGORY_MORE_DATA:// 类别下新闻或者搜索下新闻加载更多
		case SoapRes.REQ_ID_GET_NEWS_BY_SEARCH_VALUE:// 关键字搜索新闻列表最新20条
			paser = new NewsListPaser();
			break;
		case SoapRes.REQ_ID_CHECK_NEWS_CATEGORIES:
			paser = new NewsCheckCategoriesPaser();
			break;
		case SoapRes.REQ_ID_GET_NEWS_CATEGORIES_BY_BIGCATEGROY:
			paser = new NewsGetCategoriesByBigCategoryPaser();
			break;
		case SoapRes.REQ_ID_GET_NEWSDETAIL_BY_ID:
			paser = new NewsGetInfoByIdPaser();
			break;
		// 注册
		case SoapRes.REQ_ID_CHECK_USERNAME_ISVALID:
			paser = new RegistrationCheckTheUserIsValidPaser();
			break;
		case SoapRes.REQ_ID_REGISTRATION_SUBMIT_INFO:
			paser = new RegistrationSubmitInfoPaser();
			break;
		case SoapRes.REQ_ID_UPDATE_PASSWORD:
			paser = new UpdatePasswordPaser();
			break;
		case SoapRes.REQ_ID_GET_REGISTARTIONS_DATA:
			paser = new RegistrationGetCustomerInfoPaser();
			break;

		case SoapRes.REQ_ID_REGISTRATION_USER_INFO:
			paser = new RegistrationUserInfoPaser();
			break;

		case SoapRes.REQ_ID_UPDATE_USER_ALL_INFO:
			paser = new UpdateUserAllInfoPaser();
			break;
		case SoapRes.REQ_ID_UPDATE_USER_HOSPITAL:
			paser = new RegistrationUserInfo_hospitalPaser();
			break;
		case SoapRes.REQ_ID_UPDATE_USER_DEPARTMENT:
			paser = new UpdateUserDepartmentPaser();
			break;
		case SoapRes.REQ_ID_FORGOT_PWD:
			paser = new ForgotPwdPaser();
			break;
		case SoapRes.REQ_ID_FORGOT_PWD_SMS:
			paser = new ForgotPwdPaser_SMS();
			break;
		case SoapRes.REQ_ID_FORGOT_PWD_EMAIL:
			paser = new ForgotPwdPaser_Email();
			break;
		case SoapRes.REQ_ID_CHECK_USER_INFO_STATUS:
			paser = new CheckUserInfoStatusPaser();
			break;

		// 登录
		case SoapRes.REQ_ID_LOGIN_CHECK_USERNAME_AND_PASSWORD:
			paser = new LoginCheckPaser();
			break;
		case SoapRes.REQ_ID_GET_CUSTOMER_INFO:
			paser = new CustomerInfoPaser();
			break;
		case SoapRes.REQ_ID_WEIBO_LOGIN:// 微博登陆
			paser = new WeiboLoginPaser();
			break;
		case SoapRes.REQ_ID_UPDATE_PAPERS_COUNT:// 微博登陆
			paser = new UpdateParser();
			break;
		// else
		case SoapRes.REQ_ID_GET_PROFILE:
			paser = new ProfilePaser();
			break;
		case SoapRes.REQ_ID_GET_COAUTHOR:
			paser = new CoauthorPaser();
			break;
		case SoapRes.REQ_ID_RETRIEVE_ARTICLE:
			paser = new RetriveArticlePaser();
			break;

		case SoapRes.REQ_ID_EVENT:
			paser = new EventListParser();
			break;
		case SoapRes.REQ_ID_EVENT_INF:
			paser = new EventInfParse();
			break;
		case SoapRes.REQ_ID_SURVEY_GETQES:
			paser = new GetSurveyQueParser();
			break;
		case SoapRes.REQ_ID_SURVEY_SUBMIT:
			paser = new SubmitResultParser();
			break;
		case SoapRes.REQ_ID_RESEARCH_TA:
			paser = new ResearchParser();
			break;
		case SoapRes.REQ_ID_CHECK_TA:
			paser = new ResearchCheckParser();
			break;
		case SoapRes.REQ_ID_RESEARCH_TART:
			paser = new ResearchGetArtParser();
			break;

		case SoapRes.REQ_ID_RESEARCH_GETINFID:
			paser = new ResearchGetInf();
			break;
		case SoapRes.REQ_ID_FEED_BACK:
			paser = new FeebBackPaser();
			break;
		case SoapRes.REQ_ID_EVENT_SEARCH:
			paser = new GetEventSearchPaser();
			break;//
		case SoapRes.REQ_ID_RESEARCH_SEARCH:
			paser = new GetResearchSearch();
			break;// GetResearchSearch
		case SoapRes.REQ_ID_SYNC_VIEW_HISTORY:
		case SoapRes.REQ_ID_SYNC_HISTORY_NEW:
			paser = new SyncHistoryPaser();
			break;
		case SoapRes.REQ_ID_DRUG_EDL:
			paser = new DrugEDLPaser();
			break;
		case SoapRes.REQ_ID_DRUG_REIMBURSEMENT:
			paser = new DrugReimbursementPaser();
			break;
		case SoapRes.REQ_ID_DRUG_MANUFACTURE_PRICE:
			paser = new MfgrPricePaser();
			break;
		case SoapRes.REQ_ID_DRUG_ACADEMIC:
			paser = new DrugAcademicPaser();
			break;
		case SoapRes.REQ_ID_RETRIEVE_COAUTHOR_ARTICLE:
			paser = new RetrieveCoauthorPaser();
			break;
		case SoapRes.REQ_ID_GET_NEWS_COUNT:
			paser = new NewsCountPaser();
			break;
		case SoapRes.REQ_ID_GET_CUSTOMER_DOWN:
			paser = new PackageInfoPaser();
			break;
		case SoapRes.REQ_ID_GETMSG:
			paser = new NewGetMsgPaser();
			break;
		case SoapRes.REQ_ID_FAVORITE:
			paser = new NewSyncFavPaser();
			break;
		case SoapRes.REQ_ID_GET_IMAGE_PATH:
			paser = new GetAdvertisingImagePaser();
			break;
		case SoapRes.REQ_ID_NEW_VERSION:
			paser = new NewDBDataPaser();
			break;
		case SoapRes.REQ_ID_APP_NEW_VERSION:
			paser = new NewAPPVersionDataPaser();
			break;
		case SoapRes.REQ_ID_DATA_NEW_VERSION:
			paser = new NewDATAVersionDataPaser();
			break;
		case SoapRes.REQ_ID_GET_CATEGORY_UPDATE:
			paser = new GetCategoryUpdatePaser();
			break;
		case SoapRes.REQ_ID_RECORD_USER_INFO:
			return null;
		case SoapRes.REQ_ID_GET_ZIP_FILE:
			paser = new GetZipFilePaser();
			break;
		case SoapRes.REQ_ID_GETGOOLE:
			paser = new GetgoogleParser();
			break;
		case SoapRes.REQ_ID_MARKET:
			paser = new MarketPaserAdapter();
			break;
		case SoapRes.REQ_ID_SURVEY:
			paser = new SurveyInfoPaser();
			break;
		case SoapRes.REQ_ID_UPDATE_PAY_INFO:
			paser = new PayInfoSubmitPaser();
			break;
		case SoapRes.REQ_ID_GET_SURVEY_RECHECK:
			paser = new SurveyQuestionPaser();
			break;
		case SoapRes.REQ_ID_NEW_SURVEY_SUBMIT:
			paser = new SurveySubmitPaser();
			break;
		case SoapRes.REQ_ID_GET_SURVEY_HISTORY:
			paser = new SurveyMineHistory();
			break;
		case SoapRes.REQ_ID_GET_PAY_INFO:
			paser = new SurveyGetPayInfo();
			break;
		case SoapRes.REQ_ID_UPDATE_INVITE_CODE:
			paser = new UpdateInviteCodePaser();
			break;
		case SoapRes.REQ_ID_INVITE:
			paser = new InviteFriendsPaser();
			break;
		case SoapRes.REQ_ID_GET_INVITE:
			paser = new GetFriendsPaser();
			break;
		case SoapRes.REQ_ID_GET_NEWS_TOP_MORE_BY_ID:
		case SoapRes.REQ_ID_GET_IMAGELIST:
		case SoapRes.REQ_ID_GET_PAPER_LIST:
		case SoapRes.REQ_ID_GetUsersPeriodicalInfoByUserId:
		case SoapRes.REQ_ID_GET_IMAGELIST_TOP:
			paser = new EntityObjPaser(id);
			break;
		case SoapRes.REQ_ID_NEWS_DETAIL:
			paser = new NewsDetailParser();
			break;
		case SoapRes.REQ_ID_GET_RESEARCH_DETAIL:
			paser = new PaperDetailPaser();
			break;
		case SoapRes.REQ_ID_JOURNAL_SUBSCIBE:
			paser = new JournalSubscibePaser();
			break;
		// 药物相互作用
		case SoapRes.REQ_ID_GET_INTERACTIONFIND:
			paser = new InteractionFindPaser();
			break;
		// 药物相互作用
		case SoapRes.REQ_ID_GET_INTERACTION:
			paser = new InteractionPaser();
			break;
		// 药物相互作用
		case SoapRes.REQ_ID_GET_INTERACTIONRELATIONSHIP:
			paser = new InteractionRelationshipPaser();
			break;
		case SoapRes.REQ_ID_SPECIAL_COLLECTION:
			paser = new SpecialCollectionPaser();
			break;
		// 药物相互作用
		case SoapRes.REQ_ID_GET_DRUGPRODUCT:
			paser = new InteractionDrugProductPaser();
			break;
		case SoapRes.REQ_ID_DOWNLOAD_FULLTEXT:
			paser = new DownloadFullTextPaser();
			break;
		}
		paser.paser(response);
		return paser;
	}
}
