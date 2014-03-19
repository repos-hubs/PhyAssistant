package com.jibo.base.src.request;

public abstract class LogicListener {
	public Object oj;
	public String elseVIsitLink;
	
	public LogicListener(String elseVIsitLink) {
		super();
		this.elseVIsitLink = elseVIsitLink;
	}

	public Object getOj() {
		return oj;
	}

	public void setOj(Object oj) {
		this.oj = oj;
	}

	public abstract boolean logic(Object result);
}
