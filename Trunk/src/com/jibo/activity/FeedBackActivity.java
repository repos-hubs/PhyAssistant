package com.jibo.activity;



import java.util.Properties;

import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.FeebBackPaser;
import com.jibo.net.BaseResponseHandler;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.api.android.GBApp.R;
public class FeedBackActivity extends BaseActivity implements OnClickListener {
	private EditText etv;
	private Button btnHelp;
	private ImageButton btnClose;
	private BaseResponseHandler baseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback);
		inits();
	}
	
	public void inits() {
		baseHandler = new BaseResponseHandler(this);
	    LinearLayout li=(LinearLayout) findViewById(R.id.li);
        etv=(EditText) findViewById(R.id.tv_text_limit);

        btnHelp=(Button)findViewById(R.id.btn_fbSubmit);
        btnClose=(ImageButton) findViewById(R.id.btnClose); 
        btnHelp.setOnClickListener(this);
        btnClose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnClose: 
			finish();
			break;
		case R.id.btn_fbSubmit: 
			System.out.println("onClick   ###");
            String content=etv.getText().toString().trim();
            Properties propertyInfo =new Properties();
   			propertyInfo.put(SoapRes.KEY_FEED_BACK_USERID, SharedPreferencesMgr.getUserName());
   			propertyInfo.put(SoapRes.KEY_FEED_BACK_CONTENT, content);
   			sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_FEED_BACK,
   				propertyInfo, baseHandler);
     	   break;
		default:
			break;
		}
	}
	
	@Override
	public void onReqResponse(Object o, int methodId) {
		if(o!=null) {
			if(o instanceof FeebBackPaser) {
				FeebBackPaser fbp = (FeebBackPaser)o;
				if("True".equals(fbp.getObj().toString())) {
					//TODO
					Toast toast = Toast.makeText(context, context
							.getString(R.string.submitSuccess), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);

					toast.show();
					this.finish();
				}
			}
		}
		super.onReqResponse(o, methodId);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
				finish();
		}
		return false;
	}
}
