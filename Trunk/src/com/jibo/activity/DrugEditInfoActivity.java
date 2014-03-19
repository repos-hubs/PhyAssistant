package com.jibo.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.dbhelper.DrugEditAdapter;

public class DrugEditInfoActivity extends BaseSearchActivity {
	private EditText edtInfo;
	private Button btnSave;
	private TextView txtTitle;
	private DrugEditAdapter editAdapter;
	private GBApplication app;
	private String drugId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_info);
		inits();
		super.onCreate(savedInstanceState);
	}
	
	public void inits() {
		drugId = getIntent().getStringExtra("drugId");
		System.out.println("drugId    "+drugId);
		app = (GBApplication)getApplication();
		editAdapter = new DrugEditAdapter(app.getLogin().getGbUserName(), drugId, this);
		edtInfo = (EditText) findViewById(R.id.edtInfo);
		txtTitle = (TextView) findViewById(R.id.txt_header_title);
		btnSave = (Button) findViewById(R.id.btn_save);
		
		edtInfo.setText(editAdapter.getDrugEditInfo());
		txtTitle.setText(getString(R.string.drug_note));
		btnSave.setOnClickListener(new BtnClickListener());
	}
	
	private class BtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.btn_save:
				if(editAdapter.updateDrugEditInfo(edtInfo.getText().toString())) {
					//TODO
					finish();
				}
				editAdapter.closeDB();
				app.setHomeLaunched(false);
				app.setStartActivity(true);
				break;
			}
		}
	}
}
