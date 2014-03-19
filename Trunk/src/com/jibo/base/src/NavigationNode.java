package com.jibo.base.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationNode {

	private String currLyLabel;
	private List<String> runtimeCurrVisitValue = new ArrayList<String>(0);
	private List<String> currVisitValue = new ArrayList<String>(0);

	public List<String> getRuntimeCurrVisitValue() {
		return runtimeCurrVisitValue;
	}

	public void setRuntimeCurrVisitValue(List<String> runtimeCurrVisitValue) {
		this.runtimeCurrVisitValue = runtimeCurrVisitValue;
	}

	public String getCurrLyLabel() {
		return currLyLabel;
	}

	public void setCurrLyLabel(String currLyLabel) {
		this.currLyLabel = currLyLabel;
	}

	public List<String> getCurrVisitValue() {
		return currVisitValue;
	}

	public void setCurrVisitValue(List<String> currVisitValue) {
		this.currVisitValue = new ArrayList<String>(currVisitValue);
	}

	public void setCurrVisitValue(String[] currVisitValue) {
		this.currVisitValue = new ArrayList<String>(Arrays.asList(currVisitValue));
	}

	public List<String> nextLyVal;
	public String naviLinkLabel;
	public List<String> argNames;

	public NavigationNode(String[] nextLyVal, String naviBarLabel) {
		super();
		this.setArgV(nextLyVal);
		this.naviLinkLabel = naviBarLabel;
	}

	public NavigationNode(List<String> nextLyNames, String naviBarLabel) {
		super();
		this.argNames = nextLyNames;
		this.naviLinkLabel = naviBarLabel;
	}

	public NavigationNode() {
		// TODO Auto-generated constructor stub
	}

	public String getLyLabel() {
		return naviLinkLabel;
	}

	public List<String> getArgV() {
		return nextLyVal;
	}

	public void setArgV(String[] args) {
		this.nextLyVal = Arrays.asList(args);
	}

	public void setResultToNextLyMapNames(String[] names) {
		this.argNames = Arrays.asList(names);
	}

	public List<String> getArgPassedNames() {
		return argNames;
	}

}
