package com.jibo.app.research;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;

public class PaperDetailLinkActivity extends BaseSearchActivity{
	private String url = null;
	
	private WebView mWebView = null;
	private View progressView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.research_special_detail);
		super.onCreate(savedInstanceState);
		((TextView) findViewById(R.id.txt_header_title)).setText(R.string.research);
		
		inits();
		
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		url = data.getString("url");
		if(!TextUtils.isEmpty(url)){
			mWebView.loadUrl(url);
		}
	}
	
	private void inits(){
		progressView = findViewById(R.id.progress);
		mWebView = (WebView) findViewById(R.id.webview);
		 //设置可以支持缩放   
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);  
        //设置出现缩放工具   
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        
        mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				progressView.setVisibility(View.VISIBLE);  
    			mWebView.setVisibility(View.GONE);  
				mWebView.loadUrl(url);
				return true;
			}  
        	
        });  
        
        mWebView.setWebChromeClient(new WebChromeClient(){  
        	@Override  
        	public void onProgressChanged(WebView view, int newProgress) {  
        		super.onProgressChanged(view, newProgress);  
        		if (newProgress == 100) {  
        			mWebView.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							mWebView.setVisibility(View.VISIBLE);  
		        			progressView.setVisibility(View.GONE);  
						}
					}, 1000);
        		}  
        	}  
        });
        
        mWebView.setOnKeyListener(new View.OnKeyListener() {    
        	@Override
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if (event.getAction() == KeyEvent.ACTION_DOWN) {    
        			if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
        				mWebView.goBack();   //后退    
        				return true;    //已处理    
        			}    
        		}    
        		return false;    
        	}    
        });  
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
