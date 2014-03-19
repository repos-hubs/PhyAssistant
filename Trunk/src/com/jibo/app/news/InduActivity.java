package com.jibo.app.news;

import java.util.List;
import java.util.Map.Entry;

import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.config.DescInfo;


public class InduActivity extends ProActivity {

	@Override
	public void sort(List<EntityObj> eob, Entry<DescInfo, Integer> entry) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void enter() {
		// TODO Auto-generated method stub
		srcRequests.startCat(new String[]{""});
	}

}
