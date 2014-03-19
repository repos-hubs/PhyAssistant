/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.base.search;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.View;


import com.api.android.GBApp.R;
import com.jibo.base.highlight.KeyMatcher;
import com.jibo.base.highlight.TextHighLightDecorator;

/**
 * 
 * @author chenliang
 */
public class SearchAdapter extends CountAdapter {

	KeyMatcher matcher = new KeyMatcher("");
	TextHighLightDecorator decoratorHighLight;
	List<String> fieldstoimpose;
	int color;

	public SearchAdapter(Context context, AdaptInfo listViewHolder) {
		super(context, listViewHolder);
	}

	public void setHighlightInfo(String matcherText, String[] fieldstoimpose,
			int color) {
		try {
			decoratorHighLight = new TextHighLightDecorator(color);
			switchKeyWords(matcherText);
			this.fieldstoimpose = Arrays.<String> asList(fieldstoimpose);
			this.color = color;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void switchKeyWords(String matcherText) {
		this.matcher = new KeyMatcher(matcherText);
		decoratorHighLight.setMatcher(matcher);

	}

	@Override
	protected void findAndBindView(View convertView, int pos, Object item,
			String name, Object value) {
		if (decoratorHighLight != null && fieldstoimpose != null) {
			if (fieldstoimpose.contains(name)) {
				value = decoratorHighLight.getDecorated(value.toString());
			}
		}
		super.findAndBindView(convertView, pos, item, name, value);
		showLogoOrNot(convertView, name, value, "email", R.id.imgemail);
		showLogoOrNot(convertView, name, value, "phone", R.id.imgphone);
	}

	private void showLogoOrNot(View convertView, String name, Object value,
			String matched, int imgId) {
		if (name.equals(matched)) {
			convertView
					.findViewById(imgId)
					.setVisibility(
							value == null || value.toString().equals("") ? View.INVISIBLE
									: View.VISIBLE);
		}
	}
}
