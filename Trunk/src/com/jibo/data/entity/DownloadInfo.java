package com.jibo.data.entity;

public class DownloadInfo {

	private int threadId;// ������id
	private long startPos;// ��ʼ��
	private long endPos;// ������
	private long compeleteSize;// ��ɶ�
	private String url;// �����������ʶ
	private String specialID;// �������ݰ�id
	private boolean downloadState; //�Ƿ�������
	private String title;
	private long fileSize;
	
	public DownloadInfo(){
		super();
	}
	
	public DownloadInfo(int threadId, long startPos, long endPos,
			long compeleteSize, String url, long fileSize, String title, String specialID) {
		super();
		this.threadId = threadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.compeleteSize = compeleteSize;
		this.url = url;
		this.fileSize = fileSize;
		this.title = title;
		this.specialID = specialID;
	}
	
	public DownloadInfo(long fileSize, long compeleteSize, boolean downloadState){
		super();
		this.fileSize = fileSize;
		this.compeleteSize = compeleteSize;
		this.downloadState = downloadState;
	}
	
	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public long getStartPos() {
		return startPos;
	}
	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}
	public long getEndPos() {
		return endPos;
	}
	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}
	public long getCompeleteSize() {
		return compeleteSize;
	}
	public void setCompeleteSize(long compeleteSize) {
		this.compeleteSize = compeleteSize;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isDownloadState() {
		return downloadState;
	}
	public void setDownloadState(boolean downloadState) {
		this.downloadState = downloadState;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSpecialID() {
		return specialID;
	}
	public void setSpecialID(String specialID) {
		this.specialID = specialID;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "DownloadInfo [threadId=" + threadId
				+ ", startPos=" + startPos + ", endPos=" + endPos
				+ ", paperid=" + specialID
				+ ", downloadState=" + downloadState
				+ ", title=" + title
				+ ", compeleteSize=" + compeleteSize +"]";
	}


}
