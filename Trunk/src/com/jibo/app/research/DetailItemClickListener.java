package com.jibo.app.research;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;

import com.jibo.activity.BaseActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.updatespot.SpotUtil;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.src.EntityObj;
import com.jibo.common.Constant;
import com.jibo.util.ActivityPool;
import com.jibo.util.DebugInst;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

public class DetailItemClickListener implements OnItemClickListener {
	
	public String launchKey = null;
	private String eventflag = "";
	
	public DetailItemClickListener(String string,String launchKey) {
		super();
		this.eventflag = string;
		this.launchKey = launchKey;
	}

	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			new DebugInst(){

				@Override
				public void forDebug() {
					Logs.i("================================ journal e");
					if(eventflag.equals("research_browse_enJournals_paper")){
						Logs.i("================================ journal C");
					}
				}
				
			};
				
				MobclickAgent.onEvent(parent.getContext(), eventflag,1);
			if("research_latest_papers".equalsIgnoreCase(eventflag)){
				SpotUtil.spots.get(SpotUtil.LASTEST_SPOT).clear(ActivityPool.getInstance().activityMap.get(ResearchPageActivity.class));
			}
			if(!eventflag.equals(""))
			MobclickAgent.onEvent(parent.getContext(), eventflag,1);
			
			MapAdapter mapAdapter;
			if (parent.getAdapter() instanceof HeaderViewListAdapter) {
				mapAdapter = (MapAdapter) ((HeaderViewListAdapter) parent
						.getAdapter()).getWrappedAdapter();
			} else
				mapAdapter = (MapAdapter) parent.getAdapter();
			
			DetailsData.entities = ((List<EntityObj>) (mapAdapter
					.getItemDataSrc().getContent()));
			
			EntityObj nEntity = ((EntityObj) (parent
					.getItemAtPosition(position)));
			
			Logs.i(" nEntity " + nEntity.get("clc_code"));
			
			DetailsData.tappedne = nEntity;
			
			openResearch(view.getContext(), nEntity,launchKey,this);
			mapAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	String objId;
	public static void openResearch(Context context, EntityObj nEntity,String launchKey,DetailItemClickListener detailItemClickListener) {
		Intent intent = new Intent(context,
				PaperDetailActivity.class);
		if(launchKey==null){
			launchKey = "id";
		}
		Object o = nEntity.get(launchKey);
		o = o != null ? o : nEntity.get("id");
		o = o != null ? o : nEntity.get("Id");
		String objId= o.toString();
		if(detailItemClickListener!=null){
			detailItemClickListener.objId = objId;
		}
		Logs.i("===objId "+objId);
		intent.putExtra(Constant.ID, objId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
//		intent.putExtra("title", "鍚摐鍖荤敤鎶楄弻鍖绘姢鏈嶉娆″紩鍏ユ垜鍥�);
		intent.putExtra("title", nEntity==null||nEntity.get("Title")==null?"":nEntity.get("Title").toString());
		BaseActivity.app.setHomeLaunched(false);
		context.startActivity(intent);
	}
};
