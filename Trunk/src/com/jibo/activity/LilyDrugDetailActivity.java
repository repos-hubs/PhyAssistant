package com.jibo.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.common.Constant;

public class LilyDrugDetailActivity extends BaseSearchActivity implements
		OnClickListener {

	private TextView txtHeader;
	private WebView webView;
	String bid,productId;
//	private String URL = "file:///android_asset/html/1_2009939.htm";  
//	private String URL = "file:///android_asset/";  
	private String URL = "file://"+Constant.DATA_PATH_Mufacture_doc+"/";
//	String baseUrl = "file:///android_asset/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lilydrugdetailactivity);
		super.onCreate(savedInstanceState);
		this.getIntent();
		txtHeader = (TextView) findViewById(R.id.txt_header_title);
		txtHeader.setText(R.string.drugdtl);
		webView = (WebView) findViewById(R.id.webview);
		getWindowManager().getDefaultDisplay();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			bid = extras.getString("brandId");
			productId = extras.getString("productId");
		}
//		webView.loadDataWithBaseURL(baseUrl,null , "text/html", "utf-8", null);
		
		webView.loadUrl(URL+bid+"_"+productId+".htm");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
