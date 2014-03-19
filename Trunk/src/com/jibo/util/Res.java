/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;



/**
 * 
 * @author chenliang
 */
public class Res {
	public static Class r;
	public static Map<String, TypeAttrs> TypeMaps = new TreeMap<String, TypeAttrs>(
			ComparatorRepo.stringKey);
	public static final String styleable = "styleable";

	public static void initR(Context context) {
		if (r == null) {
			try {
				r = Class.forName(context.getPackageName() + ".R");
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static Class getR() {
		return r;
	}

	public static Class getSubClass(String childName) {
		Class clz = getR();
		for (Class clazz : clz.getClasses()) {
			if (clazz.getName().endsWith(childName)) {
				return clazz;
			}
		}
		return null;
	}

	public static Object getAttr(String r_type_attr) {
		int commaIdx = r_type_attr.lastIndexOf(".");
		return getAttr(r_type_attr.substring(2, commaIdx),
				r_type_attr.substring(commaIdx + 1));
	}

	public static class TypeAttrs {
		public Class type;
		public Map<String, Object> attrMaps = new TreeMap<String, Object>(
				ComparatorRepo.stringKey);

	}

	public static Object getAttr(String type, String attr) {
		Object componentObj = null;
		TypeAttrs typeAttrs = TypeMaps.get(type);
		if (typeAttrs == null) {
			TypeAttrs varTypeAttrs = new TypeAttrs();
			varTypeAttrs.type = getSubClass(type);
			TypeMaps.put(type, varTypeAttrs);
			typeAttrs = varTypeAttrs;
		} else {
			Logs.i("--- ");
			componentObj = typeAttrs.attrMaps.get(attr);
			if (componentObj != null) {
				return componentObj;
			}
		}
		return generateAttrValue(attr, typeAttrs);
	}

	private static Object generateAttrValue(String attr, 
			TypeAttrs typeAttrs) {
		Field field;
		Object componentObj = null;
		try {
			field = typeAttrs.type.getField(attr);
			componentObj = field.get(typeAttrs.type);
			typeAttrs.attrMaps.put(attr, componentObj);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return componentObj;
	}
}
