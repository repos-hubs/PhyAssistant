package com.jibo.base.src.request;

import java.util.List;

public abstract class RecursiveListener extends CallBackListener {
	public RequestSrc currRequestSrc;
	public RequestSrc nextRequestSrc;
	public List<String> nextLayerValue;

	public RecursiveListener(RequestSrc requestSrc) {
		super();
		this.currRequestSrc = requestSrc;
	}

	@Override
	public boolean callback() {
		// TODO Auto-generated method stub
		return recursive(currRequestSrc, nextRequestSrc);
	}

	public abstract boolean recursive(RequestSrc currRequestSrc,
			RequestSrc nextRequestSrc);

	public abstract void stop(RequestSrc currRequestSrc,
			RequestSrc nextRequestSrc);
}
