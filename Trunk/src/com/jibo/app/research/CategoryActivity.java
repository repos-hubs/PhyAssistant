package com.jibo.app.research;

import android.os.Bundle;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.base.src.FloorActivity;
import com.jibo.base.src.FloorPack;
import com.jibo.base.src.RequestController;
import com.jibo.util.ActivityPool;

public class CategoryActivity extends FloorActivity {
	public static RequestController srcRequests;
	public static String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String category = this.getIntent().getStringExtra("category");
		title = this.getIntent().getStringExtra("title");
		((TextView)findViewById(R.id.moduletitle)).setText(title);
		if (category == null)
			finish();
		if (srcRequests == null) {
			srcRequests = new RequestController(null, this);
			srcRequests.dynActivity = true;
		}
		CategoryActivity.srcRequests = srcRequests;
		ActivityPool.getInstance().activityMap.put(this.getClass(), this);
		new FloorPack().startup(this, category, srcRequests);
		if (category.toLowerCase().contains("journal")) {
			initSearch();
		}
	}

	@Override
	public void finish() {
		super.finish();
		if(CategoryActivity.srcRequests!=null&&CategoryActivity.srcRequests.atymap!=null){
			CategoryActivity.srcRequests.atymap.clear();			
		}
		CategoryActivity.srcRequests = null;
		FloorPack.hasHead = false;
	}

}
