package com.jibo.util;

import com.jibo.common.Constant;

public abstract class DebugInst {
	
	public DebugInst() {
		debug();
	}
	protected void debug(){
		if(Constant.DEBUG){
			forDebug();
		}
	}
	public abstract void forDebug();
}
