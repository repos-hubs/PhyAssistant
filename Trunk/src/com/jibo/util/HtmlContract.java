package com.jibo.util;

import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.HistoryFavoriteActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.base.src.EntityObj;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.NewsDetail;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;

public class HtmlContract {
	private static final String mimeType = "text/html";
	private static final String encoding = "utf-8";
	private static String webFootContent;
	private static Object webHeadContent;
	private static HistoryAdapter historyAdapter = new HistoryAdapter(
			GBApplication.gbapp, 1, "mysqllite.db");
	private static FavoritDataAdapter fData;
	public static View titleview;

	public static void updateWebContent(WebView web, EntityObj en, View view,
			View rootView) {
		try{
		if (fData == null) {
			fData = new FavoritDataAdapter(web.getContext());
			fData.selectNews(SharedPreferencesMgr.getUserName());
		}
		try {
			if (HistoryFavoriteActivity.arrFavorNewsId.contains(en.get("id"))) {
				rootView.findViewById(R.id.tgbtn_2nd).setBackgroundResource(
						R.drawable.btn_favorite_select);
			} else {
				rootView.findViewById(R.id.tgbtn_2nd).setBackgroundResource(
						R.drawable.btn_favorite_normal);
			}

		} catch (Exception e) {
			Log.e("EntityObj", "EntityObj中没有key为id的value");
			e.printStackTrace();
		}
		NewsDetail nd = (NewsDetail) en.getObject("newsDetail");
		if (nd == null) {
			return;
		}
		String text = nd.content;
		String date = nd.date;
		String title = nd.title;
		if (text == null)
			return;
		NewsDetailActivity.storeHistory(en.get("id"), title,
				GBApplication.gbapp, historyAdapter);

		String msg = en.get("stickMsg");
		if (msg != null && !en.get("stickMsg").equals("")
				&& !msg.contains("any")) {
			((TextView) (view.findViewById(R.id.src))).setText(msg);
		} else {
			((TextView) (view.findViewById(R.id.src))).setVisibility(View.GONE);
		}

		titleview = view;
		((TextView) (view.findViewById(R.id.title))).setText(title);
		((TextView) (view.findViewById(R.id.date))).setText(date);
		if (web.getChildCount() > 0) {
			if (!(web.getChildAt(0) instanceof RelativeLayout)) {
				web.addView(view, 0);
			}
		} else {
			web.addView(view, 0);
		}
		// 改为每次重新load内容
		Logs.i("= source :"+nd.source+" news_source :"+nd.newsSource);
		String source = (nd.source==null||nd.source.equals(""))  ? ""
				: "<br><br><div id=\"source\" style=\"margin-bottom: 30pt;\">"
						+ web.getContext().getString(R.string.link_to_source)
						+ ": &nbsp <a href=\"" + nd.source
						+ "\" onclick=\"stopBubble(event)\">" + (nd.newsSource.equals("")?nd.source:nd.newsSource)
						+ "</a></div>";

		Log.i("simon", "text>>>" + text);
		web.loadDataWithBaseURL(null, getWebHeadContent() + "" + text + source
				+ getWebFootContent(), mimeType, encoding, null);
		web.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return true;
			}

		});
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String getWebHeadContent() {
		if (null == webHeadContent) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			buffer.append("<head>");

			buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");

			buffer.append("<style type=\"text/css\">.borderImage { -webkit-tap-highlight-color:rgba(0,0,0,0);}" 
//			+
//					"</style>"
//					);
//			buffer.append(
					+
//					"<style type=\"text/css\">" +
					"#newscontentcontainer div,p{text-indent:2em}" +
//					"body {font-family:'Helvetica';font-size:14px;word-wrap:break-word;line-height:1.5;font-weight:bold;margin:0;}b{font-size:15px;}html,body{overflow:hidden;}" +
					"</style>");
			buffer.append("<script type=\"text/javascript\"> ");
			// 重排字体
			buffer.append("function updateTextSize(){ ");
			buffer.append(" document.getElementById(\"content\").style.webkitTextSizeAdjust = '10%'; ");
			buffer.append("}");
			// 更新WebView内容function
			buffer.append("function updateContent(content){ ");
			buffer.append("  document.getElementById(\"content\").innerHTML = content; ");
			buffer.append("}");
			//
			buffer.append("function stopBubble(e){ ");
			buffer.append("  e = window.event || e;");
			buffer.append("  if (e.stopPropagation) {");
			buffer.append("  e.stopPropagation();");
			buffer.append("  } else {e.cancelBubble = true;}");
			buffer.append("  }");
			// 页面加载完成后,默认滚动到顶部
			buffer.append("function init(){ ");
			buffer.append("  window.scrollBy(0, 0); ");
			buffer.append("}");
			buffer.append("</script>");
			buffer.append("<title>无标题文档</title>");
			buffer.append("</head>");
			buffer.append("<body onload=\"init();\" onclick=\"window.jscall.scaleWindow()\" >");
			buffer.append("<div style=\"margin-top: 70pt;outline:none;\" id=\"content\">");
			webHeadContent = buffer.toString();
		}
		return (String) webHeadContent;
	}

	public static String getWebFootContent() {
		if (null == webFootContent) {
			webFootContent = "</div></body></html>";
		}
		return webFootContent;
	}
}
