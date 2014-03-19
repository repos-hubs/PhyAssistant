package com.jibo.dao;

import java.util.Map;

import com.jibo.entity.AdminRouteInfo;
import com.jibo.entity.AhfsDrugInfo;
import com.jibo.entity.BrandInfo;
import com.jibo.entity.CalcSearch;
import com.jibo.entity.CategoryFormulaMapping;
import com.jibo.entity.CompanyInfo;
import com.jibo.entity.ContactManufuture;
import com.jibo.entity.Department;
import com.jibo.entity.DptRelation;
import com.jibo.entity.DrugDetailInfo;
import com.jibo.entity.DrugDetailTypeInfo;
import com.jibo.entity.DrugInfo;
import com.jibo.entity.Formula;
import com.jibo.entity.FormulaCategory;
import com.jibo.entity.FormulaRank;
import com.jibo.entity.FormulationInfo;
import com.jibo.entity.Hospital;
import com.jibo.entity.HospitalCity;
import com.jibo.entity.HospitalProvince;
import com.jibo.entity.ManufutureBrandInfo;
import com.jibo.entity.SearchDrug;
import com.jibo.entity.TADrug;
import com.jibo.entity.TADrugRef;
import com.jibo.entity.UnitValues;
import com.jibo.entity.Units;
import com.jibo.entity.Version;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.IdentityScopeType;

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

	private DaoConfig formulaCategoryDaoConfig;
	private DaoConfig formulaDaoConfig;
	private DaoConfig categoryFormulaMappingDaoConfig;
	private DaoConfig formulaRankDaoConfig;
	private DaoConfig unitsDaoConfig;
	private DaoConfig unitValuesDaoConfig;
	private DaoConfig calcSearchDaoConfig;
	private DaoConfig dptRelationDaoConfig;
	private DaoConfig drugInfoDaoConfig;
	private DaoConfig brandInfoDaoConfig;
	private DaoConfig companyInfoDaoConfig;
	private DaoConfig tADrugDaoConfig;
	private DaoConfig tADrugRefDaoConfig;
	private DaoConfig drugDetailInfoDaoConfig;
	private DaoConfig drugDetailTypeInfoDaoConfig;
	private DaoConfig adminRouteInfoDaoConfig;
	private DaoConfig formulationInfoDaoConfig;
	private DaoConfig ahfsDrugInfoDaoConfig;
	private DaoConfig contactManufutureDaoConfig;
	private DaoConfig manufutureBrandInfoDaoConfig;
	private DaoConfig hospitalProvinceDaoConfig;
	private DaoConfig hospitalCityDaoConfig;
	private DaoConfig hospitalDaoConfig;
	private DaoConfig departmentDaoConfig;
	private DaoConfig searchDrugDaoConfig;
	private DaoConfig versionDaoConfig;

	    

	private FormulaCategoryDao formulaCategoryDao;
	private FormulaDao formulaDao;
	private CategoryFormulaMappingDao categoryFormulaMappingDao;
	private FormulaRankDao formulaRankDao;
	private UnitsDao unitsDao;
	private UnitValuesDao unitValuesDao;
	private CalcSearchDao calcSearchDao;
	private DptRelationDao dptRelationDao;
	private DrugInfoDao drugInfoDao;
	private BrandInfoDao brandInfoDao;
	private CompanyInfoDao companyInfoDao;
	private TADrugDao tADrugDao;
	private TADrugRefDao tADrugRefDao;
	private DrugDetailInfoDao drugDetailInfoDao;
	private DrugDetailTypeInfoDao drugDetailTypeInfoDao;
	private AdminRouteInfoDao adminRouteInfoDao;
	private FormulationInfoDao formulationInfoDao;
	private AhfsDrugInfoDao ahfsDrugInfoDao;
	private ContactManufutureDao contactManufutureDao;
	private ManufutureBrandInfoDao manufutureBrandInfoDao;
	private HospitalProvinceDao hospitalProvinceDao;
	private HospitalCityDao hospitalCityDao;
	private HospitalDao hospitalDao;
	private DepartmentDao departmentDao;
	private SearchDrugDao searchDrugDao;
	private VersionDao versionDao;

	private boolean isSDCardDB;

	public DaoSession(DBHelper helper, IdentityScopeType type,
			Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
		super(helper.getSQLiteDatabase());
		isSDCardDB = helper.isSDCardDB();
		if (isSDCardDB) {
			formulaCategoryDaoConfig = daoConfigMap.get(
					FormulaCategoryDao.class).clone();
			formulaCategoryDaoConfig.initIdentityScope(type);

			formulaDaoConfig = daoConfigMap.get(FormulaDao.class).clone();
			formulaDaoConfig.initIdentityScope(type);

			categoryFormulaMappingDaoConfig = daoConfigMap.get(
					CategoryFormulaMappingDao.class).clone();
			categoryFormulaMappingDaoConfig.initIdentityScope(type);

			formulaRankDaoConfig = daoConfigMap.get(FormulaRankDao.class)
					.clone();
			formulaRankDaoConfig.initIdentityScope(type);

			unitsDaoConfig = daoConfigMap.get(UnitsDao.class).clone();
			unitsDaoConfig.initIdentityScope(type);

			unitValuesDaoConfig = daoConfigMap.get(UnitValuesDao.class).clone();
			unitValuesDaoConfig.initIdentityScope(type);

			calcSearchDaoConfig = daoConfigMap.get(CalcSearchDao.class).clone();
			calcSearchDaoConfig.initIdentityScope(type);

			dptRelationDaoConfig = daoConfigMap.get(DptRelationDao.class)
					.clone();
			dptRelationDaoConfig.initIdentityScope(type);

			drugInfoDaoConfig = daoConfigMap.get(DrugInfoDao.class).clone();
			drugInfoDaoConfig.initIdentityScope(type);

			brandInfoDaoConfig = daoConfigMap.get(BrandInfoDao.class).clone();
			brandInfoDaoConfig.initIdentityScope(type);

			companyInfoDaoConfig = daoConfigMap.get(CompanyInfoDao.class)
					.clone();
			companyInfoDaoConfig.initIdentityScope(type);

			tADrugDaoConfig = daoConfigMap.get(TADrugDao.class).clone();
			tADrugDaoConfig.initIdentityScope(type);

			tADrugRefDaoConfig = daoConfigMap.get(TADrugRefDao.class).clone();
			tADrugRefDaoConfig.initIdentityScope(type);

			drugDetailInfoDaoConfig = daoConfigMap.get(DrugDetailInfoDao.class)
					.clone();
			drugDetailInfoDaoConfig.initIdentityScope(type);

			drugDetailTypeInfoDaoConfig = daoConfigMap.get(
					DrugDetailTypeInfoDao.class).clone();
			drugDetailTypeInfoDaoConfig.initIdentityScope(type);

			adminRouteInfoDaoConfig = daoConfigMap.get(AdminRouteInfoDao.class)
					.clone();
			adminRouteInfoDaoConfig.initIdentityScope(type);

			formulationInfoDaoConfig = daoConfigMap.get(
					FormulationInfoDao.class).clone();
			formulationInfoDaoConfig.initIdentityScope(type);

			ahfsDrugInfoDaoConfig = daoConfigMap.get(AhfsDrugInfoDao.class)
					.clone();
			ahfsDrugInfoDaoConfig.initIdentityScope(type);

			contactManufutureDaoConfig = daoConfigMap.get(
					ContactManufutureDao.class).clone();
			contactManufutureDaoConfig.initIdentityScope(type);

			manufutureBrandInfoDaoConfig = daoConfigMap.get(
					ManufutureBrandInfoDao.class).clone();
			manufutureBrandInfoDaoConfig.initIdentityScope(type);
			hospitalProvinceDaoConfig = daoConfigMap.get(
					HospitalProvinceDao.class).clone();
			hospitalProvinceDaoConfig.initIdentityScope(type);

			hospitalCityDaoConfig = daoConfigMap.get(HospitalCityDao.class)
					.clone();
			hospitalCityDaoConfig.initIdentityScope(type);

			hospitalDaoConfig = daoConfigMap.get(HospitalDao.class).clone();
			hospitalDaoConfig.initIdentityScope(type);

			departmentDaoConfig = daoConfigMap.get(DepartmentDao.class).clone();
			departmentDaoConfig.initIdentityScope(type);

			searchDrugDaoConfig = daoConfigMap.get(SearchDrugDao.class).clone();
			searchDrugDaoConfig.initIdentityScope(type);
			versionDaoConfig = daoConfigMap.get(VersionDao.class).clone();
	        versionDaoConfig.initIdentityScope(type);

	        

			formulaCategoryDao = new FormulaCategoryDao(
					formulaCategoryDaoConfig, this);
			formulaDao = new FormulaDao(formulaDaoConfig, this);
			categoryFormulaMappingDao = new CategoryFormulaMappingDao(
					categoryFormulaMappingDaoConfig, this);
			formulaRankDao = new FormulaRankDao(formulaRankDaoConfig, this);
			unitsDao = new UnitsDao(unitsDaoConfig, this);
			unitValuesDao = new UnitValuesDao(unitValuesDaoConfig, this);
			calcSearchDao = new CalcSearchDao(calcSearchDaoConfig, this);
			dptRelationDao = new DptRelationDao(dptRelationDaoConfig, this);
			drugInfoDao = new DrugInfoDao(drugInfoDaoConfig, this);
			brandInfoDao = new BrandInfoDao(brandInfoDaoConfig, this);
			companyInfoDao = new CompanyInfoDao(companyInfoDaoConfig, this);
			tADrugDao = new TADrugDao(tADrugDaoConfig, this);
			tADrugRefDao = new TADrugRefDao(tADrugRefDaoConfig, this);
			drugDetailInfoDao = new DrugDetailInfoDao(drugDetailInfoDaoConfig,
					this);
			drugDetailTypeInfoDao = new DrugDetailTypeInfoDao(
					drugDetailTypeInfoDaoConfig, this);
			adminRouteInfoDao = new AdminRouteInfoDao(adminRouteInfoDaoConfig,
					this);
			formulationInfoDao = new FormulationInfoDao(
					formulationInfoDaoConfig, this);
			ahfsDrugInfoDao = new AhfsDrugInfoDao(ahfsDrugInfoDaoConfig, this);
			contactManufutureDao = new ContactManufutureDao(
					contactManufutureDaoConfig, this);
			manufutureBrandInfoDao = new ManufutureBrandInfoDao(
					manufutureBrandInfoDaoConfig, this);
			hospitalProvinceDao = new HospitalProvinceDao(
					hospitalProvinceDaoConfig, this);
			hospitalCityDao = new HospitalCityDao(hospitalCityDaoConfig, this);
			hospitalDao = new HospitalDao(hospitalDaoConfig, this);
			departmentDao = new DepartmentDao(departmentDaoConfig, this);
			searchDrugDao = new SearchDrugDao(searchDrugDaoConfig, this);
			versionDao = new VersionDao(versionDaoConfig, this);


			registerDao(FormulaCategory.class, formulaCategoryDao);
			registerDao(Formula.class, formulaDao);
			registerDao(CategoryFormulaMapping.class, categoryFormulaMappingDao);
			registerDao(FormulaRank.class, formulaRankDao);
			registerDao(Units.class, unitsDao);
			registerDao(UnitValues.class, unitValuesDao);
			registerDao(CalcSearch.class, calcSearchDao);
			registerDao(DptRelation.class, dptRelationDao);
			registerDao(DrugInfo.class, drugInfoDao);
			registerDao(BrandInfo.class, brandInfoDao);
			registerDao(CompanyInfo.class, companyInfoDao);
			registerDao(TADrug.class, tADrugDao);
			registerDao(TADrugRef.class, tADrugRefDao);
			registerDao(DrugDetailInfo.class, drugDetailInfoDao);
			registerDao(DrugDetailTypeInfo.class, drugDetailTypeInfoDao);
			registerDao(AdminRouteInfo.class, adminRouteInfoDao);
			registerDao(FormulationInfo.class, formulationInfoDao);
			registerDao(AhfsDrugInfo.class, ahfsDrugInfoDao);
			registerDao(ContactManufuture.class, contactManufutureDao);
			registerDao(ManufutureBrandInfo.class, manufutureBrandInfoDao);
			registerDao(HospitalProvince.class, hospitalProvinceDao);
			registerDao(HospitalCity.class, hospitalCityDao);
			registerDao(Hospital.class, hospitalDao);
			registerDao(Department.class, departmentDao);
			registerDao(SearchDrug.class, searchDrugDao);
			registerDao(Version.class, versionDao);
		}
	}

	public void clear() {
		if (isSDCardDB) {
			formulaCategoryDaoConfig.getIdentityScope().clear();
			formulaDaoConfig.getIdentityScope().clear();
			categoryFormulaMappingDaoConfig.getIdentityScope().clear();
			formulaRankDaoConfig.getIdentityScope().clear();
			unitsDaoConfig.getIdentityScope().clear();
			unitValuesDaoConfig.getIdentityScope().clear();
			calcSearchDaoConfig.getIdentityScope().clear();
			dptRelationDaoConfig.getIdentityScope().clear();
			drugInfoDaoConfig.getIdentityScope().clear();
			brandInfoDaoConfig.getIdentityScope().clear();
			companyInfoDaoConfig.getIdentityScope().clear();
			tADrugDaoConfig.getIdentityScope().clear();
			tADrugRefDaoConfig.getIdentityScope().clear();
			drugDetailInfoDaoConfig.getIdentityScope().clear();
			drugDetailTypeInfoDaoConfig.getIdentityScope().clear();
			adminRouteInfoDaoConfig.getIdentityScope().clear();
			formulationInfoDaoConfig.getIdentityScope().clear();
			ahfsDrugInfoDaoConfig.getIdentityScope().clear();
			contactManufutureDaoConfig.getIdentityScope().clear();
			manufutureBrandInfoDaoConfig.getIdentityScope().clear();
			hospitalProvinceDaoConfig.getIdentityScope().clear();
			hospitalCityDaoConfig.getIdentityScope().clear();
			hospitalDaoConfig.getIdentityScope().clear();
			departmentDaoConfig.getIdentityScope().clear();
			searchDrugDaoConfig.getIdentityScope().clear();
			versionDaoConfig.getIdentityScope().clear();
		}
	}

	public FormulaCategoryDao getFormulaCategoryDao() {
		return formulaCategoryDao;
	}

	public FormulaDao getFormulaDao() {
		return formulaDao;
	}

	public CategoryFormulaMappingDao getCategoryFormulaMappingDao() {
		return categoryFormulaMappingDao;
	}

	public FormulaRankDao getFormulaRankDao() {
		return formulaRankDao;
	}

	public UnitsDao getUnitsDao() {
		return unitsDao;
	}

	public UnitValuesDao getUnitValuesDao() {
		return unitValuesDao;
	}

	public CalcSearchDao getCalcSearchDao() {
		return calcSearchDao;
	}

	public DptRelationDao getDptRelationDao() {
		return dptRelationDao;
	}

	public DrugInfoDao getDrugInfoDao() {
		return drugInfoDao;
	}

	public BrandInfoDao getBrandInfoDao() {
		return brandInfoDao;
	}

	public CompanyInfoDao getCompanyInfoDao() {
		return companyInfoDao;
	}

	public TADrugDao gettADrugDao() {
		return tADrugDao;
	}

	public TADrugRefDao gettADrugRefDao() {
		return tADrugRefDao;
	}

	public DrugDetailInfoDao getDrugDetailInfoDao() {
		return drugDetailInfoDao;
	}

	public DrugDetailTypeInfoDao getDrugDetailTypeInfoDao() {
		return drugDetailTypeInfoDao;
	}

	public AdminRouteInfoDao getAdminRouteInfoDao() {
		return adminRouteInfoDao;
	}

	public FormulationInfoDao getFormulationInfoDao() {
		return formulationInfoDao;
	}

	public AhfsDrugInfoDao getAhfsDrugInfoDao() {
		return ahfsDrugInfoDao;
	}

	public ContactManufutureDao getContactManufutureDao() {
		return contactManufutureDao;
	}

	public ManufutureBrandInfoDao getManufutureBrandInfoDao() {
		return manufutureBrandInfoDao;
	}

	public HospitalProvinceDao getHospitalProvinceDao() {
		return hospitalProvinceDao;
	}

	public HospitalCityDao getHospitalCityDao() {
		return hospitalCityDao;
	}

	public HospitalDao getHospitalDao() {
		return hospitalDao;
	}

	public DepartmentDao getDepartmentDao() {
		return departmentDao;
	}

	public SearchDrugDao getSearchDrugDao() {
		return searchDrugDao;
	}
	
	public VersionDao getVersionDao() {
        return versionDao;
    }
}
