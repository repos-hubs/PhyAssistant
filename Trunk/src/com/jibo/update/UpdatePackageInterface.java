package com.jibo.update;


public abstract class UpdatePackageInterface {
	public boolean bSuccess = true;
	public abstract boolean update(String path);
}
