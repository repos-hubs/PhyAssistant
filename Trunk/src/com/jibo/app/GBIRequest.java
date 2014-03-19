package com.jibo.app;

import java.util.List;

import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseActivity;
import com.jibo.app.news.NewsAdapter;
import com.jibo.app.news.ProActivity;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.config.DescInfo;

public class GBIRequest extends RequestSrc{


	public GBIRequest(RequestInfos soapInfos, BaseActivity listActivity,
			AdaptInfo adaptInfo, MapAdapter adapter,String flag) {
		super(soapInfos, listActivity, adaptInfo, adapter);
		this.flag = flag;
	}

	public GBIRequest(RequestInfos soapInfos, BaseActivity listActivity,
			AdaptInfo adaptInfo,String flag) {
		super(soapInfos, listActivity, adaptInfo);
		this.flag = flag;
	}

	public GBIRequest(RequestInfos soapInfos, BaseActivity listActivity,
			MapAdapter adapter,String flag) {
		super(soapInfos, listActivity, adapter);
		this.flag = flag;
	}

	public GBIRequest(RequestInfos soapInfos, int tmpPiece, int tmpSoap,
			boolean dataTail, BaseActivity listActivity, int mId,String flag) {
		super(soapInfos, tmpPiece, tmpSoap, dataTail, listActivity, mId);
		this.flag = flag;
		}

	public GBIRequest(RequestInfos soapInfos, int tmpPiece, int tmpSoap,
			boolean dataTail, BaseActivity listActivity,String flag) {
		super(soapInfos, tmpPiece, tmpSoap, dataTail, listActivity);
		this.flag = flag;
		// TODO Auto-generated constructor stub
	}

	public GBIRequest(RequestSrc src) {
		super(src);
		flag = ((GBIRequest)src).flag;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void postHandle(List<EntityObj> eob) {
		// TODO Auto-generated method stub
		super.postHandle(eob);
		DetailsData.clearCacheEntities();
	}

	@Override
	public boolean loadContinue(Object item) {
		// TODO Auto-generated method stub
		
		return super.loadContinue(item);
	}

	@Override
	public void onReload() {
		// TODO Auto-generated method stub
		this.asyncHandler.onReload();
		
	}

}
