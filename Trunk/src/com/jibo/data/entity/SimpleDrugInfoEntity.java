package com.jibo.data.entity;

import java.io.Serializable;

import com.jibo.common.Constant;
/**ҩƷ������Ϣ����
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class SimpleDrugInfoEntity implements Serializable{
	
	public String id=Constant.SPACE;
	/**������*/
	public String nameEn=Constant.SPACE;
	/**������*/
	public String nameCn=Constant.SPACE;
	public String atc=Constant.SPACE;
	/**Ʒ����*/
	public String brandName=Constant.SPACE;
	
	public int flag;//0��ҩƷ��1��Ʒ��
}
