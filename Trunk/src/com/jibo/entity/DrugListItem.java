package com.jibo.entity;

/**
 * ҩƷ��ʾ�б�item����������װ��Ϊ��ʵ����ϵ������Ϣ��ҩƷ��Ϣ����
 *
 */
public class DrugListItem {
	//��ϵ����
	public ManufutureBrandInfo brandInfo;
	//ҩƷ��Ϣ
	public DrugInfo  drugInfo;
	//�Ƿ�ΪҩƷ
	public boolean isDrug;
	
	public DrugListItem(ManufutureBrandInfo brandInfo, DrugInfo drugInfo,
			boolean isDrug) {
		super();
		this.brandInfo = brandInfo;
		this.drugInfo = drugInfo;
		this.isDrug = isDrug;
	}
	
}
