package com.jibo.activity;

import com.api.android.GBApp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WebViewActivity extends BaseSearchActivity implements OnClickListener{
//	private ImageButton mHome;
	private WebView webView;
	private LinearLayout layout;
	private String URL = "http://www.ncbi.nlm.nih.gov/m/pubmed";
	private String key ;
	private TextView txtHeader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webviewactivity);
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		txtHeader = (TextView) findViewById(R.id.txt_header_title);
		txtHeader.setText(R.string.research);
		if(intent != null){
			Bundle bundle = intent.getExtras();
			if(bundle != null)
				key = bundle.getString("searchkey");
			if(key == null)
				key = "";
		}
		webView = (WebView)findViewById(R.id.webview);
		layout  = (LinearLayout)findViewById(R.id.dialogprogress);
		Display display = getWindowManager().getDefaultDisplay();
		
		if(!isWifi()&& !isMobile()){
			webView.setVisibility(View.GONE);
			return;
		}else{
			webView.setVisibility(View.VISIBLE);
		}
		if(!"".equals(key))
			URL = URL + "/?term=" + key;
		webView.loadUrl(URL);
		webView.setWebViewClient(new PublishWebViewClient());
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if(newProgress == 100){
					layout.setVisibility(View.GONE);
					view.setVisibility(View.VISIBLE);
					view.requestFocus();
				}else{
					view.setVisibility(View.GONE);
					layout.setVisibility(View.VISIBLE);
				}
			}
			
		});
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
			 webView.goBack();
			 return true;
		 }
				 
		return super.onKeyDown(keyCode, event);
	}



	
	private class PublishWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}
	}
	
	private boolean isWifi(){
		ConnectivityManager cm 	= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni 			= cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiAvail 	= ni.isAvailable();
		boolean isWifiConnect 	= ni.isConnected();
		return isWifiAvail && isWifiConnect;
	}
	
	private boolean isMobile(){
		ConnectivityManager cm 	= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni			= cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileAvail 	= ni.isAvailable();
		boolean isMobileConnect = ni.isConnected();
		return isMobileAvail && isMobileConnect;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	

	

	

}
