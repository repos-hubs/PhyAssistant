//package com.jibo.adapter;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.api.android.GBApp.R;
//import com.jibo.GBApplication;
//import com.jibo.activity.ModifyDepartmentActivity;
//import com.jibo.common.Util;
//import com.jibo.data.entity.DepartmentEntity;
//
//public class DptGVAdapter extends BaseAdapter {
//	private Context context;
//	private ArrayList<DepartmentEntity> lst;
//	private GBApplication app;
//	private String dpt;
//	public DptGVAdapter(Context context, ArrayList<DepartmentEntity> lst, String dpt) {
//		this.context = context;
//		this.lst = lst;
//		this.dpt = dpt;
//		app = (GBApplication) ((ModifyDepartmentActivity) context).getApplication();
//	}
//    public View getView(int position, View convertView, ViewGroup parent) {
//        TextView txt = new TextView(context);
//        String department = lst.get(position).getDepartment();
//        if(dpt.equals(department)) {
//        	txt.setTextColor(Color.RED);
//        } else {
//        }
//        
//        txt.setGravity(Gravity.CENTER);
//        txt.setSingleLine();
//        txt.setEllipsize(TextUtils.TruncateAt.END);
//        txt.setId(lst.get(position).getId());
//        txt.setTextSize(app.getDeviceInfo().getScale()*15);
//        txt.setText(department);
//        return txt;
//    }
//
//    public final int getCount() {
//        return lst.size();
//    }
//
//    public final Object getItem(int position) {
//        return lst.get(position);
//    }
//
//    public final long getItemId(int position) {
//        return position;
//    }
//}
