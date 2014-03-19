package com.jibo.data.entity;


/**
 * Ò©Æ·¼Û¸ñ
 * */
public class MfgrPriceEntity {

	public class Pricing {
    	String productId;
    	public GenericPricing genericPricing;
    }

    public class GenericPricing {
    	public int formulationsCount;
    	public Formulation[] formulations;
    }

    public static class Formulation {
    	public String formul;
    	public int specsCount;
    	public Specification[] specs;
    }

    public static class Specification {
    	public String spec;
    	public String maxRetailPricing;
    	public String effectiveYear;
    	public String area;
    }

    public class CompanyBrandsInfo {
    	public int companyBrandsCount;
    	public CompanyBrand[] companyBrands;
    }

    public class CompanyBrand {
    	String companyId;
    	String companyNameEn;
    	public String companyNameCn;
    	public int brandsCount;
    	public Brand[] brands;
    }

    public class Brand {
    	String brandId;
    	String nameEn;
    	public String nameCn;
    	public Formulation[] formulations;
    }


	public Pricing mPricing;
    public CompanyBrandsInfo mMfgrBrandsInfo;
    MfgrPricePaser mManufacturersPrice;
    
    private void FillPriceData() {
		int formulationsCount, specsCount;
		GenericPricing genericPricing;
		Formulation formulation;
		Specification specification;

		try {
			genericPricing = new GenericPricing();

			mPricing = new Pricing();
			mPricing.productId = mManufacturersPrice.getProductId();
			mPricing.genericPricing = genericPricing;

			
			formulationsCount = new Integer(Integer.parseInt(mManufacturersPrice.getFormulationsCount()));
			genericPricing.formulationsCount = formulationsCount;
			genericPricing.formulations = new Formulation[formulationsCount];

			for (int i=0; i<formulationsCount; i++) {
				formulation = new Formulation();
				formulation.formul = mManufacturersPrice.getFormul(i);
				specsCount = mManufacturersPrice.getSpecsCount(i);
				formulation.specsCount = specsCount;
				formulation.specs = new Specification[specsCount];

				genericPricing.formulations[i] = formulation;
				for (int j=0; j<specsCount; j++) {
					specification = new Specification();
					specification.spec = mManufacturersPrice.getSpec(i, j);
					specification.maxRetailPricing = mManufacturersPrice.getMaxRetailPricing(i, j);
					specification.effectiveYear = mManufacturersPrice.getEffectiveYear(i, j);
					specification.area = mManufacturersPrice.getArea(i, j);

					formulation.specs[j] = specification;
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	private void FillMfgrBrandData() {
		int companyBrandsCount, brandsCount;
		CompanyBrand companyBrand;
		Brand brand;

		try {
			mMfgrBrandsInfo = new CompanyBrandsInfo();
			companyBrandsCount = new Integer(Integer.parseInt(mManufacturersPrice.getCompanyBrandsCount()));
			mMfgrBrandsInfo.companyBrandsCount = companyBrandsCount;
			mMfgrBrandsInfo.companyBrands = new CompanyBrand[companyBrandsCount];

			for (int i=0; i<companyBrandsCount; i++) {
				companyBrand = new CompanyBrand();
				mMfgrBrandsInfo.companyBrands[i] = companyBrand;

				companyBrand.companyId     = mManufacturersPrice.getCompanyId(i);
				companyBrand.companyNameEn = mManufacturersPrice.getCompanyNameEn(i);
				companyBrand.companyNameCn = mManufacturersPrice.getCompanyNameCn(i);
				brandsCount = new Integer(Integer.parseInt(mManufacturersPrice.getBrandsCount(i)));
				companyBrand.brandsCount = brandsCount;
				companyBrand.brands = new Brand[brandsCount];

				for (int j=0; j<brandsCount; j++) {
					brand = new Brand();
					companyBrand.brands[j] = brand;

					brand.brandId = mManufacturersPrice.getBrandId(i, j);
					brand.nameEn = mManufacturersPrice.getNameEn(i, j);
					brand.nameCn = mManufacturersPrice.getNameCn(i, j);
					brand.formulations=mManufacturersPrice.getFormulation(i, j);
					
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	public void FillMfgrPriceData(MfgrPricePaser price) throws Exception {
		mManufacturersPrice=price;
		FillPriceData();
		FillMfgrBrandData();
	}

}
