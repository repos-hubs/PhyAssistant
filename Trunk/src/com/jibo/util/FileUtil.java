package com.jibo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

public class FileUtil {
	public static long copy(InputStream f1,File f2) throws Exception{
		  long time=new Date().getTime();
		  int length=2097152;
		  InputStream in=f1;
		  FileOutputStream out=new FileOutputStream(f2);
		  byte[] buffer=new byte[length];
		  while(true){
		   int ins=in.read(buffer);
		   if(ins==-1){
		    in.close();
		    out.flush();
		    out.close();
		    return new Date().getTime()-time;
		   }else
		    out.write(buffer,0,ins);
		  }
		 }
}
