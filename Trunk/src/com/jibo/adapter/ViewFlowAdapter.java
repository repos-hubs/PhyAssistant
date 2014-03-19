//package com.jibo.adapter;
//
//
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//
//import com.api.android.GBApp.R;
//import com.jibo.data.entity.DepartmentEntity;
//import com.jibo.ui.TitleProvider;
//
//public class ViewFlowAdapter extends BaseAdapter implements TitleProvider {
//
//	private ArrayList<DepartmentEntity> dptList;
//
//    private LayoutInflater mInflater;
//    private Context context;
//	private GridView gvDptLst;
//	private ArrayList<String> titleList;
//	private ArrayList<DepartmentEntity> selectedDptList;
//	private String dpt;
//    public ViewFlowAdapter(Context context, ArrayList<String> titleList, ArrayList<DepartmentEntity> dptList, String dpt) {
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.dptList = dptList;
//        this.titleList = titleList;
//        this.context = context;
//        this.dpt = dpt;
//    }
//
//    
//    
//    public String getDpt() {
//		return dpt;
//	}
//
//
//
//	public void setDpt(String dpt) {
//		this.dpt = dpt;
//	}
//
//
//
//	@Override
//    public int getItemViewType(int position) {
//        return position;
//    }
//
//    @Override
//    public int getCount() {
//        return titleList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//        	convertView = (LinearLayout) mInflater.inflate(R.layout.gv_dpt, null);
//        }
//        gvDptLst = (GridView) convertView.findViewById(R.id.gv_dptLst);
//		String selectedTitle = getTitle(position);
//		selectedDptList = new ArrayList<DepartmentEntity>();
//		for(DepartmentEntity dpt:dptList) {
//			if(selectedTitle.equals(dpt.getBigDepartment())) {
//				selectedDptList.add(dpt);
//			}
//		}
//		DptGVAdapter gbAdapter = new DptGVAdapter(context, selectedDptList, dpt);
//		gvDptLst.setAdapter(gbAdapter);
////		if(context instanceof ModifyDepartmentActivity) {
////			((ModifyDepartmentActivity) context).setListener(gvDptLst);
////		}
//        return convertView;
//    }
//    
//	public String getTitle(int position) {
//		return titleList.get(position);
//	}
//
//	
//}
