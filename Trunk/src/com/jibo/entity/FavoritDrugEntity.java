package com.jibo.entity;

import java.util.List;

import android.util.Log;

import com.jibo.dao.BrandInfoDao;
import com.jibo.dao.DaoSession;

/**
 * 收藏药品实体
 * 
 */
public class FavoritDrugEntity {
	public String drugId;
	public String drugName;
	public String adminRouteId;
	public String adminRouteName;
	public int mode;
	/** 是否包含AHFS,包含Y，不包含N */
	public String ahfsInfo;

	// 商品名合集
	private List<BrandInfo> brandInfoList;

	public List<BrandInfo> getBrandInfo(DaoSession daoSession) {
		if (brandInfoList == null) {
			if (daoSession == null) {
				Log.i("simon", "Entity is detached from DAO context");
				return null;
			}
			BrandInfoDao targetDao = daoSession.getBrandInfoDao();
			brandInfoList = targetDao._queryBrandInfoList(drugId, adminRouteId);
		}
		return brandInfoList;
	}
}
