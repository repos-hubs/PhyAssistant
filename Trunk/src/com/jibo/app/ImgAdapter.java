package com.jibo.app;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.dynaImage.FileIconHelper;
import com.jibo.base.dynaImage.FileRequest;
import com.jibo.base.src.request.RequestSrc;

public class ImgAdapter extends MapAdapter {
	FileIconHelper fileIconHelper;

	public RequestSrc lisUnit;
	public ImgAdapter(Context context, AdaptInfo adaptInfo,
			RequestSrc lis) {
		super(context, adaptInfo);
		this.lisUnit = lis;
		fileIconHelper = new FileIconHelper(context);
		// TODO Auto-generated constructor stub
	}

	protected View special;
	protected boolean isTop;
	protected View bg;

	@Override
	protected void findAndBindView(View convertView, int pos, Object item,
			String name, Object value) {
		// TODO Auto-generated method stub
		if (name.equalsIgnoreCase("imgUrl")) {
			int theViewId = this.fieldnames.indexOf(name);
			View theView = convertView.findViewById(this.viewsid[theViewId]);
			int visible = -1;
			Object url;
			String strurl = null;
			if (value != null && !value.equals("")
					&& !value.equals("anyType{}")) {
				try {
					strurl = value.toString();
					visible = View.VISIBLE;
					((View) theView.getParent()).setVisibility(visible);
					url = new URL(strurl);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					url = strurl;
				}
				fileIconHelper.setIcon(new FileRequest(url),
						(ImageView) theView);
			} else {
				((View) theView.getParent()).setVisibility(View.GONE);
				return;
			}

		}
		
		super.findAndBindView(convertView, pos, item, name, value);

	}

	

};