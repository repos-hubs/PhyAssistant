/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.util;

import java.util.Comparator;


/**
 *
 * @author chenliang
 */
public class ComparatorRepo {
        public static Comparator<String> stringKey = new Comparator<String>() {

        public int compare(String arg0, String arg1) {
        	if(arg0==null||arg1 == null) return -1;
            return arg0.compareTo(arg1);
        }
    };
		
}
