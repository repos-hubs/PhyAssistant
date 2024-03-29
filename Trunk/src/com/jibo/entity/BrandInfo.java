package com.jibo.entity;

import com.jibo.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.jibo.dao.AdminRouteInfoDao;
import com.jibo.dao.BrandInfoDao;
import com.jibo.dao.CompanyInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table BrandInfo.
 * 
 * 药品的商品表 
 */
public class BrandInfo implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
    private String nameCn;
    private String nameEn;
    private String drugId;
    private String companyId;
    private String adminRouteId;
    private String formulationId;
    private String orderDate;
    private String changeDate;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BrandInfoDao myDao;

    private CompanyInfo companyInfo;
    private String companyInfo__resolvedKey;
    
    private AdminRouteInfo adminRouteInfo;
    private String adminRouteInfo__resolvedKey;


    public BrandInfo() {
    }

    public BrandInfo(String id) {
        this.id = id;
    }

    public BrandInfo(String id, String nameCn, String nameEn, String drugId, String companyId, String adminRouteId,String formulationId,String orderDate,String changeDate) {
        this.id = id;
        this.nameCn = nameCn;
        this.nameEn = nameEn;
        this.drugId = drugId;
        this.companyId = companyId;
        this.changeDate = changeDate;
        this.adminRouteId = adminRouteId;
        this.formulationId = formulationId;
        this.orderDate = orderDate;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBrandInfoDao() : null;
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

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    /** To-one relationship, resolved on first access. */
    public CompanyInfo getCompanyInfo() {
        if (companyInfo__resolvedKey == null || companyInfo__resolvedKey != companyId) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CompanyInfoDao targetDao = daoSession.getCompanyInfoDao();
            companyInfo = targetDao.load(companyId);
            companyInfo__resolvedKey = companyId;
        }
        return companyInfo;
    }
    
    
    public CompanyInfo getAdminRouteInfo() {
        if (adminRouteInfo__resolvedKey == null || adminRouteInfo__resolvedKey != adminRouteId) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AdminRouteInfoDao targetDao = daoSession.getAdminRouteInfoDao();
            adminRouteInfo = targetDao.load(adminRouteId);
            adminRouteInfo__resolvedKey = adminRouteId;
        }
        return companyInfo;
    }
    
    
	public void setAdminRouteInfo(AdminRouteInfo adminRouteInfo) {
		this.adminRouteInfo = adminRouteInfo;
        adminRouteId = adminRouteInfo == null ? null : adminRouteInfo.getId();
        adminRouteInfo__resolvedKey = adminRouteId;
	}

	public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
        companyId = companyInfo == null ? null : companyInfo.getId();
        companyInfo__resolvedKey = companyId;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

	public String getAdminRouteId() {
		return adminRouteId;
	}

	public void setAdminRouteId(String adminRouteId) {
		this.adminRouteId = adminRouteId;
	}

	public String getFormulationId() {
		return formulationId;
	}

	public void setFormulationId(String formulationId) {
		this.formulationId = formulationId;
	}
    
    

}
