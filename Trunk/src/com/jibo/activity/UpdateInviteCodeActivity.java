package com.jibo.activity;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.app.invite.Data;
import com.jibo.app.invite.SearchActivity;
import com.jibo.common.BitmapManager;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.UpdateInviteCodePaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;

public class UpdateInviteCodeActivity extends BaseActivity implements
		OnClickListener {
	private Timer timer;
	private TimerTask timerTask;
	private final int version_bg_id = 0x121;
	private final int version_img = 0x123;
	private int version_alpha = 255;
	private int version_flag = 1;

	private TextView txtHeaderTitle;
	private TextView txtTitle;
	private Button btnAction;
	private TextView txtInvite;
	public EditText edtInviteCode;
	private boolean isPremium = false;
	private LoginSQLAdapter dbHelper;
	private GBApplication app;
	private ImageView imgBG;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case version_img:
				int alpha = (Integer) msg.obj;
				imgBG.setAlpha(alpha);
				imgBG.invalidate();
				break;
			}
		};
	};
	public static UpdateInviteCodeActivity updateInviteCodeActivity;

	ProgressDialog pd;
	boolean loadPimFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_invitecode);
		super.onCreate(savedInstanceState);
		dbHelper = new LoginSQLAdapter(this);
		updateInviteCodeActivity = this;
		inits();
		this.findViewById(R.id.toinvite).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						pd = new ProgressDialog(context);
						pd.setMessage(context.getString(R.string.loadingPeople));
						pd.show();
						pd.setOnCancelListener(new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								// TODO Auto-generated method stub
								if (dialog != null) {
									dialog.dismiss();
									dialog.cancel();
									dialog = null;
								}
								loadPimFlag = false;
							}

						});
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Data.info = SearchActivity.getSourceContacts(
										"", context);
								Logs.i("--- 111");
								loadPimHler.sendEmptyMessage(0);
							}

						}).start();
					}

				});

	}

	Handler loadPimHler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			context.startActivity(new Intent(context, SearchActivity.class));
			pd.dismiss();
			pd.cancel();
			pd = null;
		}

	};

	public void inits() {
		imgBG = (ImageView) findViewById(R.id.imgbg);
		txtHeaderTitle = (TextView) findViewById(R.id.txt_header_title);
		// txtTitle = (TextView) findViewById(R.id.txt_current_title);
		txtInvite = (TextView) findViewById(R.id.toinvite);
		btnAction = (Button) findViewById(R.id.btn_action);
		btnAction.setOnClickListener(this);
		edtInviteCode = (EditText) findViewById(R.id.edt_inviteCode);
		app = (GBApplication) getApplication();
		txtInvite.setOnClickListener(this);
		// txtHeaderTitle.setText(getString(R.string.inviteCodeTitle));
		this.findViewById(R.id.whatsInJIBOPro).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getApplicationContext(),
								VersionIntroductionActivity.class);
						intent.putExtra("isPremium", isPremium);
						startActivity(intent);
					}

				});

	}

	@Override
	public void clickPositiveButton(int dialogId) {
		removeDialog(dialogId);
		super.clickPositiveButton(dialogId);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_action:
			String str = edtInviteCode.getText().toString();
			if ("".equals(str)) {
				showDialog(DialogRes.DIALOG_ID_INVITECODE_BLANK);
			} else {
				updateInviteCode();
			}

			break;
		}
	}

	public void getInvited() {
		changeView((String) SharedPreferenceUtil.getValue(
				getApplicationContext(), "GBAPP",
				SharedPreferencesMgr.KEY_InviteCode, String.class));
	}

	private void changeView(String code) {
		// TODO Auto-generated method stub
		if (code != null && !code.equals("")) {
			// UpdateInviteCodeActivity.this.findViewById(R.id.scroll)
			// .setVisibility(View.GONE);
		}
	}

	private void downLoadImage(String gbUserName, String company,
			final String imagePath) {
		if (null != imagePath && !"".equals(imagePath)) {// 下载广告图片
			dbHelper.insertAdvertising(new AdvertisingEntity(gbUserName,
					company, imagePath));
			new Thread(new Runnable() {
				@Override
				public void run() {
					BitmapManager.downloadBitmap(imagePath, 0, 0);
				}
			}).start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeVersion();
	}

	private void changeVersion() {
		if (!"".equals(SharedPreferencesMgr.getInviteCode())) {
			isPremium = true;
		}
		if (isPremium) {
			// txtTitle.setText(getString(R.string.premium_version));
			edtInviteCode.setText(SharedPreferencesMgr.getInviteCode());
			edtInviteCode.setEnabled(false);
			btnAction.setEnabled(false);
			btnAction.setVisibility(View.INVISIBLE);
			View std_word_view = this.findViewById(R.id.std_word);
			this.findViewById(R.id.text1).setVisibility(View.VISIBLE);
			((TextView) this.findViewById(R.id.text1))
					.setText(R.string.invite_u_r_using_std);
			this.findViewById(R.id.text2).setVisibility(View.GONE);
			this.findViewById(R.id.invite_10_text).setVisibility(View.GONE);
			this.findViewById(R.id.label_invite_10).setVisibility(View.VISIBLE);
			this.findViewById(R.id.professional_word).setLayoutParams(
					new LayoutParams(std_word_view.getLayoutParams().width,
							std_word_view.getLayoutParams().height));
			this.findViewById(R.id.professional_word).setVisibility(
					View.VISIBLE);
			std_word_view.setVisibility(View.GONE);

			String date = SharedPreferencesMgr.getInviteCodeExpiredDate();
			((TextView) this.findViewById(R.id.pro_word_cnt))
					.setText(getString(R.string.duedate)
							+ " "
							+ (date.length() >= 10 ? date.substring(0, 10) : ""));
			((TextView) this.findViewById(R.id.label_invite_10))
					.setText(R.string.continue_invite);
			// this.findViewById(R.id.imgbg).setBackgroundDrawable(
			// this.getResources().getDrawable(
			// R.drawable.version_premium_bg));
			imgBG.setImageResource(R.drawable.version_premium_bg);
			this.findViewById(R.id.versionImage)
					.setBackgroundDrawable(
							this.getResources().getDrawable(
									R.drawable.version_premium));
		} else {
			this.findViewById(R.id.text1).setVisibility(View.VISIBLE);
			((TextView) this.findViewById(R.id.text1))
					.setText(R.string.invite_u_r_using_std1);
			this.findViewById(R.id.text2).setVisibility(View.VISIBLE);
			this.findViewById(R.id.invite_10_text).setVisibility(View.VISIBLE);
			this.findViewById(R.id.label_invite_10).setVisibility(View.GONE);
			this.findViewById(R.id.professional_word).setVisibility(View.GONE);
			this.findViewById(R.id.std_word).setVisibility(View.VISIBLE);
			// txtTitle.setText(getString(R.string.standard_version));
			// this.findViewById(R.id.imgbg).setBackgroundDrawable(
			// this.getResources().getDrawable(
			// R.drawable.version_standard_bg));
			imgBG.setImageResource(R.drawable.version_standard_bg);
			this.findViewById(R.id.versionImage).setBackgroundDrawable(
					this.getResources()
							.getDrawable(R.drawable.version_standard));

		}

		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public synchronized void run() {
				if (version_flag == 1) {
					version_alpha = version_alpha - 51;
					Message msg = handler.obtainMessage(version_img,
							version_alpha);
					handler.sendMessage(msg);
					if (version_alpha == 0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						version_flag = 0;
					}
				} else {
					version_alpha = version_alpha + 51;
					Message msg = handler.obtainMessage(version_img,
							version_alpha);
					handler.sendMessage(msg);
					if (version_alpha == 255) {
						version_flag = 1;
					}
				}
			}
		};

		timer.schedule(timerTask, 1000, 100);
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof UpdateInviteCodePaser) {
				UpdateInviteCodePaser codePaser = (UpdateInviteCodePaser) o;
				String rescode = codePaser.getRescode();
				String error = codePaser.getError();
				String url = codePaser.getUrl();
				if ("200".equals(rescode)) {
					Toast.makeText(this,
							getString(R.string.update_version_success),
							Toast.LENGTH_LONG).show();
					downLoadImage(SharedPreferencesMgr.getUserName(), "", url);// 下载广告图片
					SharedPreferencesMgr.setInviteCode(edtInviteCode.getText()
							.toString());
					SharedPreferencesMgr.setInviteCodeExpiredDate(codePaser
							.getExpiredDate());
					finish();
				} else if ("601".equals(rescode)) {
					showDialog(DialogRes.DIALOG_ID_INVITECODE_FAIL);
				}
			}
		}
		super.onReqResponse(o, methodId);
	}

	public void updateInviteCode() {
		String str = edtInviteCode.getText().toString();
		String userName = SharedPreferencesMgr.getUserName();

		Properties pro = new Properties();
		pro.put(SoapRes.KEY_CODE, str);
		pro.put(SoapRes.KEY_USERNAME, userName);
		sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_UPDATE_INVITE_CODE,
				pro, new BaseResponseHandler(this,
						DialogRes.DIALOG_SEND_REGISTER_REQUEST));
	}

}
