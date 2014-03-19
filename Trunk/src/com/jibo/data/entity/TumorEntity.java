package com.jibo.data.entity;

import java.util.ArrayList;

public class TumorEntity {
	private ArrayList<String> indexList;
	private ArrayList<String> signList;
	private ArrayList<String> significanceList;
	
	public ArrayList<String> getIndexList() {
		return indexList;
	}
	public void setIndexList(ArrayList<String> indexList) {
		this.indexList = indexList;
	}
	public ArrayList<String> getSignList() {
		return signList;
	}
	public void setSignList(ArrayList<String> signList) {
		this.signList = signList;
	}
	public ArrayList<String> getSignificanceList() {
		return significanceList;
	}
	public void setSignificanceList(ArrayList<String> significanceList) {
		this.significanceList = significanceList;
	}
}
