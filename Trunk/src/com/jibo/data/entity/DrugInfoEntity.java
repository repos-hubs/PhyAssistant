package com.jibo.data.entity;

import java.io.Serializable;
/**ҩƷ��Ϣ��ϸ����
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class DrugInfoEntity extends SimpleDrugInfoEntity implements Serializable{

	public int brandCount;
	public int formulationCount;
	public int specificationCount;
	/**Ӣ��Ʒ��������*/
	public String brandEn[];
	/**����Ʒ��������*/
	public String brandCn[];
	/**�䷽����*/
	public String formulation[];
	/**���˵����*/
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
