package com.jibo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.api.android.GBApp.R;
public class HelpActivity extends Activity implements OnClickListener {
	String str = null;
	ImageButton btnClose = null;// mainhelpview
	TextView tvdrughelpView = null, tvcalhelpView = null,
			tvnewshelpView = null, tvtoolhelpView = null, tvacahelpView = null,
			tvreshelpView = null, tveventhelpView = null,
			tvsurveyhelpView = null, tvnetwrkhelpView = null,
			tvhistoryFavhelpView = null;
	LinearLayout li = null, mainhelp = null, tvdrughelp, tvcalhelp = null,
			newshelp = null, tvnewshelp = null, tvtoolhelp = null,
			tvacahelp = null, tveventhelp = null, tvreshelp = null,
			tvsurveyhelp = null,tvnetwrkhelp = null,tvhistoryFavhelp = null;
	Boolean drgVisibleFlag = false, calVisbleFlag = false,
			newsVisbleFlag = false, toolVisbleFlag = false,
			acaVisbleFlag = false, resVisbleFlag = false,
			eventVisbleFlag = false, surveyVisbleFlag = false,
			netwrkVisbleFlag = false, hisfavVisbleFlag = false,
			mainhelpFlag = false;
	ImageView imgtvui = null, imgtvdrg = null, imgtvcal = null,
			imgtvnews = null, imgtvtool = null, imgtvaca = null,
			imgtvres = null, imgtvevent = null, imgtvsurvey = null,
			imgtvnetwrk = null,imgtvhis=null;
	ImageButton imgbtncls=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.helpactivity);
		btnClose = (ImageButton) findViewById(R.id.btnClose);
		tvdrughelp = (LinearLayout) findViewById(R.id.drughelp);
		tvcalhelp = (LinearLayout) findViewById(R.id.calhelp);
		tvnewshelp = (LinearLayout) findViewById(R.id.newshelp);
		tvtoolhelp = (LinearLayout) findViewById(R.id.toolhelp);
		tvacahelp = (LinearLayout) findViewById(R.id.acahelp);
		tvreshelp = (LinearLayout) findViewById(R.id.reshelp);
		tveventhelp = (LinearLayout) findViewById(R.id.eventhelp);
		tvsurveyhelp = (LinearLayout) findViewById(R.id.surveyhelp);
		tvnetwrkhelp = (LinearLayout) findViewById(R.id.netwrkhelp);
		tvhistoryFavhelp = (LinearLayout) findViewById(R.id.historyFavhelp);
		tvdrughelpView = (TextView) findViewById(R.id.drughelpview);
		tvcalhelpView = (TextView) findViewById(R.id.calhelpview);
		tvnewshelpView = (TextView) findViewById(R.id.newshelpview);
		tvtoolhelpView = (TextView) findViewById(R.id.toolhelpview);
		tvacahelpView = (TextView) findViewById(R.id.acahelpview);
		tvreshelpView = (TextView) findViewById(R.id.reshelpview);
		tveventhelpView = (TextView) findViewById(R.id.eventhelpview);
		tvsurveyhelpView = (TextView) findViewById(R.id.surveyhelpview);
		tvnetwrkhelpView = (TextView) findViewById(R.id.netwrkhelpview);
		tvhistoryFavhelpView = (TextView) findViewById(R.id.hisfavhelpview);
		imgtvui = (ImageView) findViewById(R.id.imgtvui);
		li = (LinearLayout) findViewById(R.id.mainhelpview);
		mainhelp = (LinearLayout) findViewById(R.id.mainhelp);
		imgtvdrg = (ImageView) findViewById(R.id.imgtvdrg);
		imgtvcal = (ImageView) findViewById(R.id.imgtvcal);
		imgtvnews = (ImageView) findViewById(R.id.imgtvnews);
		imgtvtool = (ImageView) findViewById(R.id.imgtvtool);
		imgtvaca = (ImageView) findViewById(R.id.imgtvaca);
		imgtvres = (ImageView) findViewById(R.id.imgtvres);
		imgtvevent = (ImageView) findViewById(R.id.imgtvevent);
		imgtvsurvey = (ImageView) findViewById(R.id.imgtvsurvey);
		imgtvnetwrk = (ImageView) findViewById(R.id.imgtvnetwrk);
		imgtvhis=(ImageView) findViewById(R.id.imgtvhis);
		imgbtncls=(ImageButton) findViewById(R.id.btnClose);
		imgbtncls.setOnClickListener(this);
		tvdrughelp.setOnClickListener(this);
		tvcalhelp.setOnClickListener(this);
		tvnewshelp.setOnClickListener(this);
		tvtoolhelp.setOnClickListener(this);
		tvacahelp.setOnClickListener(this);
		tvreshelp.setOnClickListener(this);
		tveventhelp.setOnClickListener(this);
		tvsurveyhelp.setOnClickListener(this);
		tvnetwrkhelp.setOnClickListener(this);
		tvhistoryFavhelp.setOnClickListener(this);
		mainhelp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewId = v.getId();
		switch (viewId) {
		case R.id.btnClose: {
			finish();
			break;
		}
		case R.id.drughelp:
			if (drgVisibleFlag == false) {
				tvdrughelpView.setVisibility(View.VISIBLE);
				drgVisibleFlag = true;
				imgtvdrg.setImageResource(R.drawable.moredtl_dwn);
			} else {
				tvdrughelpView.setVisibility(View.GONE);
				drgVisibleFlag = false;
				imgtvdrg.setImageResource(R.drawable.moredtl);
			}
			break;
		case R.id.calhelp:
			if (calVisbleFlag == false) {
				tvcalhelpView.setVisibility(View.VISIBLE);
				calVisbleFlag = true;
				imgtvcal.setImageResource(R.drawable.moredtl_dwn);
			} else {
				imgtvcal.setImageResource(R.drawable.moredtl);
				tvcalhelpView.setVisibility(View.GONE);
				calVisbleFlag = false;
			}
		case R.id.newshelp:
			if (newsVisbleFlag == false) {
				tvnewshelpView.setVisibility(View.VISIBLE);
				newsVisbleFlag = true;
				imgtvnews.setImageResource(R.drawable.moredtl_dwn);
			} else {
				imgtvnews.setImageResource(R.drawable.moredtl);
				tvnewshelpView.setVisibility(View.GONE);
				newsVisbleFlag = false;
			}
			break;
		case R.id.toolhelp:
			if (toolVisbleFlag == false) {
				tvtoolhelpView.setVisibility(View.VISIBLE);
				toolVisbleFlag = true;
				imgtvtool.setImageResource(R.drawable.moredtl_dwn);
			} else {
				imgtvtool.setImageResource(R.drawable.moredtl);
				tvtoolhelpView.setVisibility(View.GONE);
				toolVisbleFlag = false;
			}
			break;
		case R.id.acahelp:
			if (acaVisbleFlag == false) {
				tvacahelpView.setVisibility(View.VISIBLE);
				acaVisbleFlag = true;
				imgtvaca.setImageResource(R.drawable.moredtl_dwn);
			} else {
				tvacahelpView.setVisibility(View.GONE);
				acaVisbleFlag = false;
				imgtvaca.setImageResource(R.drawable.moredtl);
			}
			break;
		case R.id.reshelp:
			if (resVisbleFlag == false) {
				tvreshelpView.setVisibility(View.VISIBLE);
				imgtvres.setImageResource(R.drawable.moredtl_dwn);
				resVisbleFlag = true;
			} else {
				tvreshelpView.setVisibility(View.GONE);
				imgtvres.setImageResource(R.drawable.moredtl);
				resVisbleFlag = false;
			}
			break;
		case R.id.eventhelp:
			if (eventVisbleFlag == false) {
				tveventhelpView.setVisibility(View.VISIBLE);
				eventVisbleFlag = true;
				imgtvevent.setImageResource(R.drawable.moredtl_dwn);
			} else {
				tveventhelpView.setVisibility(View.GONE);
				eventVisbleFlag = false;
				imgtvevent.setImageResource(R.drawable.moredtl);
			}
			break;
		case R.id.surveyhelp:
			if (surveyVisbleFlag == false) {
				tvsurveyhelpView.setVisibility(View.VISIBLE);
				surveyVisbleFlag = true;
				imgtvsurvey.setImageResource(R.drawable.moredtl_dwn);
			} else {
				tvsurveyhelpView.setVisibility(View.GONE);
				surveyVisbleFlag = false;
				imgtvsurvey.setImageResource(R.drawable.moredtl);
			}
			break;
		case R.id.netwrkhelp:
			if (netwrkVisbleFlag == false) {
				tvnetwrkhelpView.setVisibility(View.VISIBLE);
				netwrkVisbleFlag = true;
				imgtvnetwrk.setImageResource(R.drawable.moredtl_dwn);
			} else {
				tvnetwrkhelpView.setVisibility(View.GONE);
				netwrkVisbleFlag = false;
				imgtvnetwrk.setImageResource(R.drawable.moredtl);
			}
			break;
		case R.id.historyFavhelp:
			if (hisfavVisbleFlag == false) {
				tvhistoryFavhelpView.setVisibility(View.VISIBLE);
				imgtvhis.setImageResource(R.drawable.moredtl_dwn);
				hisfavVisbleFlag = true;
			} else {
				tvhistoryFavhelpView.setVisibility(View.GONE);
				imgtvhis.setImageResource(R.drawable.moredtl);
				hisfavVisbleFlag = false;
			}
			break;
		case R.id.mainhelp:
			if (mainhelpFlag == false) {
				li.setVisibility(View.VISIBLE);
				imgtvui.setImageResource(R.drawable.moredtl_dwn);
				mainhelpFlag = true;
			} else {
				imgtvui.setImageResource(R.drawable.moredtl);
				li.setVisibility(View.GONE);
				mainhelpFlag = false;
			}
			break;

		default:
			break;
		}
	}
}
