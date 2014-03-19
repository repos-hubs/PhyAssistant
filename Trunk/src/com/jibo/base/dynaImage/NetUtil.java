package com.jibo.base.dynaImage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {
	public static byte[] getImage(URL url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
		return StreamTool.readInputStream(inStream);// 得到图片的二进制数据
	}

	public static String splitPath(URL url) throws Exception {
		return url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
	}
}
