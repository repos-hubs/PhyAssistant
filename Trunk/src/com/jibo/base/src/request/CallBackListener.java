package com.jibo.base.src.request;


public abstract class CallBackListener {
	public abstract boolean callback();

	public Object oj;

	public void setResultOj(Object oj) {
		this.oj = oj;
	}

	public LogicListener logicListener;

	public LogicListener getLogicListener() {
		return logicListener;
	}

	public void setLogicListener(LogicListener logicListener) {
		this.logicListener = logicListener;
		logicListener.setOj(this.oj);
	}

}
