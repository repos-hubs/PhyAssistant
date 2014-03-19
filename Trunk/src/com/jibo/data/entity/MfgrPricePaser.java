package com.jibo.data.entity;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.jibo.data.SoapDataPaser;
import com.jibo.data.entity.MfgrPriceEntity.Formulation;
import com.jibo.data.entity.MfgrPriceEntity.Specification;

public class MfgrPricePaser extends SoapDataPaser {

	 public class Pricing {
	    	String productId;
	    	GenericPricing genericPricing;
	    }

	    public class GenericPricing {
	    	String formulationsCount;
	    	Formulation[] formulations;
	    }


	    public Pricing mPricing;

	    public class CompanyBrandsInfo {
	    	String companyBrandsCount;
	    	CompanyBrand[] companyBrands;
	    }

	    public class CompanyBrand {
	    	String companyId;
	    	String companyNameEn;
	    	String companyNameCn;
	    	String brandsCount;
	    	Brand[] brands;
	    }

	    public class Brand {
	    	String brandId;
	    	String nameEn;
	    	String nameCn;
	    	Formulation []formulations;
	    }

	    public CompanyBrandsInfo mCompanyBrandsInfo;
    private static final String INVALID_DATA            = "anyType{}";
    private static final String NULL_STRING             = "";
    private static final String FLAG_FORMULATION_COUNT = "FormulationCount=";
    private static final String FLAG_FORMULATIONS_COUNT = "FormulationsCount=";
	private static final String FLAG_FORMUL             = "Formul=";
	private static final String FLAG_SPECS_COUNT        = "SpecsCount=";
	private static final String FLAG_SPEC               = "Spec=";
	private static final String FLAG_MAX_RETAIL_PRICING = "MaxRetailPricing=";
	private static final String FLAG_EFFECTIVE_YEAR     = "EffectiveYear=";
	private static final String FLAG_AREA               = "Area=";
	private static final String FLAG_AREA_NATIONAL      = "national";

	private static final String FLAG_COMPANY_ID         = "CompanyID=";
	private static final String FLAG_COMPANY_NAME_EN    = "CompanyName_EN=";
	private static final String FLAG_COMPANY_NAME_CN    = "CompanyName_CN=";
	private static final String FLAG_BRANDS_COUNT       = "BrandsCount=";
	private static final String FLAG_BRAND_ID           = "BrandID=";
	private static final String FLAG_NAME_EN            = "Name_EN=";
	private static final String FLAG_NAME_CN            = "Name_CN=";
	
	MfgrPriceEntity  entity;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("GetPricingsByRegionResult");
		String data;
		int postion;
		entity =new MfgrPriceEntity();
		int formulationsCount, specsCount;
		GenericPricing genericPricing;
		Formulation formulation;
		Specification specification;

		int companyBrandsCount, brandsCount;
		CompanyBrand companyBrand;
		Brand brand;

		try{
			genericPricing = new GenericPricing();
			postion = 0;
			data = detail.getProperty(postion).toString();
			mPricing = new Pricing();
			mPricing.productId = data;
			mPricing.genericPricing = genericPricing;

			postion++;
			data = detail.getProperty(postion).toString();

			genericPricing.formulationsCount = data.substring(
					data.indexOf(FLAG_FORMULATIONS_COUNT)+FLAG_FORMULATIONS_COUNT.length(), 
					data.indexOf("; ", data.indexOf(FLAG_FORMULATIONS_COUNT)));
			formulationsCount = new Integer(Integer.parseInt(genericPricing.formulationsCount));
			genericPricing.formulations = new Formulation[formulationsCount];

			for (int i=0; i<formulationsCount; i++) {
				formulation = new Formulation();
				formulation.formul = data.substring(
						data.indexOf(FLAG_FORMUL)+FLAG_FORMUL.length(), 
						data.indexOf("; ", data.indexOf(FLAG_FORMUL)));
				formulation.specsCount = new Integer(data.substring(
						data.indexOf(FLAG_SPECS_COUNT)+FLAG_SPECS_COUNT.length(), 
						data.indexOf("; ", data.indexOf(FLAG_SPECS_COUNT))));
				specsCount = formulation.specsCount;
				formulation.specs = new Specification[specsCount];

				if (formulation.formul.equals(INVALID_DATA))
					formulation.formul = NULL_STRING;

				genericPricing.formulations[i] = formulation;
				for (int j=0; j<specsCount; j++) {
					specification = new Specification();
					specification.spec = data.substring(
							data.indexOf(FLAG_SPEC)+FLAG_SPEC.length(), 
							data.indexOf("; ", data.indexOf(FLAG_SPEC)));
					specification.maxRetailPricing = data.substring(
							data.indexOf(FLAG_MAX_RETAIL_PRICING)+FLAG_MAX_RETAIL_PRICING.length(), 
							data.indexOf("; ", data.indexOf(FLAG_MAX_RETAIL_PRICING)));
					specification.effectiveYear = data.substring(
							data.indexOf(FLAG_EFFECTIVE_YEAR)+FLAG_EFFECTIVE_YEAR.length(), 
							data.indexOf("; ", data.indexOf(FLAG_EFFECTIVE_YEAR)));
					specification.area = data.substring(
							data.indexOf(FLAG_AREA)+FLAG_AREA.length(), 
							data.indexOf("; ", data.indexOf(FLAG_AREA)));

					formulation.specs[j] = specification;

					if (specification.spec.equals(INVALID_DATA))
						specification.spec = NULL_STRING;
					if (specification.maxRetailPricing.equals(INVALID_DATA))
						specification.maxRetailPricing = NULL_STRING;
					if (specification.effectiveYear.equals(INVALID_DATA))
						specification.effectiveYear = NULL_STRING;
					if (specification.area.equals(INVALID_DATA))
						specification.area = NULL_STRING;
					if (specification.area.equals(FLAG_AREA_NATIONAL))
						specification.area = "国家";

					data = data.substring(data.indexOf(FLAG_AREA)+FLAG_AREA.length());
				}
			}

			postion++;
			data = detail.getProperty(postion).toString();
			mCompanyBrandsInfo = new CompanyBrandsInfo();
			mCompanyBrandsInfo.companyBrandsCount = data;
			companyBrandsCount = new Integer(Integer.parseInt(mCompanyBrandsInfo.companyBrandsCount));
			mCompanyBrandsInfo.companyBrands = new CompanyBrand[companyBrandsCount];

			postion++;
			data = detail.getProperty(postion).toString();
			for (int i=0; i<companyBrandsCount; i++) {
				companyBrand = new CompanyBrand();
				mCompanyBrandsInfo.companyBrands[i] = companyBrand;

				companyBrand.companyId = data.substring(
						data.indexOf(FLAG_COMPANY_ID)+FLAG_COMPANY_ID.length(), 
						data.indexOf("; ", data.indexOf(FLAG_COMPANY_ID)));
				companyBrand.companyNameEn = data.substring(
						data.indexOf(FLAG_COMPANY_NAME_EN)+FLAG_COMPANY_NAME_EN.length(), 
						data.indexOf("; ", data.indexOf(FLAG_COMPANY_NAME_EN)));
				companyBrand.companyNameCn = data.substring(
						data.indexOf(FLAG_COMPANY_NAME_CN)+FLAG_COMPANY_NAME_CN.length(), 
						data.indexOf("; ", data.indexOf(FLAG_COMPANY_NAME_CN)));
				companyBrand.brandsCount = data.substring(
						data.indexOf(FLAG_BRANDS_COUNT)+FLAG_BRANDS_COUNT.length(), 
						data.indexOf("; ", data.indexOf(FLAG_BRANDS_COUNT)));
				brandsCount = new Integer(Integer.parseInt(companyBrand.brandsCount));
				companyBrand.brands = new Brand[brandsCount];

				if (companyBrand.companyId.equals(INVALID_DATA))
					companyBrand.companyId = NULL_STRING;
				if (companyBrand.companyNameEn.equals(INVALID_DATA))
					companyBrand.companyNameEn = NULL_STRING;
				if (companyBrand.companyNameCn.equals(INVALID_DATA))
					companyBrand.companyNameCn = NULL_STRING;
				if (companyBrand.brandsCount.equals(INVALID_DATA))
					companyBrand.brandsCount = NULL_STRING;

				data = data.substring(data.indexOf(FLAG_BRANDS_COUNT)+FLAG_BRANDS_COUNT.length());
				for (int j=0; j<brandsCount; j++) {
					brand = new Brand();
					companyBrand.brands[j] = brand;

					brand.brandId = data.substring(
							data.indexOf(FLAG_BRAND_ID)+FLAG_BRAND_ID.length(), 
							data.indexOf("; ", data.indexOf(FLAG_BRAND_ID)));
					brand.nameEn = data.substring(
							data.indexOf(FLAG_NAME_EN)+FLAG_NAME_EN.length(), 
							data.indexOf("; ", data.indexOf(FLAG_NAME_EN)));
					brand.nameCn = data.substring(
							data.indexOf(FLAG_NAME_CN)+FLAG_NAME_CN.length(), 
							data.indexOf("; ", data.indexOf(FLAG_NAME_CN)));

					if (brand.brandId.equals(INVALID_DATA))
						brand.brandId = NULL_STRING;
					if (brand.nameEn.equals(INVALID_DATA))
						brand.nameEn = NULL_STRING;
					if (brand.nameCn.equals(INVALID_DATA))
						brand.nameCn = NULL_STRING;

					formulationsCount = Integer.parseInt(data.substring(
							data.indexOf(FLAG_FORMULATION_COUNT)+FLAG_FORMULATION_COUNT.length(), 
							data.indexOf("; ", data.indexOf(FLAG_FORMULATION_COUNT))));
					brand.formulations = new Formulation[formulationsCount];

					for (int ii=0; ii<formulationsCount; ii++) {						
						formulation = new Formulation();						
						formulation.formul = data.substring(
								data.indexOf(FLAG_FORMUL)+FLAG_FORMUL.length(), 
								data.indexOf("; ", data.indexOf(FLAG_FORMUL)));
						formulation.specsCount = new Integer(data.substring(
								data.indexOf(FLAG_SPECS_COUNT)+FLAG_SPECS_COUNT.length(), 
								data.indexOf("; ", data.indexOf(FLAG_SPECS_COUNT))));
						specsCount =formulation.specsCount;
						formulation.specs = new Specification[specsCount];

						if (formulation.formul.equals(INVALID_DATA))
							formulation.formul = NULL_STRING;

						brand.formulations[ii] = formulation;
						for (int jj=0; jj<specsCount; jj++) {
							specification = new Specification();
							specification.spec = data.substring(
									data.indexOf(FLAG_SPEC)+FLAG_SPEC.length(), 
									data.indexOf("; ", data.indexOf(FLAG_SPEC)));
							specification.maxRetailPricing = data.substring(
									data.indexOf(FLAG_MAX_RETAIL_PRICING)+FLAG_MAX_RETAIL_PRICING.length(), 
									data.indexOf("; ", data.indexOf(FLAG_MAX_RETAIL_PRICING)));
							specification.effectiveYear = data.substring(
									data.indexOf(FLAG_EFFECTIVE_YEAR)+FLAG_EFFECTIVE_YEAR.length(), 
									data.indexOf("; ", data.indexOf(FLAG_EFFECTIVE_YEAR)));
							specification.area = data.substring(
									data.indexOf(FLAG_AREA)+FLAG_AREA.length(), 
									data.indexOf("; ", data.indexOf(FLAG_AREA)));

							brand.formulations[ii] .specs[jj] = specification;

							if (specification.spec.equals(INVALID_DATA))
								specification.spec = NULL_STRING;
							if (specification.maxRetailPricing.equals(INVALID_DATA))
								specification.maxRetailPricing = NULL_STRING;
							if (specification.effectiveYear.equals(INVALID_DATA))
								specification.effectiveYear = NULL_STRING;
							if (specification.area.equals(INVALID_DATA))
								specification.area = NULL_STRING;
							if (specification.area.equals(FLAG_AREA_NATIONAL))
								specification.area = "国家";

							data = data.substring(data.indexOf(FLAG_AREA)+FLAG_AREA.length());
						}
					
					}
					data = data.substring(data.indexOf(FLAG_NAME_CN)+FLAG_NAME_CN.length());
				}
			}
		}catch (Exception e){
			Log.e("MfgrPrice", "parse manufacturers price error");
			e.printStackTrace();//prime for debug 2011-8-18.
		}
	}
	public Pricing getPricing() {
		return mPricing;
	}

	public String getProductId() {
		return mPricing.productId;
	}

	public GenericPricing getGenericPricing() {
		return mPricing.genericPricing;
	}

	public String getFormulationsCount() {
		return mPricing.genericPricing.formulationsCount != null ? mPricing.genericPricing.formulationsCount: "0";
	}

	public Formulation[] getFormulations() {
		return mPricing.genericPricing.formulations;
	}

	public String getFormul(int index) {
		return mPricing.genericPricing.formulations[index].formul;
	}

	public int getSpecsCount(int index) {
		return mPricing.genericPricing.formulations[index].specsCount;
	}

	public Specification[] getSpecs(int index) {
		return mPricing.genericPricing.formulations[index].specs;
	}

	public String getSpec(int i1, int i2) {
		return mPricing.genericPricing.formulations[i1].specs[i2].spec;
	}

	public String getMaxRetailPricing(int i1, int i2) {
		return mPricing.genericPricing.formulations[i1].specs[i2].maxRetailPricing;
	}

	public String getEffectiveYear(int i1, int i2) {
		return mPricing.genericPricing.formulations[i1].specs[i2].effectiveYear;
	}

	public String getArea(int i1, int i2) {
		return mPricing.genericPricing.formulations[i1].specs[i2].area;
	}

	public CompanyBrandsInfo getCompanyBrandsInfo() {
		return mCompanyBrandsInfo;
	}

	public String getCompanyBrandsCount() {
		return mCompanyBrandsInfo != null ? mCompanyBrandsInfo.companyBrandsCount : "0";
	}

	public CompanyBrand[] getCompanyBrands() {
		return mCompanyBrandsInfo.companyBrands;
	}

	public String getCompanyId(int index) {
		return mCompanyBrandsInfo.companyBrands[index].companyId;
	}

	public String getCompanyNameEn(int index) {
		return mCompanyBrandsInfo.companyBrands[index].companyNameEn;
	}

	public String getCompanyNameCn(int index) {
		return mCompanyBrandsInfo.companyBrands[index].companyNameCn;
	}

	public String getBrandsCount(int index) {
		return mCompanyBrandsInfo.companyBrands[index].brandsCount;
	}

	public Brand[] getBrands(int index) {
		return mCompanyBrandsInfo.companyBrands[index].brands;
	}

	public String getBrandId(int i1, int i2) {
		return mCompanyBrandsInfo.companyBrands[i1].brands[i2].brandId;
	}

	public String getNameEn(int i1, int i2) {
		return mCompanyBrandsInfo.companyBrands[i1].brands[i2].nameEn;
	}

	public String getNameCn(int i1, int i2) {
		return mCompanyBrandsInfo.companyBrands[i1].brands[i2].nameCn;
	}
	
	public Formulation[] getFormulation(int i1, int i2) {
		return mCompanyBrandsInfo.companyBrands[i1].brands[i2].formulations;
	}
}
