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
 * �첽������
 * @author will
 */
public class DownloaderResume {
	private String urlstr;// ���صĵ�ַ
	private String specialID;
	private String title;
	private String localfile;// ����·��
	private int threadcount;// �߳���
	private Handler mHandler;// ��Ϣ������
	private DownloadResumeDao dao;// ������
	private PaperDownloadAdapter downloadAdapter;
	private long fileSize;// ��Ҫ���ص��ļ��Ĵ�С
	private List<DownloadInfo> infos;// ���������Ϣ��ļ���
	public static final int INIT = 1;// �����������ص�״̬����ʼ��״̬����������״̬����ͣ״̬
	public static final int DOWNLOADING = 2;
	public static final int PAUSE = 3;
	private int state = INIT;
	private String dataPath;
	private Context context;
	private FileSizeCallBack fileCB;
	private boolean isFullText = false; //�Ƿ���ȫ������

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
	 * ȫ�����ع�����
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
	 * �ж��Ƿ���������
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	/**
	 * �õ�downloader�����Ϣ ���Ƚ����ж��Ƿ��ǵ�һ�����أ�����ǵ�һ�ξ�Ҫ���г�ʼ������������������Ϣ���浽���ݿ���
	 * ������ǵ�һ�����أ��Ǿ�Ҫ�����ݿ��ж���֮ǰ���ص���Ϣ����ʼλ�ã�����Ϊֹ���ļ���С�ȣ�������������Ϣ���ظ�������
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
			// ����infos�е����ݵ����ݿ�
			dao.saveInfos(infos);
			// ����һ��LoadInfo��������������ľ�����Ϣ
			DownloadProgressInfo loadInfo = new DownloadProgressInfo(fileSize, 0, urlstr);
			return loadInfo;
		} else {
			// �õ����ݿ������е�urlstr���������ľ�����Ϣ
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
			// ���ط����ļ�
			RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
			accessFile.setLength(fileSize);
			accessFile.close();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ж��Ƿ��ǵ�һ�� ����
	 */
	private boolean isFirst(String urlstr) {
		return dao.isHasInfors(urlstr);
	}

	/**
	 * 114 * �����߳̿�ʼ�������� 115
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
					// �������ݿ��е�������Ϣ
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
				// ���÷�Χ����ʽΪRange��bytes x-y;
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-" + endPos);

				randomAccessFile = new RandomAccessFile(localfile, "rw");
				randomAccessFile.seek(startPos + compeleteSize);
				// ��Ҫ���ص��ļ�д�������ڱ���·���µ��ļ���
				is = connection.getInputStream();
				byte[] buffer = new byte[102400];
				int length = -1;
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					publishProgress((long)compeleteSize);
					// �������ݿ��е�������Ϣ
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
	
	// ɾ�����ݿ���urlstr��Ӧ����������Ϣ
	public void delete(String ID) {
		dao.delete(ID);
	}

	// ������ͣ
	public void pause() {
		state = PAUSE;
	}

	// ��������״̬
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
