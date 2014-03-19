package com.jibo.asynctask;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jibo.activity.BaseActivity;
import com.jibo.app.research.CollectionActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.Util;
import com.jibo.data.entity.DownloadInfo;
import com.jibo.data.entity.DownloadProgressInfo;
import com.jibo.data.entity.PaperDownloadEntity;
import com.jibo.dbhelper.DownloadResumeDao;
import com.jibo.dbhelper.PaperDownloadAdapter;

/**
 * 异步下载类
 * @author will
 */
public class DownloaderResume {
	private String urlstr;// 下载的地址
	private String specialID;
	private String title;
	private String localfile;// 保存路径
	private int threadcount;// 线程数
	private Handler mHandler;// 消息处理器
	private DownloadResumeDao dao;// 工具类
	private PaperDownloadAdapter downloadAdapter;
	private long fileSize;// 所要下载的文件的大小
	private List<DownloadInfo> infos;// 存放下载信息类的集合
	public static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int DOWNLOADING = 2;
	public static final int PAUSE = 3;
	private int state = INIT;
	private String dataPath;
	private Context context;
	private FileSizeCallBack fileCB;
	private boolean isFullText = false; //是否是全文下载

	public DownloaderResume(String urlstr, String specialID, String localfile, int threadcount,
			Context context, Handler mHandler, String title, FileSizeCallBack fileCB) {
		this.urlstr = urlstr;
		this.specialID = specialID;
		this.title = title;
		this.localfile = localfile;
		this.threadcount = threadcount;
		this.mHandler = mHandler;
		this.context = context;
		this.fileCB = fileCB;
		dao = new DownloadResumeDao(context);
		dataPath = Constant.PAPER_DOWNLOAD + "/" 
				+ SharedPreferencesMgr.getUserName();
		File f = new File(dataPath);
		if(!f.exists()){
			f.mkdirs();
		}
	}
	
	/**
	 * 全文下载构造器
	 */
	public DownloaderResume(String urlstr, String specialID, String localfile, int threadcount,
			Context context) {
		this.urlstr = urlstr;
		this.specialID = specialID;
		this.localfile = localfile;
		this.threadcount = threadcount;
		this.context = context;
		this.isFullText = true;
		dao = new DownloadResumeDao(context);
		downloadAdapter = new PaperDownloadAdapter(context);
		dataPath = Constant.PAPER_DOWNLOAD + "/" 
				+ SharedPreferencesMgr.getUserName();
		File f = new File(dataPath);
		if(!f.exists()){
			f.mkdirs();
		}
	}
	
	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	/**
	 * 得到downloader里的信息 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载器
	 */
	public DownloadProgressInfo getDownloaderInfors() {
		if (isFirst(urlstr)) {
			init();
			long range = (long) (fileSize / threadcount);
			infos = new ArrayList<DownloadInfo>();
			for (int i = 0; i < threadcount - 1; i++) {
				DownloadInfo info = new DownloadInfo(i, i * range, (i + 1)
						* range - 1, 0, urlstr, fileSize, title, specialID);
				infos.add(info);
			}
			DownloadInfo info = new DownloadInfo(threadcount - 1,
					(threadcount - 1) * range, fileSize - 1, 0, urlstr, fileSize, title, specialID);
//			info.setSpecialID(specialID);
			infos.add(info);
			// 保存infos中的数据到数据库
			dao.saveInfos(infos);
			// 创建一个LoadInfo对象记载下载器的具体信息
			DownloadProgressInfo loadInfo = new DownloadProgressInfo(fileSize, 0, urlstr);
			return loadInfo;
		} else {
			// 得到数据库中已有的urlstr的下载器的具体信息
			infos = dao.getInfos(urlstr);
			Log.v("TAG", "not isFirst size=" + infos.size());
			long size = 0l;
			long compeleteSize = 0l;
			for (DownloadInfo info : infos) {
				compeleteSize += info.getCompeleteSize();
				size += info.getEndPos() - info.getStartPos() + 1;
			}
			return new DownloadProgressInfo(size, compeleteSize, urlstr);
		}
	}

	/**
      */
	private void init() {
		try {
			URL url = new URL(urlstr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
//			connection.setConnectTimeout(5000);
//			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
			if(fileCB != null){
				fileCB.onGetFileSize(fileSize);
			}
			File file = new File(localfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			// 本地访问文件
			RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
			accessFile.setLength(fileSize);
			accessFile.close();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否是第一次 下载
	 */
	private boolean isFirst(String urlstr) {
		return dao.isHasInfors(urlstr);
	}

	/**
	 * 114 * 利用线程开始下载数据 115
	 */
	public void download() {
		if (infos != null) {
			if (state == DOWNLOADING)
				return;
			state = DOWNLOADING;
			for (DownloadInfo info : infos) {
				if(isFullText){
					new FullTextThread(info.getThreadId(), info.getStartPos(),
							info.getEndPos(), info.getCompeleteSize(),
							info.getUrl(), new CallBack() {
						@Override
						public void onFinish(boolean b) {
							if(b){
								dao.updateDownloadState(specialID);
								downloadAdapter.updateState(context, specialID, PaperDownloadEntity.READ, SharedPreferencesMgr.getUserName());
								Util.downloaders.remove(urlstr);
								Message message = new Message();
								message.what = 2;
								message.obj = urlstr;
								Bundle data = new Bundle();
								data.putString("specialID", specialID);
								data.putString("isDone", "yes");
								message.setData(data);
								mHandler.sendMessage(message);
							}
						}
					}).start();
				}else
				new MyThread(info.getThreadId(), info.getStartPos(),
						info.getEndPos(), info.getCompeleteSize(),
						info.getUrl(), new CallBack() {
					@Override
					public void onFinish(boolean b) {
						if(b){
							dao.updateDownloadState(specialID);
							Util.downloaders.remove(urlstr);
							Message message = Message.obtain();
							if(isFullText){
								message.what = 2;
							}else{
								message.what = 1;
							}
							message.obj = urlstr;
							Bundle data = new Bundle();
							data.putString("specialID", specialID);
							data.putString("isDone", "yes");
							message.setData(data);
							mHandler.sendMessage(message);
						}
					}
				}).execute();
			}
		}
	}
	
	public class FullTextThread extends Thread{
		private int threadId;
		private long startPos;
		private long endPos;
		private long compeleteSize;
		private String urlstr;
		private CallBack callBack;
		
		public FullTextThread(int threadId, long startPos, long endPos,
				long compeleteSize, String urlstr, CallBack callBack) {
			this.threadId = threadId;
			this.startPos = startPos;
			this.endPos = endPos;
			this.compeleteSize = compeleteSize;
			this.urlstr = urlstr;
			this.callBack = callBack;
		}

		@Override
		public void run() {
			super.run();
			try{
				runImpl();
				if(state != PAUSE){
					unzip(urlstr.substring(urlstr.lastIndexOf("=") + 1));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void runImpl() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-" + endPos);

				randomAccessFile = new RandomAccessFile(localfile, "rw");
				randomAccessFile.seek(startPos + compeleteSize);
				is = connection.getInputStream();
				byte[] buffer = new byte[102400];
				int length = -1;
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					showProgress();
					// 更新数据库中的下载信息
					dao.updataInfos(threadId, compeleteSize, urlstr);
					if (state == PAUSE) {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(is != null)
					is.close();
					if(randomAccessFile != null)
					randomAccessFile.close();
					if(connection != null)
					connection.disconnect();
					if(dao != null)
					dao.closeDb();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private void showProgress(){
			if(context instanceof PaperDetailActivity || context instanceof CollectionActivity){
				if(null != CollectionActivity.mContext){
					if(fileSize == 0){
						try {
							URL url = new URL(urlstr);
							HttpURLConnection connection = (HttpURLConnection) url
									.openConnection();
							fileSize = connection.getContentLength();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					((BaseActivity) CollectionActivity.mContext).setDownloadProgress((int)((compeleteSize*100)/fileSize),urlstr, specialID);
				}
			}
		}
		
		private void unzip(String outputFileName) {
			File outputFile = new File(dataPath + "/" + outputFileName + ".zip");
			if(!outputFile.exists()) return;
			try {
				ZipFile zf = new ZipFile(outputFile.getAbsolutePath());
//				zf.extractAll(dataPath);
				File file = new File(dataPath
						+ "/"
						+ specialID);
				if (!file.exists())
					file.mkdirs();
				zf.extractAll(file.getAbsolutePath());
				callBack.onFinish(true);
			} catch (ZipException e) {
				e.printStackTrace();
			} finally {
				outputFile.delete();
			}
		}
		
	}

	public class MyThread extends AsyncTask<Void, Long, Boolean> {
		private int threadId;
		private long startPos;
		private long endPos;
		private long compeleteSize;
		private String urlstr;
		private CallBack callBack;
		
		public MyThread(int threadId, long startPos, long endPos,
				long compeleteSize, String urlstr, CallBack callBack) {
			this.threadId = threadId;
			this.startPos = startPos;
			this.endPos = endPos;
			this.compeleteSize = compeleteSize;
			this.urlstr = urlstr;
			this.callBack = callBack;
		}

		private void run() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
//				connection.setConnectTimeout(5000);
//				connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-" + endPos);

				randomAccessFile = new RandomAccessFile(localfile, "rw");
				randomAccessFile.seek(startPos + compeleteSize);
				// 将要下载的文件写到保存在保存路径下的文件中
				is = connection.getInputStream();
				byte[] buffer = new byte[102400];
				int length = -1;
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					publishProgress((long)compeleteSize);
					// 更新数据库中的下载信息
					dao.updataInfos(threadId, compeleteSize, urlstr);
					if (state == PAUSE) {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(is != null)
					is.close();
					if(randomAccessFile != null)
					randomAccessFile.close();
					if(connection != null)
					connection.disconnect();
					if(dao != null)
					dao.closeDb();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				run();
				if(state != PAUSE){
					unzip(urlstr.substring(urlstr.lastIndexOf("/") + 1));
				}
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			if (context != null) {
				if(context instanceof CollectionActivity){
					if(null != CollectionActivity.mContext){
						((BaseActivity) CollectionActivity.mContext).setDownloadProgress(compeleteSize,urlstr, specialID);
					}
				}else if(context instanceof PaperDetailActivity){
					if(null != CollectionActivity.mContext){
						if(fileSize == 0){
							try {
								URL url = new URL(urlstr);
								HttpURLConnection connection = (HttpURLConnection) url
										.openConnection();
								fileSize = connection.getContentLength();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						((BaseActivity) CollectionActivity.mContext).setDownloadProgress((int)((compeleteSize*100)/fileSize),urlstr, specialID);
					}
				}else{
					((BaseActivity) context).setDownloadProgress(compeleteSize,urlstr, specialID);
				}
			}
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
		
		private void unzip(String outputFileName) {
			File outputFile = new File(dataPath + "/" + outputFileName);
			if(!outputFile.exists()) return;
			try {
				ZipFile zf = new ZipFile(outputFile.getAbsolutePath());
				zf.extractAll(dataPath);
				callBack.onFinish(true);
			} catch (ZipException e) {
				e.printStackTrace();
			} finally {
				outputFile.delete();
			}

		}
	}
	
	// 删除数据库中urlstr对应的下载器信息
	public void delete(String ID) {
		dao.delete(ID);
	}

	// 设置暂停
	public void pause() {
		state = PAUSE;
	}

	// 重置下载状态
	public void reset() {
		state = INIT;
	}
	
	public int getDownloadState(){
		return state;
	}
	
	private interface CallBack {
		void onFinish(boolean b);
	}
	
	public interface FileSizeCallBack{
		void onGetFileSize(long fileSize);
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
}
