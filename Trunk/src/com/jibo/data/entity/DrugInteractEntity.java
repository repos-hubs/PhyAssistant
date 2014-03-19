package com.jibo.data.entity;

public class DrugInteractEntity {
	public String drugName;
	public String drugId;
	public String description;
	public String comments;
	public String isOTC;
	public String isTCM;
	public DrugInteractEntity() {
		super();
	}
	public DrugInteractEntity(String drugId,String drugName) {
		super();
		this.drugName = drugName;
		this.drugId = drugId;
	}
}
