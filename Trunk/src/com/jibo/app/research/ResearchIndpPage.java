package com.jibo.app.research;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.api.android.GBApp.R;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.util.ActivityPool;

public class ResearchIndpPage extends com.jibo.base.search.SearchActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.research_page_new);

		this.findViewById(R.id.imgbtn_home).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Activity aty;
						Set<RequestSrc> set = new HashSet<RequestSrc>(CategoryActivity.srcRequests.atymap.keySet());
						set.addAll(set);
						for (RequestSrc src : set) {
							aty = CategoryActivity.srcRequests.atymap.get(src);
							CategoryActivity.srcRequests.atymap.remove(src);
							if (aty == null) {
								continue;
							}
							aty.onKeyDown(KeyEvent.KEYCODE_BACK, null);							
						}
						if(CategoryActivity.srcRequests!=null){
							ActivityPool.getInstance().activityMap.get(
									CategoryActivity.class).onKeyDown(
											KeyEvent.KEYCODE_BACK, null);							
						}
						
					}

				});
	}
}
