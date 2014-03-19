package com.jibo.app.research;

import java.util.Properties;

import android.content.Context;

import com.jibo.activity.BaseActivity;
import com.jibo.asynctask.DownloaderResume;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.DownloadFullTextPaser;
import com.jibo.data.entity.PaperDownloadEntity;
import com.jibo.dbhelper.PaperDownloadAdapter;
import com.jibo.net.BaseResponseHandler;

public class RequestDownloadURLThread extends Thread{
	private PaperDownloadEntity entity;
//	private boolean isBreak = false;
	private String paperID;
	private String urlstr;
	private String resCode;
	
	private Context context;
	private PaperDownloadAdapter downloadAdpt;
	
	public RequestDownloadURLThread(PaperDownloadEntity entity, Context context){
		super();
		this.entity = entity;
		this.paperID = entity.getPaperID();
		this.context = context;
		downloadAdpt = new PaperDownloadAdapter(context);
	}

	@Override
	public void run() {
		super.run();
//		while(!isBreak){
			
			Properties propertyInfo = new Properties();
			propertyInfo.put("sign", "");
			propertyInfo.put(SoapRes.KEY_FULLTEXT_USER_NAME ,
					SharedPreferencesMgr.getUserName());
			propertyInfo.put(SoapRes.KEY_FULLTEXT_PAPER_ID, paperID);
			propertyInfo.put(SoapRes.KEY_FULLTEXT_CULTURE , "en-US");
			((BaseActivity) context).sendRequest(SoapRes.URLResearchDetail,
					SoapRes.REQ_ID_DOWNLOAD_FULLTEXT, propertyInfo,
					new BaseResponseHandler((BaseActivity)context, false){

				@Override
				public void onSuccess(Object content, int methodId) {
					super.onSuccess(content, methodId);
					DownloadFullTextPaser data = (DownloadFullTextPaser) content;
					if(data != null){
						resCode = data.resCode;
						if(resCode.trim().equals(DownloadFullTextPaser.ERROR_CODE_NORES)){
							downloadAdpt.updateState(context, paperID, PaperDownloadEntity.FAILURE, entity.getUsername());
						}else if(resCode.trim().equals(DownloadFullTextPaser.SUCCESS_CODE)){
							urlstr = data.url;
							startDownload(urlstr, paperID, entity);
						}
					}else{
						downloadAdpt.updateState(context, paperID, PaperDownloadEntity.FAILURE, entity.getUsername());
					}
				}

			});
//			try {
//				Thread.sleep(1000 * 60);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
//	public void setBreak(){
//		isBreak = true;
//	}
	private void startDownload(String url, final String specialID, PaperDownloadEntity entity) {
		String urlstr = url + ".zip";
		String fileName = urlstr.substring(urlstr.lastIndexOf("=") + 1);
		String localfile = CollectionActivity.SD_PATH + fileName;
		downloadAdpt.updateFilename(context, specialID, url, fileName, SharedPreferencesMgr.getUserName());
		// 设置下载线程数为10
		int threadcount = 1 ;
		// 初始化一个downloader下载器
		DownloaderResume downloader = Util.downloaders.get(url);
		if (downloader == null) {
			downloader = new DownloaderResume(url, specialID, localfile, threadcount, context);
			Util.downloaders.put(url, downloader);
		}
		if (downloader.isdownloading())
			return;
		downloadAdpt.updateState(context, specialID, PaperDownloadEntity.DOWNLOADING, entity.getUsername());
		// 得到下载信息类的个数组成集合
		downloader.getDownloaderInfors();
		// 调用方法开始下载
		downloader.download();
//		downloadAdpt.insertInfo(this, entity);
	}
	
}

