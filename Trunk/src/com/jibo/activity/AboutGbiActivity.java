package com.jibo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.api.android.GBApp.R;
import com.umeng.fb.UMFeedbackService;
/**
 * 
 * @author simon
 *
 */
public class AboutGbiActivity extends BaseActivity implements OnClickListener {
	private TextView tv, tvLink, tvLinkSource;
	private String str;
	private Button btnHelp;
	private ImageButton btnClose;
	private Button btnInfset;
	private Button btnEvaluate;

	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aboutus);
		LinearLayout li = (LinearLayout) findViewById(R.id.li);
		btnHelp = (Button) findViewById(R.id.helpBtn);
		btnEvaluate = (Button) findViewById(R.id.btn_evaluate);
		btnInfset = (Button) findViewById(R.id.btn_infset);
		btnClose = (ImageButton) findViewById(R.id.btnClose);
		tv = (TextView) findViewById(R.id.tv_text_limit);
		tv.setText(R.string.aboutuscontent1);
		tvLink = (TextView) findViewById(R.id.link);
		tvLink.setText(R.string.aboutuscontent2);
		tvLinkSource = (TextView) findViewById(R.id.linksource);
		tvLinkSource.setText(Html.fromHtml("<a href=\"" + "http://www.jibo.cn/"
				+ "\">" + "http://www.jibo.cn/" + "</a>"));
		tvLinkSource.setMovementMethod(LinkMovementMethod.getInstance());
		btnHelp.setOnClickListener(this);
		btnEvaluate.setOnClickListener(this);
		btnInfset.setOnClickListener(this);
		btnClose.setOnClickListener(this);

	}

	//

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewId = v.getId();
		switch (viewId) {
		case R.id.btnClose: {
			finish();
			break;
		}
		case R.id.helpBtn:
			Intent intent = new Intent(this, HelpActivity.class);// 
			startActivity(intent);
			break;
		case R.id.btn_evaluate:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=com.api.android.GBApp")));

			// evaluate();
			break;
		case R.id.btn_infset:
			UMFeedbackService.openUmengFeedbackSDK(context);
			break;
		default:
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
		}
		return false;
	}

	/**
	 * ÆÀ¼Û
	 */
	private void evaluate() {

		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.chooseMarket))
				.setItems(R.array.markets,
						new DialogInterface.OnClickListener() {
							// content
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String url = "";
								switch (which) {
								case 0:
									url = "https://play.google.com/store/apps/details?id=com.api.android.GBApp";
									break;
								case 1:
									url = "http://www.appchina.com/soft_detail_289428_0_10.html";
									break;
								case 2:
									url = "http://apk.gfan.com/Product/App168533.html";
									break;
								case 3:
									url = "http://www.anzhi.com/soft_181414.html";
									break;
								case 4:
									url = "http://mobile.91.com/Soft/Android/com.api.android.GBApp-1.10.html";
									break;
								}

								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse(url)));
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}


}
