package com.jibo.base.src.request;

public class ScrollCounter {
	public int count;
	public int runtimePiece;
	public int piece;

	public ScrollCounter(int count, int piece) {
		super();
		this.count = count;
		this.piece = this.runtimePiece = piece;
	}

}
