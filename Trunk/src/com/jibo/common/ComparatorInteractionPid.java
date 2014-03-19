package com.jibo.common;
import java.util.Comparator;

import com.jibo.data.entity.InteractionPidsEntity;

public class ComparatorInteractionPid implements Comparator {

	public int compare(Object arg0, Object arg1) {
		InteractionPidsEntity user0 = (InteractionPidsEntity) arg0;
		InteractionPidsEntity user1 = (InteractionPidsEntity) arg1;
		// 首先比较年龄，如果年龄相同，则比较名字
		int flag = user0.getPid1DrugName().compareTo(user1.getPid1DrugName());
		if (flag == 0) {
			return user0.getPid2DrugName().compareTo(user1.getPid2DrugName());
		} else {
			return flag;
		}
	}

}