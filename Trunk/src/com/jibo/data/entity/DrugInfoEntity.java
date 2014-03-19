package com.jibo.data.entity;

import java.io.Serializable;
/**药品信息详细数据
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class DrugInfoEntity extends SimpleDrugInfoEntity implements Serializable{

	public int brandCount;
	public int formulationCount;
	public int specificationCount;
	/**英文品牌名数组*/
	public String brandEn[];
	/**中文品牌名数组*/
	public String brandCn[];
	/**配方数组*/
	public String formulation[];
	/**规格说明书*/
	public String specification[];
	public String otc;
	public int getBrandCount() {
		return brandCount;
	}
	public void setBrandCount(int brandCount) {
		this.brandCount = brandCount;
	}
	public int getFormulationCount() {
		return formulationCount;
	}
	public void setFormulationCount(int formulationCount) {
		this.formulationCount = formulationCount;
	}
	public int getSpecificationCount() {
		return specificationCount;
	}
	public void setSpecificationCount(int specificationCount) {
		this.specificationCount = specificationCount;
	}
	public String[] getBrandEn() {
		return brandEn;
	}
	public void setBrandEn(String[] brandEn) {
		this.brandEn = brandEn;
	}
	public String[] getBrandCn() {
		return brandCn;
	}
	public void setBrandCn(String[] brandCn) {
		this.brandCn = brandCn;
	}
	public String[] getFormulation() {
		return formulation;
	}
	public void setFormulation(String[] formulation) {
		this.formulation = formulation;
	}
	public String[] getSpecification() {
		return specification;
	}
	public void setSpecification(String[] specification) {
		this.specification = specification;
	}
	public String getOtc() {
		return otc;
	}
	public void setOtc(String otc) {
		this.otc = otc;
	}
}
