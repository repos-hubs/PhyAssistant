package com.jibo.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.api.android.GBApp.R;
/**
 * �Ի������Դ
 * */
public class DialogRes {

	/** ��������ʱ�Ի��� */
	public static final int DIALOG_ID_NET_CONNECT = 1;
	/** ȡ�����ضԻ��� */
	public static final int DIALOG_ID_CANCEL_DOWNLOAD = 2;
	/** ע�������� */
	public static final int DIALOG_QUIT_PROMPT = 3;
	/** SD��û׼���� */
	public static final int DIALOG_ID_SDCARD_NOT_AVAILABLE = 4;
	/** SD��û������ */
	public static final int DIALOG_ID_NO_DATA = 5;
	/** û��doctorId */
	public static final int DIALOG_ID_NO_LISENCE = 6;
	/** �޸Ŀ�����Ϣ */
	public static final int DIALOG_ID_CHANGE_DPT = 7;
	/** ��ʾ���µ����ݰ����Ը��� */
	public static final int DIALOG_ID_NEW_DATA_AVAILABLE = 8;
	/** �����ļ�ȱʧ */
	public static final int DIALOG_ID_DATA_ERROR = 9;
	/** �������ݸ��� */
	public static final int DIALOG_ID_DATA_FULL = 10;
	/** ȡ������Ӧ�� */
	public static final int DIALOG_ID_CANCEL_INSTALL = 11;
	/** �����ύʧ�� */
	public static final int DIALOG_ID_SURVEY_SUBMIT_ERROR = 12;
	/** �����ύ���ύ�� */
	public static final int DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED = 13;
	/** �����ύ�������� */
	public static final int DIALOG_ID_SURVEY_SUBMIT_FULL = 14;
	/** û�п���ʹ�õ����� */
	public static final int DIALOG_ID_NETWORK_NOT_AVALIABLE = 100;
	/** ����Ի���*/
	public static final int DIALOG_ID_ERR = 101;
	/** ����ʧ�ܶԻ���*/
	public static final int DIALOG_ID_DOWNLOAD_FAILED=102;
	/** ��ȡ���ݶԻ���*/
	public static final int DIALOG_WAITING_FOR_DATA=103;
	/** �������ݶԻ���*/
	public static final int DIALOG_UPDATE_FOR_DATA=104;
	/** �Զ���¼�Ի���*/
	public static final int DIALOG_ID_AUTO_LOGINING=105;
	/** ��¼�Ի���*/
	public static final int DIALOG_ID_LOGINING=106;
	/** ��¼ʧ�ܶԻ���*/
	public static final int DIALOG_ID_LOGINING_FAILD=107;
	/** �����û����Ƿ��ظ�*/
	public static final int DIALOG_ID_CHECK_USERNAME_ISVALID = 108;
	/** ע����Ϣ�������� */
	public static final int DIALOG_SEND_REGISTER_REQUEST = 109;
	/** �µ�Ӧ����Ҫ��װ */
	public static final int DIALOG_ID_UPDATE_NEW_VERSION=110;
	/** �°汾���밲װ */
	public static final int DIALOG_ID_NEED_UPDATE = 111;
	/** �ʾ�����ύ����� */
	public static final int DIALOG_ID_SHARE_AFTER_SUBMIT=112;
	/** �ʾ����û������� */
	public static final int DIALOG_ID_SURVEY_NOT_COMPLETE = 113;
	/** ж�����ݰ� */
	public static final int DIALOG_ID_PACKAGE_UNINSTALLING=114;
	/** ������û���� */
	public static final int DIALOG_ID_NO_INVITATION=115;
	/**  ����������  */
	public static final int DIALOG_ID_UPDATE_INVITECODE = 116;
	
	public static final int DIALOG_ERROR_PROMPT =129;
	/** ��ʾ�û�������*/
	public static final int DIALOG_ID_HAS_UPDATE=230;
	/** Ӧ��ǿ������*/
	public static final int DIALOG_ID_MUST_UPDATE=250;
	/** ��ʾ�û�û������*/
	public static final int DIALOG_ID_NO_UPDATE=231;
	/** �����ʾ�û������ȱʧ*/
	public static final int DIALOG_ID_SURVEY_DATA_LACK=232;
	/** ������Ϊ�� */
	public static final int DIALOG_ID_INVITECODE_BLANK = 233;
	/** ����������ʧ�� */
	public static final int DIALOG_ID_INVITECODE_FAIL = 234;
	
	/**��ʾ�û�ȥע��*/
	public static final int DIALOG_REGIST_NOTIFY=501;
	/**��ʾ�û�ȥ��������*/
	public static final int DIALOG_INVITE_NOTIFY=502;
	/**��ʾ�û�ȥ����*/
	public static final int DIALOG_GOTO_EVALUATE = 10000;
	/** �Զ���¼�Ի���*/
	public static final int DIALOG_ID_JUMP_TO=1232;
	/** ����doctorId */
	 public static final int DIALOG_ID_ERROR_LISENCE = 1011;
	 
	 /** ��ʾ�û��������г�ȥ���� */
	 public static final int DIALOG_ID_GOTO_INSTALL = 601;
	public static final int DIALOG_INVITATION_REQUEST = 602;
	 
	
	/**��ȡ�Ի���ı���
	 * @param dialogID �Ի���id
	 * @return ������������Ӧ����Դid
	 * */
	public static int getTitle(int dialogID)
	{
		switch(dialogID)
		{
		case DIALOG_ID_CANCEL_DOWNLOAD:
		case DIALOG_ID_NET_CONNECT:
		case DIALOG_ID_SDCARD_NOT_AVAILABLE:
		case DIALOG_ID_NO_DATA:
		case DIALOG_ID_HAS_UPDATE:
		case DIALOG_ID_NO_UPDATE:
		case DIALOG_ID_NO_LISENCE:
		case DIALOG_ID_CHANGE_DPT:
		case DIALOG_REGIST_NOTIFY:
		case DIALOG_ID_NEW_DATA_AVAILABLE:
		case DIALOG_ID_DATA_ERROR:
		case DIALOG_ID_DATA_FULL:
		case DIALOG_ID_CANCEL_INSTALL:
		case DIALOG_ID_UPDATE_NEW_VERSION:
		case DIALOG_ID_NEED_UPDATE:
		case DIALOG_ID_MUST_UPDATE:
		case DIALOG_INVITE_NOTIFY:
		case DIALOG_ID_ERROR_LISENCE:
		case DIALOG_ID_SURVEY_SUBMIT_ERROR:
		case DIALOG_ID_SURVEY_SUBMIT_FULL:
		case DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED:
		case DIALOG_ID_SHARE_AFTER_SUBMIT:
		case DIALOG_ID_SURVEY_NOT_COMPLETE:
		case DIALOG_ID_PACKAGE_UNINSTALLING:
		case DIALOG_ID_NO_INVITATION:
		case DIALOG_ID_UPDATE_INVITECODE:
		case DIALOG_ID_SURVEY_DATA_LACK:
		case DIALOG_ID_INVITECODE_BLANK:
		case DIALOG_ID_INVITECODE_FAIL:
			return R.string.generalprompt;
		case DIALOG_ID_ERR:
		case DIALOG_ID_DOWNLOAD_FAILED:
		case DIALOG_ERROR_PROMPT:
			return R.string.gbaerror;
		case DIALOG_GOTO_EVALUATE:
			return R.string.give_me_a_evaluate_title;
		case DIALOG_ID_NETWORK_NOT_AVALIABLE:
			return R.string.networktitle;
		case DIALOG_ID_LOGINING_FAILD:
			return R.string.errortint;
		case DIALOG_QUIT_PROMPT:
			return R.string.generalprompt;
		case DIALOG_ID_GOTO_INSTALL:
			return R.string.generalprompt;
		}
		return -1;
	}
	/**��ȡ�Ի��������
	 * @param dialogID �Ի���id
	 * @return �Ի����������������Ӧ����Դid
	 * */
	public static int getMessage(int dialogID)
	{
		switch(dialogID)
		{
		case DIALOG_ID_NO_DATA:
			return R.string.filenotexist;
		case DIALOG_ID_SDCARD_NOT_AVAILABLE:
			return R.string.sdnotready;
		case DIALOG_ID_CANCEL_DOWNLOAD:
			return R.string.canceldownload;
		case DIALOG_ID_NETWORK_NOT_AVALIABLE:
			return R.string.networknotavailable;
		case DIALOG_ID_NEW_DATA_AVAILABLE:
			return R.string.newDataAvailable;
		case DIALOG_ID_NET_CONNECT:
			return R.string.waiting;
		case DIALOG_ID_DOWNLOAD_FAILED:
		case DIALOG_ERROR_PROMPT:
			return R.string.losedownload;
		case DIALOG_ID_LOGINING_FAILD:
			return R.string.preting;
		case DIALOG_QUIT_PROMPT:
			return R.string.quitprompt;
		case DIALOG_ID_NO_UPDATE:
			return R.string.no_update;
		case DIALOG_ID_HAS_UPDATE:
			return R.string.need_update;
		case DIALOG_ID_NO_LISENCE:
			return R.string.nolicense;
		case DIALOG_ID_ERROR_LISENCE:
			return R.string.errorlicense;
		case DIALOG_ID_CHANGE_DPT:
			return R.string.update_dpt;
		case DIALOG_REGIST_NOTIFY:
			return R.string.regist_notify;
		case DIALOG_ID_DATA_ERROR:
			return R.string.data_error;
		case DIALOG_ID_DATA_FULL:
			return R.string.data_update_full;
		case DIALOG_ID_CANCEL_INSTALL:
			return R.string.cancel_install;
		case DIALOG_ID_UPDATE_NEW_VERSION:
			return R.string.install_app;
		case DIALOG_ID_NEED_UPDATE:
			return R.string.apk_need_install;
		case DIALOG_ID_MUST_UPDATE:
			return R.string.app_need_install;
		case DIALOG_GOTO_EVALUATE:
			return R.string.give_me_a_evaluate_content;
		case DIALOG_INVITE_NOTIFY:
			return R.string.invite_notify;
		case DIALOG_ID_SURVEY_SUBMIT_ERROR:
			return R.string.survey_submit_error;
		case DIALOG_ID_SURVEY_SUBMIT_FULL:
			return R.string.survey_submit_full;
		case DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED:
			return R.string.survey_submit_submitted;
		case DIALOG_ID_SHARE_AFTER_SUBMIT:
			return R.string.survey_submit_success;
		case DIALOG_ID_SURVEY_NOT_COMPLETE:
			return R.string.survey_not_complete;
		case DIALOG_ID_PACKAGE_UNINSTALLING:
			return R.string.market_package_uninstalling;
		case DIALOG_ID_NO_INVITATION:
			return R.string.invite_notify;
		case DIALOG_ID_UPDATE_INVITECODE:
			return R.string.invite_notify;
		case DIALOG_ID_SURVEY_DATA_LACK:
			return R.string.survey_data_lack;
		case DIALOG_ID_INVITECODE_BLANK:
			return R.string.update_version_dlg;
		case DIALOG_ID_INVITECODE_FAIL:
			return R.string.update_version_fail;
		case DIALOG_ID_GOTO_INSTALL:
			return R.string.tools_goto_install;
		}
		return -1;
	}
	/**��ȡ�Ի�����������
	 * @param dialogID �Ի���id
	 * @return �����������Ӧ����Դid
	 * */
	public static int getPositiveTxt(int dialogID)
	{
		switch(dialogID)
		{
		case DIALOG_ID_CANCEL_DOWNLOAD:
			return R.string.stopdownload;
		case DIALOG_ID_ERR:
		case DIALOG_ID_NETWORK_NOT_AVALIABLE:
		case DIALOG_ID_DOWNLOAD_FAILED:
		case DIALOG_ERROR_PROMPT:
		case DIALOG_ID_NO_DATA:
		case DIALOG_ID_HAS_UPDATE:
		case DIALOG_ID_NO_UPDATE:
		case DIALOG_ID_CHANGE_DPT:
		
		case DIALOG_ID_NEW_DATA_AVAILABLE:
		case DIALOG_ID_DATA_ERROR:
		case DIALOG_ID_DATA_FULL:
		case DIALOG_ID_CANCEL_INSTALL:
		case DIALOG_ID_UPDATE_NEW_VERSION:
		case DIALOG_ID_NEED_UPDATE:
		case DIALOG_ID_MUST_UPDATE:
		case DIALOG_ID_SURVEY_SUBMIT_ERROR:
		case DIALOG_ID_SURVEY_SUBMIT_FULL:
		case DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED:
		case DIALOG_ID_SURVEY_NOT_COMPLETE:
		case DIALOG_ID_UPDATE_INVITECODE:
		case DIALOG_ID_INVITECODE_BLANK:
		case DIALOG_ID_INVITECODE_FAIL:
			return R.string.ok;
		case DIALOG_QUIT_PROMPT:
			return R.string.exit;
		case DIALOG_ID_LOGINING_FAILD:
		case DIALOG_ID_SDCARD_NOT_AVAILABLE:
			return R.string.ok;
		case DIALOG_ID_NO_LISENCE:
		case DIALOG_ID_ERROR_LISENCE:
			return R.string.modify;
		case DIALOG_REGIST_NOTIFY:
		case DIALOG_INVITE_NOTIFY:
		case DIALOG_ID_NO_INVITATION:
			return R.string.yes;
		case DIALOG_GOTO_EVALUATE:
			return R.string.gotonow;
		case DIALOG_ID_SHARE_AFTER_SUBMIT:
			return R.string.shrout;
		case DIALOG_ID_SURVEY_DATA_LACK:
			return R.string.survey_data_lack_ok;
		case DIALOG_ID_GOTO_INSTALL:
			return R.string.ok;
		}
		return -1;
	}
	/**��ȡ�Ի�����Ҽ�����
	 * @param dialogID �Ի���id
	 * @return �Ҽ���������Ӧ����Դid
	 * */
	public static int getNegativeTxt(int dialogID)
	{
		switch(dialogID)
		{
		case DIALOG_QUIT_PROMPT:
		case DIALOG_ID_DOWNLOAD_FAILED:
		case DIALOG_ID_CANCEL_DOWNLOAD:
		case DIALOG_ID_HAS_UPDATE:
		case DIALOG_ID_NO_LISENCE:
		case DIALOG_ID_ERROR_LISENCE:
		case DIALOG_ID_CHANGE_DPT:
		case DIALOG_ID_NEW_DATA_AVAILABLE:
		case DIALOG_ID_DATA_ERROR:
		case DIALOG_ID_CANCEL_INSTALL:
		case DIALOG_ID_UPDATE_NEW_VERSION:
		case DIALOG_ID_NEED_UPDATE:
		case DIALOG_ID_SHARE_AFTER_SUBMIT:
		case DIALOG_ID_MUST_UPDATE:
		case DIALOG_ID_NO_INVITATION:
			return R.string.cancel;
		case DIALOG_REGIST_NOTIFY:
		case DIALOG_INVITE_NOTIFY:
			return R.string.late;
		case DIALOG_ID_SURVEY_DATA_LACK:
			return R.string.survey_data_lack_cancel;
		}
		return -1;
	}
	/**��ȡ�Ի�����м������
	 * @param dialogID �Ի���id
	 * @return �м����������Ӧ����Դid
	 * */
	public static int getNeutralTxt(int dialogID)
	{
		switch(dialogID)
		{
		case DIALOG_QUIT_PROMPT:
			return R.string.switchacc;
		}
		
		return -1;
	}
	
	
	/**����id����һ���Ի���
	 * @param c �Ի��������
	 * @param id �Ի���id
	 * @param canelable �Ƿ���԰����ؼ��˳�
	 * @return ���ɵĶԻ���
	 * */
	public static AlertDialog.Builder getBuild(Context c,int id,boolean canelable)
	{
		
		if(id == DIALOG_WAITING_FOR_DATA){
			return buildProgressDialogChangeTitle(c,R.string.waiting);
	    }
		else if(id==DIALOG_UPDATE_FOR_DATA || id==DIALOG_ID_NET_CONNECT){
			return buildProgressDialogChangeTitle(c,R.string.updating);
		}else if(id==DIALOG_SEND_REGISTER_REQUEST){
			return buildProgressDialogChangeMessage(c,R.string.sending);
		}else if(id==DIALOG_ID_AUTO_LOGINING){
			return new AlertDialog.Builder(c)
			.setMessage(R.string.autologining).setIcon(R.drawable.icon);
		}else if(id == DIALOG_ID_JUMP_TO){
			return buildProgressDialogChangeMessage(c,R.string.jump_to);
		}else if(id==DIALOG_ID_LOGINING){
			return buildProgressDialogChangeMessage(c,R.string.logining);
		}
		else if(id==DIALOG_ID_CHECK_USERNAME_ISVALID){
			return buildProgressDialogChangeMessage(c,R.string.checkUsername);
		}else if(id==DIALOG_ID_GOTO_INSTALL){
			return new AlertDialog.Builder(c).setTitle(getTitle(id))
			.setMessage(getMessage(id)).setIcon(R.drawable.icon).setPositiveButton(getPositiveTxt(id), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}else if(id==DIALOG_INVITATION_REQUEST){
			return new AlertDialog.Builder(c)
			.setMessage(R.string.invite_success).setIcon(R.drawable.icon).setNeutralButton(R.string.sure,  new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					dialog.cancel();
				}
			});
		}
		else{
		 AlertDialog.Builder builder = new AlertDialog.Builder(c).setCancelable(canelable)
				.setTitle(getTitle(id)).setIcon(R.drawable.icon);
		 if(id != DIALOG_ID_ERR)
			 builder.setMessage(getMessage(id));
		 return builder;
		}
	}
	
	private static AlertDialog.Builder buildProgressDialogChangeTitle(Context c,int titleId){
		 LayoutInflater li = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dv = (View) li.inflate(R.layout.dialogprogress, null);
			return new AlertDialog.Builder(c).setTitle(titleId)
					.setIcon(R.drawable.icon).setView(dv)
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
						}
					});
	}
	
	
	private static AlertDialog.Builder buildProgressDialogChangeMessage(Context c,int messageId){
		LayoutInflater li = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dv = (View) li.inflate(R.layout.dialogprogress, null);
		TextView tv = (TextView) dv.findViewById(R.id.tv);
		tv.setText(messageId);
		return new AlertDialog.Builder(c)
				.setTitle(R.string.generalprompt)
				.setIcon(R.drawable.icon).setView(dv);
	}
	
}
