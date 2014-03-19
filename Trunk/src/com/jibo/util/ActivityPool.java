/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;

/**
 *
 * @author Administrator
 */
public class ActivityPool {

    public Set<Activity> activityList = new HashSet<Activity>();
    public Map<Class,Activity> activityMap = new HashMap<Class,Activity>(); 
    private static ActivityPool instance;

    protected ActivityPool() {
    }

    public static ActivityPool getInstance() {
        if (null == instance) {
            instance = new ActivityPool();
        }
        return instance;
    }
    //添加Activity到容器中

    public void addActivity(Activity activity) {
    	activityMap.put(activity.getClass(), activity);
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    public void invokeMethod(String methodName, Activity exclude, Class[] classes, Object[] values) {
        for (Activity activity : activityList) {
            Logs.i("--------- activity " + activity);
            if (activity != exclude) {
                try {
                    activity.getClass().getMethod(methodName, classes).invoke(activity, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
