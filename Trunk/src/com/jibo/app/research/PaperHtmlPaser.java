package com.jibo.app.research;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.api.android.GBApp.R;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.PaperDetailEntity;
import com.jibo.dbhelper.ResearchAdapter;

import android.content.Context;
import android.webkit.WebView;

/**
 * html¥¶¿Ì
 * @author will
 * @create 2013-3-21 …œŒÁ10:24:04
 */
public class PaperHtmlPaser {
	private String mimeType = "text/html";
	private String encoding = "utf-8";
	private String htmlStr;
	private String htmlLocalStr;
	private Context context;
	private ResearchAdapter researchAdapter;
	
	private static String PUBMED = "pubmed";
	private static String WANFANG = "wanfang";
	
	private static String authorTemplate = "<div> @$@ </div>";
	private static String journalTemplate01 = "<div class=\" gbiclickableelement\" >@$@ : %@<span id=\"subscribe_button\" class=\" gbiclickableelement unsubscribed\" journal_info=\"%@\"></span></div>";
	private static String journalTemplate02 = "<span>@$@ : %@</span>";
	private static String journalTemplate03 = "<span>@$@ : %@</span>";
	private static String journalTemplate04 = "<span class=\"clazzurl\">DOI ID   : <u><a href=\"http://dx.doi.org/%@\">%@</a></u></span>";
	private static String journalTemplate05 = "<span class=\"clazzurl\">%@ ID : <u><a href=\"http://www.ncbi.nlm.nih.gov/m/pubmed/%@\">%@</a></u></span>";
	private static String journalWFTemplate05 = "<span class=\"clazzurl\">%@ ID : <u><a href=\"http://c.wanfangdata.com.cn/Periodical-%@.aspx\">%@</a></u></span>";
	private static String journalIFTemplate = "<span class=\"sel radius_6\">IF %@</span>";
	// journalTemplate05 = "<span>%@ ID : <a href=\"gbi::myweb/journal/%@\">%@</a></span>";
	public PaperHtmlPaser(Context context){
		this.context = context;
		htmlLocalStr = getFromAssets("webview/mindex.html");
		researchAdapter = new ResearchAdapter(context);
	}
	
	public void loadHtml(final WebView mWebView, PaperDetailEntity entity){
		htmlStr = htmlLocalStr;
        htmlStr = htmlStr.replace("{$papertitle}", entity.title)
        		.replace("{$journalname}", entity.journalName == null ? "":entity.journalName)
        		.replace("{$journalif}", (entity.IF == null || entity.IF.equals("0.0") || entity.IF.equals("")) ? "":loadIF(entity.IF))
        		.replace("{$Abstract_Label}", context.getString(R.string.paper_abstract))
        		.replace("{$datetime}", entity.publicDate == null ||entity.publicDate.trim().equals("")? "":entity.publicDate.substring(0, entity.publicDate.indexOf("T")))
        		.replace("{$abstract}", entity.abstarct == null ? "":entity.abstarct)
        		.replace("{$Readmore_Label}", context.getString(R.string.paper_read_more))
        		.replace("{$Fold_Label}", context.getString(R.string.paper_fold))
        		.replace("{$Keywords_Label}", context.getString(R.string.paper_keywords))
        		.replace("{$keywords_list}", ((entity.keyWords == null || entity.keyWords.size() == 0) && entity.mesh == null ?  context.getString(R.string.paper_detail_empty):"") + (entity.keyWords == null ? "":loadAuthorsList(entity.keyWords)) + (entity.mesh == null ? "":loadMeshList(entity.mesh)))
        		.replace("{$Author_Label}", context.getString(R.string.paper_author))
        		.replace("{$author_list}", entity.authors == null ? context.getString(R.string.paper_detail_empty):loadAuthorsList(entity.authors))
        		.replace("{$affiliations_list}", entity.affiliations == null ? "":loadAuthorsList(entity.affiliations))
        		.replace("{$Domain_Label}", context.getString(R.string.paper_domain))
        		.replace("{$domain_list}", entity.categorys == null ? context.getString(R.string.paper_detail_empty):loadAuthorsList(entity.categorys))
        		.replace("{$Drug_Label}", context.getString(R.string.paper_drug))
        		.replace("{$drug_list}", entity.substances == null ? context.getString(R.string.paper_detail_empty):loadAuthorsList(entity.substances))
        		.replace("{$Journal_Label}", context.getString(R.string.paper_journal))
        		.replace("{$journalInfo}", loadJournalInfo(entity));
        mWebView.loadDataWithBaseURL("file:///android_asset/webview/mindex.html", htmlStr, mimeType, encoding, null);
        if(researchAdapter.isSubscribed(entity.journalID, SharedPreferencesMgr.getUserName())){
        	mWebView.post(new Runnable() {
				
				@Override
				public void run() {
					mWebView.loadUrl("javascript:setSubscribeButtonStatus('true')");
				}
			});
        	
        }
	}
	
	private String loadAuthorsList(ArrayList<String> authors){
		String str = "";
		for(int i=0; i<authors.size(); i++ ){
			str += authorTemplate.replace("@$@", authors.get(i));
		}
		return str;
	}
	
	private String loadIF(String IF){
		float x1 = 0.0f;
		try{
			x1 = Float.parseFloat(IF);
		}catch (Exception e) {
			e.printStackTrace();
		}
		IF = String.format("%.2f", x1);
		String str = "";
		str = journalIFTemplate.replace("%@", IF);
		return str;
	}
	
	private String loadMeshList(ArrayList<String> mesh){
		String str = "";
		if(mesh.size() > 0)
		try{
			for(int i=0; i<mesh.size(); i++ ){
				str += authorTemplate.replace("@$@", mesh.get(i).substring(mesh.get(i).lastIndexOf(":") + 1, mesh.get(i).length() - 2).replaceAll("\"", ""));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	private String loadJournalInfo(PaperDetailEntity entity){
		String str = "";
		str = journalTemplate01.replace("@$@", context.getString(R.string.paper_detail_journal))
				.replaceFirst("%@", entity.journalName == null ? "":entity.journalName)
				.replaceFirst("%@", entity.journalID==null?"":entity.journalID);
		
		String pubtpStr = "";
		if(entity.pubTypes != null){
			for(int i=0; i<entity.pubTypes.size(); i++){
				pubtpStr += entity.pubTypes.get(i) + "\n";
			}
		}
		str += journalTemplate02.replace("@$@", context.getString(R.string.paper_detail_publication_type))
				.replace("%@", pubtpStr);
		
		str += journalTemplate04.replaceAll("%@", entity.DOI == null ? "":entity.DOI);
		
		str += journalTemplate03.replace("@$@", context.getString(R.string.paper_detail_language))
				.replace("%@", entity.language == null ? "":entity.language);
		if(entity.id!=null){
		if(entity.id.substring(0, entity.id.indexOf("_")).equals(PUBMED)){
			str += journalTemplate05.replaceFirst("%@", entity.id.substring(0, entity.id.indexOf("_")))
					.replace("%@", entity.id.substring(entity.id.indexOf("_") + 1));
		}else if(entity.id.substring(0, entity.id.indexOf("_")).equals(WANFANG)){
			str += journalWFTemplate05.replaceFirst("%@", entity.id.substring(0, entity.id.indexOf("_")))
					.replace("%@", entity.id.substring(entity.id.indexOf("_") + 1));
		}
		}
		return str;
	}
	
	private String getFromAssets(String fileName){ 
    	String Result="";
    	try { 
    		InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open(fileName) ); 
    		BufferedReader bufReader = new BufferedReader(inputReader);
    		String line="";
    		while((line = bufReader.readLine()) != null)
    			Result += line;
    		
    	} catch (Exception e) { 
    		e.printStackTrace(); 
    	}
    	return Result;
    } 
}
