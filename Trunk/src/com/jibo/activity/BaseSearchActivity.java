package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.AssociateAdapter;
import com.jibo.adapter.SearchHistoryAdapter;
import com.jibo.app.invite.Constants;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.app.research.PaperDetailLinkActivity;
import com.jibo.app.research.ResearchPageActivity;
import com.jibo.app.research.SpecialDetailActivity;
import com.jibo.app.research.SpecialListActivity;
import com.jibo.dbhelper.StoreSearchHistoryAdapter;
import com.jibo.news.NewsPageActivity;
import com.jibo.news.SearchPageActivity;
import com.jibo.ui.HomePageLayout;
import com.jibo.util.Logs;

public class BaseSearchActivity extends BaseActivity {
	private ImageButton mSearch;
	private ImageButton mSearchSelect;
	public AutoCompleteTextView mSearchEdit;
	private ImageButton mHome;
	private String searchFlag;
	private boolean[] mCheckedItemsArticle;
	private Builder builder;
	private boolean[] mCheckedItemsEvent;
	private Context curContext;
	public static final String SEARCH_TEXT = "search_text";
	private StoreSearchHistoryAdapter searchHistoryAdapter;
	private GBApplication app;
	private AssociateAdapter associateAdapter;
	protected String searchData;;
	protected ACTVTextWatcher actvTextWatcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeVar();
	}

	private void initializeVar() {
		curContext = this;
		app = (GBApplication) getApplication();
		searchHistoryAdapter = new StoreSearchHistoryAdapter(this, 1);
		mSearch = (ImageButton) this.findViewById(R.id.imgbtn_search);
		mSearchSelect = (ImageButton) this
				.findViewById(R.id.imgbtn_searchDropDown);
		mSearchEdit = (AutoCompleteTextView) this
				.findViewById(R.id.actv_searchEdit);
		mSearchEdit.setCursorVisible(true);

		mHome = (ImageButton) this.findViewById(R.id.imgbtn_home);

		setCurrentSearchIndex();

		mCheckedItemsArticle = new boolean[] { false, false, false, false,
				false, false };
		mCheckedItemsEvent = new boolean[] { false, false, false, false };
		mSearch.setOnClickListener(new HeaderClickListener());
		mSearchSelect.setOnClickListener(new HeaderClickListener());
		mHome.setOnClickListener(new HeaderClickListener());
		mSearchEdit.setOnKeyListener(new EdtKeyListener());

//		mSearchEdit.setOnTouchListener(new ViewTouchListener());

		Display display = getWindowManager().getDefaultDisplay();
		mSearchEdit.setDropDownAnchor(R.id.history_panel);
		mSearchEdit.setDropDownWidth(display.getWidth() - 16);
		mSearchEdit.setThreshold(1);
	}

	/**
	 * 收起软键盘并关闭提示文字
	 */
	public void closeInput() {
		mSearchEdit.setCursorVisible(false);
		mSearchEdit.dismissDropDown();
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(),
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * @author Rafeal Piao
	 * @description 显示搜索历史
	 */
	private class ViewTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			SearchHistoryAdapter historyAdapter = new SearchHistoryAdapter(
					curContext, searchHistoryAdapter.getHistory());
			mSearchEdit.setAdapter(historyAdapter);
			mSearchEdit.showDropDown();
			return false;
		}
	}

	private class ACTVTextWatcher implements TextWatcher {
		public ACTVTextWatcher() {
			System.out.println("ACTVTextWatcher   ");

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// if (associateAdapter == null) {

			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
//			if (s == null || s.toString().equals("")) {
//				SearchHistoryAdapter historyAdapter = new SearchHistoryAdapter(
//						curContext, searchHistoryAdapter.getHistory());
//				mSearchEdit.setAdapter(historyAdapter);
//				historyAdapter.notifyDataSetChanged();
//			} else {
				associateAdapter = new AssociateAdapter(curContext);
				mSearchEdit.setAdapter(associateAdapter);
				associateAdapter.notifyDataSetChanged();
				
//			}
			mSearchEdit.showDropDown();
		}

	}

	List<QueryThread> queue = new ArrayList<QueryThread>(0);
	private Handler searchHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.OperationContants.SEARCHNOTIFY:
				Logs.i("rebind --");
				associateAdapter.notifyDataSetChanged();
				break;

			case Constants.OperationContants.SEARCH_NOTIFY_EMPTY:
				// if (adapter != null) {
				// adapter.getItemDataSrc().setContent(Data.info);
				// adapter.notifyDataSetChanged();
				// listview.invalidate();
				// }
				break;
			}
		}
	};

	QueryThread quertThread;
	QueryThread currThread;

	public void baseKeywordSearch(String inputkeyWords) {
		queue.add(new QueryThread(inputkeyWords));
		QueryThread qthread = queue.get(queue.size() - 1);
		if (currThread != qthread) {
			if (currThread != null) {
				currThread.interrupt();
			}
			if (!qthread.isRunned()) {
				currThread = qthread;
				qthread.start();
			}
		}
	}

	class QueryThread extends Thread {

		public boolean runned;

		public QueryThread(String inputkeyWords) {
			this.inputkeyWords = inputkeyWords;
		}

		public String getInputkeyWords() {
			return inputkeyWords;
		}

		public boolean isRunned() {
			return runned;
		}

		String inputkeyWords;

		@Override
		public void run() {
			try {
				Logs.i("inputkeyWords --" + inputkeyWords);
				// if (inputkeyWords.trim().equals("")) {
				// searchHandler
				// .sendEmptyMessage(Constants.OperationContants.SEARCH_NOTIFY_EMPTY);
				// } else {
				Logs.i("dataChanged -- b");

				Logs.i("dataChanged -- a");
				searchHandler
						.sendEmptyMessage(Constants.OperationContants.SEARCHNOTIFY);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
			runned = true;
		}
	};

	private class HeaderClickListener implements OnClickListener {
		private Intent intent;
		String searchKey = "";

		@Override
		public void onClick(View v) {
			searchData = mSearchEdit.getText().toString().trim();

			switch (v.getId()) {
			case R.id.imgbtn_search:
				if (!searchData.equals("")) {
					searchHistoryAdapter.storeSearchHistory(searchData);
				}
				if (getResources().getString(R.string.drug).equals(searchFlag)) {
//					if (context.getClass() == DrugReferenceListActivity1.class) {
//						closeInput();
//					} else {
						intent = new Intent(BaseSearchActivity.this,
								DrugReferenceListActivity1.class);
						intent.putExtra(SEARCH_TEXT, searchData);
						BaseSearchActivity.this.startActivity(intent);
//					}
					searchKey = "Drug";
				} else if (getResources().getString(R.string.event).equals(
						searchFlag)) {
					showSubDialog(searchFlag);
					searchKey = "Event";
				} else if (getResources().getString(R.string.news).equals(
						searchFlag)) {
					try {
						intent = new Intent(curContext,
								SearchPageActivity.class);
						// intent.putExtra(NewsByCategoryActivity.NEWS_TYPE,
						// NewsByCategoryActivity.TYPE_RESULT);
						intent.putExtra(SEARCH_TEXT, searchData);
						startActivity(intent);
						searchKey = "News";
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (getResources().getString(R.string.research).equals(
						searchFlag)) {
					searchKey = "Research";
					if (context.getClass() == com.jibo.app.research.SearchPageActivity.class) {
						launchChild();
						closeInput();
						return;
					}
					intent = new Intent(curContext,
							com.jibo.app.research.SearchPageActivity.class);
					// intent.putExtra(NewsByCategoryActivity.NEWS_TYPE,
					// NewsByCategoryActivity.TYPE_RESULT);
					intent.putExtra(SEARCH_TEXT, searchData);
					startActivity(intent);
				} else if (getResources().getString(R.string.calculator)
						.equals(searchFlag)) {
					intent = new Intent(curContext,
							TabCalcList_NewActivity.class);
					intent.putExtra(SEARCH_TEXT, mSearchEdit.getText()
							.toString());
					startActivity(intent);
					searchKey = "Calc.";
				}
				closeInput();
				break;
			case R.id.imgbtn_searchDropDown:
				showItemList();
				break;
			case R.id.imgbtn_home:
				if (!(curContext instanceof HomePageActivity)) {
					HomePageLayout.s_pageID = 0;
					intent = new Intent(curContext, HomePageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				break;

			}
			if (actvTextWatcher == null) {
				actvTextWatcher = new ACTVTextWatcher();
				mSearchEdit.addTextChangedListener(actvTextWatcher);
			}
			uploadLoginLogNew("Search", searchKey, searchData, null);
		}

	}

	public void launchChild() {
		// TODO Auto-generated method stub

	}

	private class EdtKeyListener implements OnKeyListener {
		private boolean clickFlag;

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				new HeaderClickListener().onClick(mSearch);
			}
			return false;
		}
	}

	/**
	 * @author Rafeal Piao
	 * @description set search hint
	 * @param void
	 * @return void
	 * @Exception
	 */
	private void setCurrentSearchIndex() {
		searchFlag = getString(R.string.drug);
		if (curContext instanceof HomePageActivity
				|| curContext instanceof NetworkCoauthorActivity
				|| curContext instanceof AcademicProfileActivity
				|| curContext instanceof AccountManagerActivity
				|| curContext instanceof SurveyActivity
				|| curContext instanceof ECGListActivity
				|| curContext instanceof ECGArticleActivity
				|| curContext instanceof ToolsActivity
				|| curContext instanceof NCCNListActivity
				|| curContext instanceof TumorActivity
				|| curContext instanceof TumorArticleActivity
				|| curContext instanceof HistoryFavoriteActivity
				|| curContext instanceof DrugReferenceListActivity1
				|| curContext instanceof DrugReferenceAcademicActivity) {
			searchFlag = getString(R.string.drug);
		} else if (curContext instanceof TabCalcList_NewActivity
				|| curContext instanceof TabCalcInfoActivity2) {
			searchFlag = getString(R.string.calculator);
		} else if (curContext instanceof EventsActivity
				|| curContext instanceof RelatedEventsActivity) {
			searchFlag = getString(R.string.event);
		} else if (curContext instanceof NewsDetailActivity
				// || curContext instanceof NewsByCategoryActivity
				|| curContext instanceof RelatedNewsActivity
				|| curContext.getClass() == NewsPageActivity.class
				|| curContext.getClass() == SearchPageActivity.class) {
			searchFlag = getString(R.string.news);
		} else if (curContext instanceof ResearchActivity
				|| curContext instanceof CategoryArticlesActivity
				|| curContext instanceof RelatedArticlesActivity
				|| curContext instanceof WebViewActivity
				|| curContext instanceof ResearchPageActivity
				|| curContext instanceof com.jibo.app.research.SearchPageActivity
				|| curContext instanceof PaperDetailActivity
				|| curContext instanceof PaperDetailLinkActivity
				|| curContext instanceof SpecialDetailActivity
				|| curContext instanceof SpecialListActivity) {
			searchFlag = getString(R.string.research);
			mSearchEdit.removeTextChangedListener(actvTextWatcher);
		}
		// mSearchEdit.setHint("\t\t"+searchFlag);
		Log.e("searchFlag", String.valueOf(searchFlag));
		mSearchEdit.setHint(searchFlag);
	}

	/**
	 * @author Rafeal Piao
	 * @description show search dorpdown dialog 搜索分类
	 * @param void
	 * @return void
	 * @Exception
	 */
	private void showItemList() {
		builder = new AlertDialog.Builder(this);
		final String[] arr = getResources().getStringArray(
				R.array.drop_down_list);
		builder.setTitle(R.string.select_dialog);
		builder.setIcon(R.drawable.icon);
		builder.setItems(arr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mSearchEdit.setText(null);
				mSearchEdit.setHint(arr[which]);
				searchFlag = arr[which];
			}
		});
		builder.create().show();
	}

	/**
	 * @author Rafeal Piao
	 * @description show dialog, when search events and research
	 * @param String
	 *            action: search flag
	 * @return void
	 * @Exception
	 */
	private void showSubDialog(String action) {
		builder = new Builder(this);
		if (getResources().getString(R.string.research).equals(searchFlag)) {
			builder.setMultiChoiceItems(
					getResources().getStringArray(R.array.research_sub_dialog),
					mCheckedItemsArticle,
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
						}
					});
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(curContext,
									CategoryArticlesActivity.class);
							intent.putExtra("category_articles_type",
									"type_advanced_search");
							intent.putExtra(SEARCH_TEXT, searchData);
							intent.putExtra("select_item", mCheckedItemsArticle);
							curContext.startActivity(intent);
						}
					}).setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
		} else if (getResources().getString(R.string.event).equals(searchFlag)) {
			builder.setTitle(R.string.select_dialog);
			builder.setMultiChoiceItems(
					getResources().getStringArray(R.array.event_sub_dialog),
					mCheckedItemsEvent,
					new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							mCheckedItemsEvent[which] = isChecked;
						}
					});
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								Intent intent = new Intent(curContext,
										EventsActivity.class);
								intent.putExtra(SEARCH_TEXT, searchData);
								intent.putExtra("select_item",
										mCheckedItemsEvent);
								curContext.startActivity(intent);
							} catch (Exception e) {
								e.printStackTrace();
								dialog.dismiss();
								return;
							}
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
		}
		builder.create().show();
	}
}
