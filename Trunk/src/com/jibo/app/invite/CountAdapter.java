/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.app.invite;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter;

import android.content.Context;


/**
 *
 * @author chenliang
 */
public class CountAdapter extends MapAdapter {

    public CountAdapter(Context context, AdaptInfo listViewHolder) {
        super(context, listViewHolder);
    }
    public Map<Integer, Object> items = new TreeMap<Integer, Object>(new Comparator<Integer>() {

        public int compare(Integer arg0, Integer arg1) {
            return arg0 - arg1;
        }
    });

    public Object getItem(int position) {
        if (!items.containsKey(position)) {
            items.put(position, super.getItem(position));
        }
        Object obj = items.get(position);
        if (obj == null) {
            return null;
        }
        return obj;
    }
    int apkVersionMaxWidth;


    public void setItemDataSrc(AdapterSrc itemDataSrc) {
        items.clear();
        super.setItemDataSrc(itemDataSrc);
    }
  
}
