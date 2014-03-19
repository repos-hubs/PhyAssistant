package com.jibo.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.methods.PostMethod;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 图片管理类
 * 
 * @author simon
 * 
 */
public class BitmapManager {

	private static Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	/**
	 * 加载本地缓存图片
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
	public static Bitmap loadBitmap(String url) {
		if (url == null)
			return null;
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap != null) {
			return bitmap;
		} else {
			Bitmap map = getLocalBitmap(url);
			if (null != map) {
				cache.put(url, new SoftReference<Bitmap>(map));
				return map;
			}
		}
		return null;
	}

	/**
	 * 获取程序内存Cache的缓存图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url).get();
		}
		return null;
	}

	public static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int bytes = read();
					if (bytes < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * 下载网络图片
	 * 
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap downloadBitmap(String url, int width, int height) {
		try {
			Log.i("test", "下载图片");
			Log.i("test", "图片url" + url);
			long start = System.currentTimeMillis();
			InputStream input = (InputStream) new URL(url).getContent();
			if (input == null)
				Log.i("test", "下载图片失败");
			else
				Log.i("test", "图片不为空");
			Log.i("test", "下载时间为:" + (System.currentTimeMillis() - start));
			Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(
					input));
			// bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			if (bitmap != null)
				Log.i("test", "下载图片成功");
			cache.put(url, new SoftReference<Bitmap>(bitmap));
			saveBmpToSd(bitmap, URLEncoder.encode(url));
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 计算sdcard上的剩余空间
	 * 
	 * @return
	 */
	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;

		return (int) sdFreeMB;
	}

	private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static int MB = 1024 * 1024;
	public static String DIR = "/data"
			+ Environment.getDataDirectory().getAbsolutePath()
			+ "/com.api.android.GBApp/image";

	/**
	 * 判断本地是否有该url对应图片
	 * 
	 * @param url
	 * @return
	 */
	private static boolean Exist(String url) {
		File file = new File(DIR + "/" + url);
		return file.exists();
	}

	public static boolean deleteImage(String url) {
		File file = new File(DIR + "/" + url);
		if (file.exists())
			return file.delete();
		return false;
	}

	/**
	 * 获取 本地缓存图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLocalBitmap(String url) {
		Bitmap map = null;
		String localUrl = URLEncoder.encode(url);
		if (Exist(localUrl))
			map = BitmapFactory.decodeFile(DIR + "/" + localUrl);
		return map;
	}

	/**
	 * 获取 本地缓存文件输入流
	 * 
	 * @param url
	 * @return
	 */
	public static InputStream getLocalInputStream(String url) {
		Bitmap map = null;
		String localUrl = URLEncoder.encode(url);
		if (Exist(localUrl)) {
			try {
				return new FileInputStream(localUrl);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 保存图片到本地缓存
	 * 
	 * @param bm
	 * @param url
	 */
	public static void saveBmpToSd(Bitmap bm, String url) {

		if (bm == null) {
			Log.w("LOG", " trying to savenull bitmap");
			return;
		}
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			Log.w("LOG", "Low free space onsd, do not cache");
			return;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
			return;
		String filename = url;
		// 目录不存在就创建
		File dirPath = new File(DIR);
		if (!dirPath.exists()) {
			dirPath.mkdir();
		}

		File file = new File(DIR + "/" + filename);
		try {
			if (file.exists())
				file.delete();
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG,100, outStream);
			outStream.flush();
			outStream.close();
			Log.i("test", "保存图片成功");
		} catch (FileNotFoundException e) {
			Log.i("test", "FileNotFoundException");
		} catch (IOException e) {
			Log.i("test", "IOException");
		}
	}

	public static String translateEnToZh(String text) {

		HttpPost request = new HttpPost(
				"http://openapi.baidu.com/public/2.0/bmt/translate");

		// HttpPost request = new HttpPost(
		// "http://ajax.googleapis.com/ajax/services/language/translate");

		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("js", "y"));
		// params.add(new BasicNameValuePair("hl", "en"));
		// params.add(new BasicNameValuePair("prev", "_t"));
		// params.add(new BasicNameValuePair("sl", "auto"));
		// params.add(new BasicNameValuePair("ie", "UTF-8"));
		// params.add(new BasicNameValuePair("tl", "zh"));
		// params.add(new BasicNameValuePair("text", "ok"));
		// params.add(new BasicNameValuePair("file", null));
		// params.add(new BasicNameValuePair("history_state()", text));

		// String uriAPI =
		// "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q=ok&langpair=en%7Ces";

		// String uriAPI =
		// "http://fanyi.youdao.com/openapi.do?keyfrom=tengxun&key=1427436313&type=data&doctype=json&version=1.1&q=";
		//

		String s = "<p><b>Dementia Associated with Parkinson's Disease.</b> <b><i>Oral&mdash;</i></b> Initially, 1.5 mg twice daily.<a href='#r1'><font size='-1'><sup>1</sup></font></a> </p><ul><li>If well tolerated, increase dosage after &#8805;4 weeks to 3 mg twice daily;<a href='#r1'><font size='-1'><sup>1</sup></font></a> <a href='#r15'><font size='-1'><sup>15</sup></font></a>  attempt to increase dosage to 4.5 mg twice daily and 6 mg twice daily after &#8805;4 weeks of treatment at the previous dosage.<a href='#r1'><font size='-1'><sup>1</sup></font></a> <a href='#r15'><font size='-1'><sup>15</sup></font></a> </li></ul><ul><li>If adverse effects intolerable, discontinue for several doses and then resume at the same or the immediately preceding (lower) dosage in the titration regimen.<a href='#r1'><font size='-1'><sup>1</sup></font></a>  However, if therapy is interrupted for more than several days, restart drug using the recommended initial dosage (i.e., 1.5 mg twice daily) and titration schedule until the previous maintenance dosage is reached (to decrease the risk of severe vomiting and related sequelae [e.g., spontaneous esophageal rupture]).<a href='#r1'><font size='-1'><sup>1</sup></font></a>  <a href='#warnings'>(See GI Effects under Cautions.)</a></li></ul><b><i>Transdermal&mdash;</i></b> Initiate with one system delivering 4.6 mg/24 hours once daily.<a href='#r17'><font size='-1'><sup>17</sup></font></a> </p><ul><li>If well tolerated, increase dosage after &#8805;4 weeks to one system delivering 9.5 mg/24 hours once daily.<a href='#r17'><font size='-1'><sup>17</sup></font></a> </li></ul><ul><li>If adverse effects intolerable, discontinue for several doses and then resume at the same dosage or the immediately preceding (lower) dosage in the titration regimen.<a href='#r17'><font size='-1'><sup>17</sup></font></a>  However, if therapy is interrupted for more than several days, restart the drug using the recommended initial dosage (i.e., one system delivering 4.6 mg/24 hours once daily) and titration schedule (to decrease the risk of severe vomiting and related sequelae [e.g., spontaneous esophageal rupture]).<a href='#r17'><font size='-1'><sup>17</sup></font></a>  <a href='#warnings'>(See GI Effects under Cautions.)</a></li></ul>";
		s = s.replaceAll("\u1FBC", "&");
		s = s.replaceAll("\u1F7C", "\"");
		s = s.replaceAll("\u1F7D", "#");
		s = s.replaceAll("\u1F7E", "%");
		s = s.replaceAll("\u1F7F", "+");
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		s = s.replaceAll("'", "&#39;");
		s = s.replaceAll("\0", " ");

		// HttpGet request = new HttpGet(uriAPI+s);
		// params.add(new BasicNameValuePair("q", text));
		// params.add(new BasicNameValuePair("v", "1.0"));
		// params.add(new BasicNameValuePair("langpair", "en%7C"));

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("from", "en"));
		params.add(new BasicNameValuePair("to", "zh"));
		params.add(new BasicNameValuePair("client_id",
				"rBnKPxFpVleUqICg20wc6v2N"));
		params.add(new BasicNameValuePair("q", s));

		String data = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);

			response = httpclient.execute(request);
			if (response != null
					&& response.getStatusLine().getStatusCode() == 200) {
				data = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Pattern p =
		// Pattern.compile("<div\\s*id = result_box\\s*[^>]*>(.*?)</div>");
		// Matcher m = p.matcher(data);
		// String b = m.group();
		String dd = null;
		try {
			dd = URLDecoder.decode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dd;
	}

}