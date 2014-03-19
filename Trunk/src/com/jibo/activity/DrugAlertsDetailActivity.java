package com.jibo.activity;

import java.util.ArrayList;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.DrugAlertByTypeIdPaser;
import com.jibo.data.DrugAlertDetailListPaser;
import com.jibo.data.entity.DrugAlertDetailEntity;
import com.jibo.data.entity.DrugAlertEntity;
import com.jibo.dbhelper.DrugAlertSQLAdapter;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.GBAImageButton;
import com.jibo.ui.TextField;
import com.umeng.analytics.MobclickAgent;

/**
 * ͨ����ϸ����
 * 
 * @author simon
 * 
 */
public class DrugAlertsDetailActivity extends BaseSearchActivity implements
		OnGestureListener {

	private final String TAG = "DrugAlerts";

	private Context context;
	private DrugAlertSQLAdapter dbAdapter;
	// �ղذ�ť
	private Button collectBtn;

	// ��������
	public String sharing_inf = "";

	private LinearLayout layout;
	private Handler handler;

	private boolean[] isExpand;// ����Ƿ�չ�� true��չ�� �� false���ر�
	private GBAImageButton[] buttons; // ͨ����ϸ���⼯
	private LinearLayout[] contents; // ͨ����ϸ���ݼ�
	private LinearLayout[] subLayout; // ͨ����ϸ�Ӳ��ּ�(����+����)
	private String[] titles;// �������ݼ�
	private ImageView imgForTurnedToView;// ��ת��ĳ�Ӳ���
	private TextView contentText;// ͨ������(����)
	private ScrollView scrollView;
	private DrugAlertEntity alert;
	private ArrayList<DrugAlertDetailEntity> list;
	private FavoritDataAdapter favoritadpter;

	private LinearLayout progressDialog;

	private String typeId;

	private int index;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.closeDB();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drugalerts_detail);
		super.onCreate(savedInstanceState);
		context = this;
		favoritadpter = new FavoritDataAdapter(this);
		collectBtn = (Button) findViewById(R.id.favoritBtn);
		progressDialog = (LinearLayout) findViewById(R.id.dialogprogress);
		((TextView) findViewById(R.id.txt_header_title))
				.setText(R.string.drugalert);
		createHandler();
		dbAdapter = new DrugAlertSQLAdapter(this);

		scrollView = (ScrollView) findViewById(R.id.drugdetail_scrollview);
		layout = (LinearLayout) findViewById(R.id.drugalert_detail_content);
		imgForTurnedToView = (ImageView) findViewById(R.id.imgForTurnedToView);
		imgForTurnedToView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showNavigationDialog();
			}
		});
		MobclickAgent.onError(this);
		Intent intent = getIntent();
		boolean isLoadLocal = intent.getBooleanExtra("isLoadLocal", false);
		String fromActivity = intent.getStringExtra("fromActivity");
		if (null != fromActivity && fromActivity.equals("DrugAlertsActivity")) {
			progressDialog.setVisibility(View.GONE);
			Log.i("time", "isLoadLocal=" + isLoadLocal);
			alert = (DrugAlertEntity) intent
					.getParcelableExtra("DrugAlertEntity");
			ArrayList<?> data = (ArrayList<?>) intent
					.getParcelableArrayListExtra("DrugAlertDetailList");
			list = (ArrayList<DrugAlertDetailEntity>) data;

			showAlertContent();
			showDetailContent();
			if (isLoadLocal) {// ���������ݣ�������
				runThread(1);
			} else {
				runThread(2);
			}
		} else {
			typeId = intent.getStringExtra("typeID");
			alert = dbAdapter.getDrugAlertsByTypeId(typeId);
			imgForTurnedToView.setVisibility(View.GONE);

			if (alert != null) {
				index++;
				showAlertContent();
			} else {
				Properties propertyInfo = new Properties();

				propertyInfo.put(SoapRes.KEY_DRUGALERT_TYPEID, typeId);
				propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE, "");
				propertyInfo.put(SoapRes.KEY_SINCE_ID, "");
				propertyInfo.put(SoapRes.KEY_MAX_ID, "");
				propertyInfo.put(SoapRes.KEY_COUNT, "");
				sendRequest(SoapRes.URLDrug,
						SoapRes.REQ_ID_GET_DRUGALERT_BY_TYPEID, propertyInfo,
						new BaseResponseHandler(this, false));
			}
			list = dbAdapter.getDrugAlertsDetailByLocalDatabase(typeId);
			if (null != list && list.size() > 0) {
				showDetailContent();
				if (index >= 1) {
					progressDialog.setVisibility(View.GONE);
					imgForTurnedToView.setVisibility(View.VISIBLE);
				}
				index++;
			} else {
				Properties propertyInfo = new Properties();
				propertyInfo.put("Type_ID", typeId);
				sendRequest(SoapRes.URLDrug,
						SoapRes.REQ_ID_GET_DRUGALERT_GET_DETAIL, propertyInfo,
						new BaseResponseHandler(this, false));
			}
		}

		// MobclickAgent.setDebugMode(true);
		// context = DrugAlertsDetailActivity.this;
		// MobclickAgent.setSessionContinueMillis(1000);// change 30000(default)
		// to
		// // 1000
		// MobclickAgent.onError(this);
		//
		// MobclickAgent.updateOnlineConfig(this);
		// MobclickAgent.setAutoLocation(false);// collect location info, need
		// some
		// // permission. = true(default)
		// // set debug mode ,will print log in logcat(lable:MobclickAgent)
		// // =true(default)
		// UmengConstants.enableCacheInUpdate = false;
		// MobclickAgent.update(this, 1000 * 60 * 60 * 24);// daily
		// MobclickAgent.onEvent(context, "DrugAlertsDetail",
		// "DrugAlertsDetail",
		// 1);// "SimpleButtonclick");
		// MobclickAgent
		// .setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);

		// �ղ� ��ť
		

		collectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (dbAdapter.selectDrugAlertCollection(alert.getTypeId()) > 0) {
					Log.e("1", "1");

					if (dbAdapter.delDrugAlertCollection(alert.getTypeId())) {
						Toast toast = Toast.makeText(context,
								context.getString(R.string.cancelFav),
								Toast.LENGTH_LONG);
						toast.setGravity(Gravity.TOP, 0, 220);
						toast.show();
						collectBtn.setBackgroundResource(R.drawable.btnchg);
					}

				} else {
					Log.e("2", "2");
					Toast toast = Toast.makeText(context,
							context.getString(R.string.favorite),
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
					dbAdapter.insertDrugAlertCollection(alert.getTypeId(),
							alert.getTitle());
					// btnFlag=true;
					collectBtn.setBackgroundResource(R.drawable.btnunchg);
				}

			}
		});

		setPopupWindowType(Constant.MENU_TYPE_6);// ���menu
	}

	/**
	 * ��������dialog
	 */
	private void showNavigationDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.selectshortcut)
				.setIcon(R.drawable.icon).setItems(titles, ol).create().show();
	}

	/**
	 * ��ʾ�����ͨ������
	 */
	private void showAlertContent() {
		// ����
		TextView titleText = (TextView) findViewById(R.id.drugalertdetail_title);
		titleText.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		titleText.getPaint().setFakeBoldText(true);
		titleText.setText(alert.getTitle());

		String mString = alert.getContent().replaceAll("\\n\\s*", "\n");
		if (isChinese(mString)) {// ֻ�������ĵ��Ű�
			contentText = new TextField(this, null, alert.getContent(), true, 0);
		} else {
			contentText = new TextView(this);
			contentText.setText(mString);
			contentText.setTextColor(Color.BLACK);
		}
		setAttrabute(contentText, alert.getContent());
		layout.addView(contentText, 2);
		
		if (dbAdapter.selectDrugAlertCollection(alert.getTypeId()) > 0) {
			 collectBtn.setBackgroundResource(R.drawable.btnunchg);
	    }
	}

	/**
	 * ����ַ��Ƿ�������
	 * 
	 * @param c
	 * @return
	 */
	private boolean isChinese(String str) {
		if (null == str)
			return false;
		String s = str.replaceAll(" ", "");
		int length = s.length();
		if (length == 0)
			return false;
		int count = 0;
		for (int i = 0; i < 100; i++) {

			int index = (int) (Math.random() * length);
			Log.i("lushan", "index:" + index + ",length:" + length);
			count += isChineseWithChar(s.charAt(index));
		}
		if (count > 50)
			return true;
		return false;
	}

	private int isChineseWithChar(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return 1;
		}
		return 0;
	}

	/**
	 * ��ʾͨ�����ϸ����
	 */
	private void showDetailContent() {
		int Width = getWindowManager().getDefaultDisplay().getWidth();
		int length = list.size();
		if (length <= 0) {
			imgForTurnedToView.setVisibility(View.GONE);
		} else {
			isExpand = new boolean[length];
			buttons = new GBAImageButton[length];
			contents = new LinearLayout[length];
			titles = new String[length];
			subLayout = new LinearLayout[length];
		}

		int layoutWidth = Width
				- (layout.getPaddingLeft() + layout.getPaddingRight()) - 10;

		Log.i("lushan", (layout.getPaddingLeft() + layout.getPaddingRight())
				+ "");

		int i = 0;
		for (DrugAlertDetailEntity detail : list) {
			final int position = i;
			LinearLayout lay = (LinearLayout) getLayoutInflater().inflate(
					R.layout.drugalerts_detail_item, null);

			final LinearLayout content = (LinearLayout) lay
					.findViewById(R.id.drugAlertDetailContent);
			content.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.academic_profile_content));
			TextView textContent = null;

			String mString = detail.getDetailContent().replaceAll("\\n\\s*",
					"\n");
			if (isChinese(mString)) {// ֻ�������ĵ��Ű�
				textContent = new TextField(this, null,
						detail.getDetailContent(), true, layoutWidth);
			} else {
				textContent = new TextView(this);
				textContent.setText(mString);
				textContent.setTextColor(Color.BLACK);
			}

			setAttrabute(textContent, detail.getDetailContent());

			content.addView(textContent);

			GBAImageButton button = (GBAImageButton) lay
					.findViewById(R.id.drugAlertDetailTitleBar);
			button.GBAImageButtonSetText(detail.getDetailTitle());
			titles[i] = detail.getDetailTitle();
			button.GBAImagesetBackgroundResource(R.drawable.btn_bg1);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isExpand[position]) {
						content.setVisibility(View.VISIBLE);
						isExpand[position] = true;
						((GBAImageButton) v)
								.GBAArrowImagesetResource(R.drawable.down_arrow);
					} else {
						content.setVisibility(View.GONE);
						isExpand[position] = false;
						((GBAImageButton) v)
								.GBAArrowImagesetResource(R.drawable.right_arrow);
					}
				}
			});

			buttons[i] = button;
			contents[i] = content;
			isExpand[i] = false;
			subLayout[i] = lay;
			layout.addView(lay);
			i++;
		}
	}

	private void setAttrabute(TextView text, String string) {
		int Width = getWindowManager().getDefaultDisplay().getWidth();
		int Height = getWindowManager().getDefaultDisplay().getHeight();

		if ((Width * Height) <= (320 * 480)) {
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
		} else {
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
		}
		// text.setText(string);
		// text.setTextColor(Color.TRANSPARENT);
	}

	public void createHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:// ��ǰ��⵽�������� �и��£���ȡ��������
					Properties propertyInfo = new Properties();
					propertyInfo.put("Type_ID", alert.getTypeId());
					Log.i("time", "��ʼ��ȡ��������");
					sendRequest(SoapRes.URLDrug,
							SoapRes.REQ_ID_GET_DRUGALERT_GET_DETAIL,
							propertyInfo, new BaseResponseHandler(
									DrugAlertsDetailActivity.this,
									DialogRes.DIALOG_UPDATE_FOR_DATA));
					break;
				case 2:// ������ͼ
					layout.removeViews(3, layout.getChildCount() - 3);
					showDetailContent();
					if (index >= 1) {
						progressDialog.setVisibility(View.GONE);
						imgForTurnedToView.setVisibility(View.VISIBLE);
					}
					index++;
					break;
				}
			}
		};
	}

	public void runThread(final int type) {
		new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				boolean success = false;
				if (type == 1) {// ������
					if (null != alert.getTime()) {
						String new_date = dbAdapter
								.getDrugAlertsDetailTimeByTypeID(alert
										.getTypeId());// ��ȡ����ʱ��
						Log.i("time", "������ʱ�䣺" + alert.getTime()
								+ ">>>>>>>����ʱ�䣺" + new_date);
						if (null != new_date
								&& !alert.getTime().equals(new_date)) {// �������и���
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						}
					}
				} else if (type == 2) {// ����Ƿ���Ҫ����
					dbAdapter.deleteDrugAlertDetailLocalCache();// ����ֻ����3��
					int count = dbAdapter.getDrugAlertsDetailCount();
					if (count < 50) {// ���ػ��������Ϊ50��������չ����
						success = dbAdapter.insertDrugAlertDetails(list,
								alert.getTypeId(), alert.getTime());
					}
					Log.i(TAG, success + "");
				} else if (type == 3) {// ������µ�����
					Log.i("time", "�����������");
					dbAdapter.deleteDrugAlertDetails(alert.getTypeId());// ɾ���ɻ���
					success = dbAdapter.insertDrugAlertDetails(list,
							alert.getTypeId(), alert.getTime());// ������º������
					Log.i(TAG, success + "");
					if (success) {
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		super.onReqResponse(o, methodId);
		if (o != null) {
			if (o instanceof DrugAlertDetailListPaser) {// ��ȡ��������,����ص�
				Log.i("time", "��ȡ����������������ص�action..");
				list = ((DrugAlertDetailListPaser) o).getList();
				runThread(3);
			}
			if (o instanceof DrugAlertByTypeIdPaser) {// ��ȡ��������,����ص�
				Log.i("time", "��ȡ����������������ص�action..");
				alert = ((DrugAlertByTypeIdPaser) o).getEntity();
				showAlertContent();
				if (index >= 1) {
					progressDialog.setVisibility(View.GONE);
					imgForTurnedToView.setVisibility(View.VISIBLE);
				}
				index++;
				if (favoritadpter.selectDrugAlert(this, alert.getTypeId(),
						SharedPreferencesMgr.getUserName()) > 0) {
					collectBtn.setBackgroundResource(R.drawable.btnunchg);
				}
			}
		}
	}

	/**
	 * ��ʼ��Menu
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// ���봴��һ��
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * �����ת�Ӳ���
	 */
	private DialogInterface.OnClickListener ol = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(final DialogInterface dialog, int which) {

			if (!isExpand[which]) {
				isExpand[which] = true;
				contents[which].setVisibility(View.VISIBLE);
				buttons[which].GBAArrowImagesetResource(R.drawable.down_arrow);
			}

			final int index = which;
			scrollView.post(new Runnable() {

				@Override
				public void run() {
					int height = contentText.getHeight();
					for (int i = 0; i < index; i++) {
						height += subLayout[i].getHeight();
					}
					scrollView.scrollTo(0, height);
					dialog.dismiss();
				}
			});

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (isPopflg()) {
				dissMissPop();
			} else {
				if (iCurState == STATE_CONNECTING) {
					cancelConnection();
				}
				this.finish();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
}
