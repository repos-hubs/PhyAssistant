package com.jibo.activity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.Constant;
import com.jibo.common.SoapRes;
import com.jibo.data.ProfilePaser;
import com.jibo.data.RetriveArticlePaser;
import com.jibo.data.entity.ProfileEntity;
import com.jibo.data.entity.RetrieveArticleEntity;
import com.jibo.dbhelper.ProfileAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.NavigateView.GotoBackFirstInit;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;
public class AcademicProfileActivity extends BaseSearchActivity implements OnClickListener {

	private RelativeLayout mWrittenLayout;
	private TextView     mWrittenTitle;
	private TextView     mWritten;
	private RelativeLayout mCoauthorLayout;
	private TextView     mCoauthorTitle;
	private TextView     mCoauthor;
	private TextView name;
	private TextView academicStatus;
	private TextView keyWordsTitle;
	private TextView keyWords;
	private TextView specialtyTitle;
	private TextView subSpecialtyTitle;
	private TextView specialty;
	private TextView subSpecialty;
	private TextView hospitalTitle;
	private TextView hospital;
	private BaseResponseHandler baseHandler;
	private TextView coAuthor;
	private TextView paperWrite;
	private RelativeLayout coAuthorLayout;
	private RelativeLayout paperWriteLayout;
	private GBApplication app;
	Context context=null;
	String mDoctorId=null;
	private ProfileAdapter profileAdapter;
	/*用于接收从圈子好友那边返回到自己个人信息时传过来的Bean*/
	/*当大于等于第二次点击我的合著者就需要用到这个Bean， add by Terry*/
	private ProfileEntity en = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.academic_profile);
		super.onCreate(savedInstanceState);
		inits();
		context=this;
//		new Thread(){
//			@Override
//			public void run() {
//				uploadDataNew.uploadLoginLogNew(context,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "Activity","Profile", "create", null);
//			}
//		}.start();
		
		
		uploadLoginLogNew("Activity", "Profile", "create", null) ;
		findViewById(R.id.AcademicProfileWrittenLayout).setVisibility(View.GONE);
		findViewById(R.id.AcademicProfileCoauthorLayout).setVisibility(View.GONE);
	}
	
	
    @Override 
    protected void onDestroy() { 
        super.onDestroy(); 
//		new Thread(){
//			@Override
//			public void run() {
//				uploadDataNew.uploadLoginLogNew(context,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "Activity","Profile", "end", null);
//			}
//		}.start();
        
        uploadLoginLogNew("Activity","Profile","end", null) ;
    }
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()) {
		case R.id.AcademicProfileCoauthorLayout:
			intent = new Intent(this, NetworkCoauthorActivity.class);
			System.out.println("doctorId      "+app.getLogin().getDoctorId());
			/*第一次登陆的时候用默认的医生ID，后面如果其他的Activity传过来的ID就更新为这个ID对应的医生 update by Terry*/
			if (null == en){
				intent.putExtra("doctorID", app.getLogin().getDoctorId());
			}else{
				intent.putExtra("doctorID", en.getId());
			}
			
			mDoctorId=app.getLogin().getDoctorId();
			MobclickAgent.onEvent(this,"mDoctorId",app.getLogin().getDoctorId(),1);
//			new Thread() {
//
//				@Override
//				public void run() {
//					uploadDataNew.uploadLoginLogNew(context, new SimpleDateFormat(
//							"yyyy-MM-dd HH:mm:ss").format(new Date()), "Profile",
//							mDoctorId,"mDoctorId", null);
//				}
//			}.start();
			
			uploadLoginLogNew( "Profile", mDoctorId, "mDoctorId",null); 
			startActivity(intent);
			break;
		case R.id.AcademicProfileWrittenLayout:
			Properties propertyInfo =new Properties();
			propertyInfo.put(SoapRes.KEY_RETRIEVE_ARTICLE_DOCTORID, app.getLogin().getDoctorId());
			sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_RETRIEVE_ARTICLE,
					propertyInfo, baseHandler);
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
			this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void inits() {
		app = (GBApplication) getApplication();
		baseHandler = new BaseResponseHandler(this);
		profileAdapter = new ProfileAdapter(this, Constant.MY_SQLITE_VESION);
		name = (TextView) findViewById(R.id.AcademicProfileName);
		academicStatus = (TextView) findViewById(R.id.AcademicProfileAcademicStatus);
		keyWordsTitle = (TextView) findViewById(R.id.AcademicProfileFiveKeyWordsTitle);
		keyWords = (TextView) findViewById(R.id.AcademicProfileFiveKeyWords);
		specialtyTitle = (TextView) findViewById(R.id.AcademicProfileSpecialtyTitle);
		specialty = (TextView) findViewById(R.id.AcademicProfileSpecialty);
		subSpecialtyTitle = (TextView) findViewById(R.id.AcademicProfileSubSpecialtyTitle);
		subSpecialty = (TextView) findViewById(R.id.AcademicProfileSubSpecialty);
		hospitalTitle = (TextView) findViewById(R.id.AcademicProfileHospitalTitle);
		hospital  = (TextView) findViewById(R.id.AcademicProfileHospital);
		coAuthor  = (TextView) findViewById(R.id.AcademicProfileCoauthor);
		paperWrite  = (TextView) findViewById(R.id.AcademicProfileWritten);
		coAuthorLayout  = (RelativeLayout) findViewById(R.id.AcademicProfileCoauthorLayout);
		paperWriteLayout  = (RelativeLayout) findViewById(R.id.AcademicProfileWrittenLayout);
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		
		coAuthorLayout.setOnClickListener(this);
		paperWriteLayout.setOnClickListener(this);
		txtTitle.setText(getString(R.string.academic_profile));
		/*原来为局部变量，先修改为私有字段，因为在点击事件中需要用到  update by Terry*/
//		ProfileEntity en = getIntent().getParcelableExtra("data");
		en = getIntent().getParcelableExtra("data");
		showText(en);
		setPopupWindowType(Constant.MENU_TYPE_4);
	}
	
	private void showText(ProfileEntity entity) {
		name.getPaint().setFakeBoldText(true);
		keyWordsTitle.getPaint().setFakeBoldText(true);
		specialtyTitle.getPaint().setFakeBoldText(true);
		subSpecialtyTitle.getPaint().setFakeBoldText(true);
		hospitalTitle.getPaint().setFakeBoldText(true);
		name.setText(entity.getName());
		hospitalTitle.setText(getString(R.string.hospital)+getString(R.string.colon));
		specialtyTitle.setText(getString(R.string.specialty)+getString(R.string.colon));
		subSpecialtyTitle.setText(getString(R.string.sub_specialty)+getString(R.string.colon));
		keyWordsTitle.setText(getString(R.string.five_key_words)+getString(R.string.colon));
		
		specialty.setText(judgeIsNull(entity.getBigSpecialty()));
		subSpecialty.setText(judgeIsNull(entity.getSpecialty()));
		hospital.setText(judgeIsNull(entity.getHospitalName()));
		keyWords.setText(judgeIsNull(entity.getKeywords()));
		coAuthor.setText(judgeIsNull(entity.getCoauthorCount()));
		paperWrite.setText(judgeIsNull(entity.getPaperCount()));
	}
	
	public String judgeIsNull(String str) {
		String result = "";
		if(!"anyType{}".equals(str)) {
			result = str;
		}
		return result;
	}
	@Override
	public void onReqResponse(Object o,int methodId) {
		if(o!=null) {
			if(o instanceof ProfilePaser) {
				ProfilePaser vd =(ProfilePaser)o;
				ProfileEntity en = vd.getEntity();
				showText(en);
				profileAdapter.setProfile(en);
			} else if(o instanceof RetriveArticlePaser) {
				RetriveArticlePaser obj = (RetriveArticlePaser)o;
				ArrayList<RetrieveArticleEntity> list = obj.getArticleList();
				Intent intent = new Intent(this, CategoryArticlesActivity.class);
				intent.putParcelableArrayListExtra("ArticleList", list);
				intent.putExtra(CategoryArticlesActivity.CATEGORY_ARTICLES_TYPE, CategoryArticlesActivity.TYPE_ACADEMIC_PROFILE);
				startActivity(intent);
			}
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
