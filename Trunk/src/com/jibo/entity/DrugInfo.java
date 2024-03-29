package com.jibo.entity;

import java.util.List;

import android.util.Log;

import com.jibo.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.jibo.dao.AhfsDrugInfoDao;
import com.jibo.dao.BrandInfoDao;
import com.jibo.dao.DrugInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DrugBasicInfo.
 * 
 * 药品主表，目前药品直接从DrugList临时表中获取，原DrugBasicInfo暂无用处。包括药品信息+给药途径
 * 具体获取药品列表逻辑在DrugInfoDao
 */
public class DrugInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String nameCn;
	private String nameEn;
	private String insertInfo;
	private String ahfsInfo;
	private String otc;
	private String tcm;
	private Float salesRank;
	private String changeDate;
	// 给药途径的个数
	private int count;

	/** Used to resolve relations */
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	private transient DrugInfoDao myDao;

	/***
	 * 商品数据集
	 */
	private List<BrandInfo> brandInfoList;
	/***
	 * AHFS数据集
	 */
	private List<AhfsDrugInfo> ahfsDrugInfoList;
	/***
	 * 给药类型
	 */
	private AdminRouteInfo adminRouteInfo;

	/**
	 * 类型(第四级类型)
	 */
	private TADrugRef taDrugRef;

	public DrugInfo() {
	}

	public DrugInfo(String id) {
		this.id = id;
	}

	public DrugInfo(String id, String nameEn, String nameCn, String insertInfo,
			String ahfsInfo, String otc, String tcm, Float salesRank,
			String changeDate) {
		this.id = id;
		this.nameEn = nameEn;
		this.nameCn = nameCn;
		this.insertInfo = insertInfo;
		this.ahfsInfo = ahfsInfo;
		this.otc = otc;
		this.tcm = tcm;
		this.salesRank = salesRank;
		this.changeDate = changeDate;
	}

	/** called by internal mechanisms, do not call yourself. */
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getDrugInfoDao() : null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getOtc() {
		return otc;
	}

	public void setOtc(String otc) {
		this.otc = otc;
	}

	public String getTcm() {
		return tcm;
	}

	public void setTcm(String tcm) {
		this.tcm = tcm;
	}

	public Float getSalesRank() {
		return salesRank;
	}

	public void setSalesRank(Float salesRank) {
		this.salesRank = salesRank;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getInsertInfo() {
		return insertInfo;
	}

	public void setInsertInfo(String insertInfo) {
		this.insertInfo = insertInfo;
	}

	public String getAhfsInfo() {
		return ahfsInfo;
	}

	public void setAhfsInfo(String ahfsInfo) {
		this.ahfsInfo = ahfsInfo;
	}

	public AdminRouteInfo getAdminRouteInfo() {
		return adminRouteInfo;
	}

	public void setAdminRouteInfo(AdminRouteInfo adminRouteInfo) {
		this.adminRouteInfo = adminRouteInfo;
	}

	public TADrugRef getTaDrugRef() {
		return taDrugRef;
	}

	public void setTaDrugRef(TADrugRef taDrugRef) {
		this.taDrugRef = taDrugRef;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes
	 * to to-many relations are not persisted, make changes to the target
	 * entity.
	 */
	public synchronized List<BrandInfo> getBrandInfoList() {
		if (brandInfoList == null) {
			if (daoSession == null) {
				Log.i("simon", "Entity is detached from DAO context");
				return  null;
			}
			BrandInfoDao targetDao = daoSession.getBrandInfoDao();
			if (adminRouteInfo != null)
				brandInfoList = targetDao._queryDrugInfo_BrandInfoList(id,
						adminRouteInfo.getId());
			else
				brandInfoList = targetDao._queryDrugInfo_BrandInfoList(id);
		}
		return brandInfoList;
	}

	/**
	 * Resets a to-many relationship, making the next get call to query for a
	 * fresh result.
	 */
	public synchronized void resetBrandInfoList() {
		brandInfoList = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes
	 * to to-many relations are not persisted, make changes to the target
	 * entity.
	 */
	public synchronized List<AhfsDrugInfo> getAhfsDrugInfoList() {
		if (ahfsDrugInfoList == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			AhfsDrugInfoDao targetDao = daoSession.getAhfsDrugInfoDao();
			ahfsDrugInfoList = targetDao._queryDrugInfo_AhfsDrugInfoList(id);
		}
		return ahfsDrugInfoList;
	}

	/**
	 * Resets a to-many relationship, making the next get call to query for a
	 * fresh result.
	 */
	public synchronized void resetAhfsDrugInfoList() {
		ahfsDrugInfoList = null;
	}

	/**
	 * Convenient call for {@link AbstractDao#delete(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void delete() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.delete(this);
	}

	/**
	 * Convenient call for {@link AbstractDao#update(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void update() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.update(this);
	}

	/**
	 * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void refresh() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.refresh(this);
	}

}
