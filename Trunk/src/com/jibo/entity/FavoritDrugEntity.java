package com.jibo.entity;

import java.util.List;

import android.util.Log;

import com.jibo.dao.BrandInfoDao;
import com.jibo.dao.DaoSession;

/**
 * �ղ�ҩƷʵ��
 * 
 */
public class FavoritDrugEntity {
	public String drugId;
	public String drugName;
	public String adminRouteId;
	public String adminRouteName;
	public int mode;
	/** �Ƿ����AHFS,����Y��������N */
	public String ahfsInfo;

	// ��Ʒ���ϼ�
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
