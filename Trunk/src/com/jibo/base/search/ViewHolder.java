/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.base.search;


import android.app.Activity;
import android.view.View;
/**
 *
 * @author chenliang
 */
public abstract class ViewHolder {

    protected View view;
    protected Activity activity;

    public View getView() {
        return view;
    }

    public ViewHolder(View view, Activity activity2) {
        this.view = view;
        this.activity = activity2;
    }
    public abstract void refresh();
    public abstract void start();
    public abstract void dataChanged();
    public abstract void rebind();
}
