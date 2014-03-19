package com.jibo.base.src;

import java.lang.reflect.Field;

import com.jibo.data.entity.NewsEntity;

public class EntityUtil {
	public static EntityObj convert(Object o){
		EntityObj e = null;
		boolean created = false;
//		if(o instanceof NewsEntity){			
//			e = new NewsObj();
//			
//			created = true;
//		}
		
		if(!created)e = new EntityObj();
		for(Field f:o.getClass().getFields()){
			f.setAccessible(true);
			try {
				if(f.getName().equalsIgnoreCase("creator")){
					continue;
				}
				e.fieldContents.put(f.getName(), f.get(o));
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			f.setAccessible(true);
		}
		return e;
		
	}
}
