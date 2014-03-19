package com.jibo.activity;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jibo.GBApplication;
import com.jibo.dbhelper.NCCNAdapter;
import com.jibo.ui.CircleIndicator;
import com.api.android.GBApp.R;
public class NCCNDiseaseInfoActivity extends BaseActivity implements OnClickListener
									, OnGestureListener, OnTouchListener{
	private DiseaseViewFlipper vf;
	private Display display;
	private int screenWidth;
	
	private GestureDetector mGestureDetector;
	private String diseaseID;
	
	private boolean isFling = false;
	private int count = 0;
	private static int sMinDistance = 0;
	private static int sPositionFlag = 0;
	private float scale;
	private int indicatorSize = 0;
	private final int Menu_Comment_ID = 0x123456;
	private TextView currentText;
	private NCCNAdapter nccnAdapter;
	private GBApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		inits();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1!=null && e2!= null && isFling != true) {
			if(e1.getX() - e2.getX() > sMinDistance  && vf.getCurrentView().getId() != vf.getChildCount()) {
				if(vf.getChildCount() > 1) {
					vf.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
					vf.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
					vf.showNext();
				} else {
					vf.clearAnimation();
				}
				
			} else if(e2.getX() - e1.getX() > sMinDistance) {
				if(vf.getChildCount() > 1) {
					vf.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
					vf.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
					vf.showPrevious();
					vf.removeViewAt(vf.getChildCount() - 1);
				} else {
					vf.clearAnimation();
				}
			}
		}
		
		return isFling;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
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
	public void onClick(View v) {
		if(v instanceof TextView) {
			TextView view = (TextView)v;
			String nodeStr = view.getTag().toString().trim();
			String linkID = nccnAdapter.getLinkFromSubId(nodeStr, diseaseID);
			vf.addView(createVF(diseaseID, linkID));
			vf.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
			vf.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
			
			vf.showNext();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
			if(vf.getChildCount() > 1) {
				vf.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
				vf.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
				vf.showPrevious();
				vf.removeViewAt(vf.getChildCount() - 1);
			} else {
				finish();
			}
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu_Comment_ID, 0, getString(R.string.nccn_comment));
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case Menu_Comment_ID:
			Intent intent = new Intent();
			intent.setClass(this, NCCNCommentActivity.class);
			intent.putExtra("id", diseaseID);
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	public void inits() {
		app = (GBApplication) getApplication();
		nccnAdapter = new NCCNAdapter(this, 1);
		vf = new DiseaseViewFlipper(this, null);
        mGestureDetector = new GestureDetector(this);
        Intent intent = getIntent();
        scale = getResources().getDisplayMetrics().density;
        if(scale == 1.0) {
        	indicatorSize = 17;
        } else {
        	indicatorSize = 25;
        }
        sMinDistance = (int) (scale*20 + 0.5f);
        if(intent != null) {
        	diseaseID = intent.getStringExtra("id");
        }
        setContentView(vf);
        screenWidth = (int) app.getDeviceInfo().getScreenWidth();
        vf.addView(createVF(diseaseID, nccnAdapter.getFirstNode(diseaseID)));
	}
	
	/**
	 * @author Rafeal Piao
	 * @Description Customize ViewFlipper 
	 * @date 2011-11-15
	 */
	class DiseaseViewFlipper extends ViewFlipper {
	    private static final int DRAG_BOUNDS_IN_DP = 20;
	    private static final int SCROLL_LOCK_NONE = 0;
	    private static final int SCROLL_LOCK_VERTICAL = 1;
	    private static final int SCROLL_LOCK_HORIZONTAL = 2;
	    private float mTouchStartX;
	    private float mTouchStartY;
	    private int mScrollLock = SCROLL_LOCK_NONE;
		public DiseaseViewFlipper(Context context, AttributeSet attrs) {
			super(context, attrs);
			initCustomGallery(context);
		}
		
		private void initCustomGallery(Context context) {
	    }
		
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			 final int action = ev.getAction();
		        switch (action) {
		        case MotionEvent.ACTION_DOWN:
		            mTouchStartX = ev.getX();
		            mTouchStartY = ev.getY();
		            mScrollLock = SCROLL_LOCK_NONE;

		            super.onTouchEvent(ev);
		            break;

		        case MotionEvent.ACTION_MOVE:
		            if (mScrollLock == SCROLL_LOCK_VERTICAL) {
		                return false;
		            }

		            final float touchDistanceX = (ev.getX() - mTouchStartX);
		            final float touchDistanceY = (ev.getY() - mTouchStartY);

		            if (Math.abs(touchDistanceY) > sMinDistance) {
		                mScrollLock = SCROLL_LOCK_VERTICAL;
		                return false;
		            }
		            if (Math.abs(touchDistanceX) > sMinDistance) {
		                mScrollLock = SCROLL_LOCK_HORIZONTAL; 
//		                return true;
		                isFling = true;
		                
		            	if(touchDistanceX > sMinDistance) {
		            		if(vf.getChildCount() > 1) {
		            			vf.setInAnimation(AnimationUtils.loadAnimation(NCCNDiseaseInfoActivity.this,R.anim.push_right_in));
		        				vf.setOutAnimation(AnimationUtils.loadAnimation(NCCNDiseaseInfoActivity.this,R.anim.push_right_out));
		        				vf.showPrevious();
		        				vf.removeViewAt(vf.getChildCount() - 1);
		        			}
			            } else {
			            	if(vf.getChildCount() == 1 || vf.getCurrentView().getId() == vf.getChildCount()) {
			                	vf.clearAnimation();
			            		break;
			                }
			            	vf.setInAnimation(AnimationUtils.loadAnimation(NCCNDiseaseInfoActivity.this,R.anim.push_left_in));
							vf.setOutAnimation(AnimationUtils.loadAnimation(NCCNDiseaseInfoActivity.this,R.anim.push_left_out));
			            	showNext();
			            }
			            return true; 
		            }
		            
		            break;

		        case MotionEvent.ACTION_CANCEL:
		        case MotionEvent.ACTION_UP:
		        	if(currentText != null) {
		        		currentText.setBackgroundColor(Color.TRANSPARENT);
		        	}
		            super.onTouchEvent(ev);
		            break;
		        }

		        return false;
		}
	}
	
	/**
     * @author Rafeal Piao
     * @Description Create Title Panel
     * @param nodeID
     * @return LinearLayout
     */
    public LinearLayout createTitlePanel(String nodeID) {
    	count = count + 1;
    	LinearLayout lltTitle = new LinearLayout(this);
    	System.out.println("rgb title   "+Color.rgb(0, 85, 125));
    	lltTitle.setBackgroundColor(Color.rgb(0, 85, 125));
    	LayoutParams txtDiseaseLP = new LayoutParams(screenWidth*2/7, LayoutParams.FILL_PARENT);
    	LayoutParams indicatorLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	indicatorLP.gravity = Gravity.RIGHT;
    	
    	lltTitle.setOrientation(LinearLayout.HORIZONTAL);
    	lltTitle.setGravity(Gravity.CENTER_VERTICAL);
    	TextView txtDiseaseName = new TextView(this);
    	txtDiseaseName.setTextColor(Color.WHITE);
    	txtDiseaseName.setText(nccnAdapter.getDiseaseName(diseaseID)+" ("+nccnAdapter.getSectorName(nodeID, diseaseID)+")");
    	txtDiseaseName.setTextSize(18);
    	txtDiseaseName.setPadding(30, 0, 0, 0);
    	txtDiseaseName.setSingleLine(true);
    	txtDiseaseLP.setMargins(10, 10, 10, 10);
    	
    	RelativeLayout rltTitle = new RelativeLayout(this);
    	RelativeLayout.LayoutParams nameLP = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	nameLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	
    	RelativeLayout.LayoutParams circleLP = new RelativeLayout.LayoutParams((vf.getChildCount()+1)*indicatorSize, LayoutParams.WRAP_CONTENT);
    	circleLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    	circleLP.topMargin = 5;
    	rltTitle.addView(txtDiseaseName, nameLP);
    	rltTitle.addView(new CircleIndicator(this, vf.getChildCount()+1, scale), circleLP);
    	
    	LayoutParams rltLP = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    	rltLP.rightMargin = 10;
    	lltTitle.addView(rltTitle, rltLP);
    	return lltTitle;
    }
    
	/**
	 * @author Rafeal Piao
	 * @Description Create ViewFlipper's MainView
	 * @param diseaseName, nodeID
	 * @return LinearLayout
	 */
	public LinearLayout createVF(String diseaseName, String nodeID) {
		sPositionFlag = vf.getChildCount() + 1;
    	LinearLayout lltMain = new LinearLayout(this);
    	lltMain.setBackgroundColor(Color.rgb(199, 239, 255));
    	lltMain.setId(sPositionFlag);
    	lltMain.setOrientation(LinearLayout.VERTICAL);
    	
    	LayoutParams subSectorLP = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	subSectorLP.setMargins(0, 15, 0, 0);
    	subSectorLP.gravity = Gravity.CENTER_VERTICAL;
    	LayoutParams sectorLP = new LayoutParams(screenWidth*2/7, LayoutParams.WRAP_CONTENT);
    	LayoutParams sectorPanelLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	sectorPanelLP.gravity = Gravity.CENTER_VERTICAL;
    	
    	LinearLayout lltSector = new LinearLayout(this);
    	lltSector.setOrientation(LinearLayout.HORIZONTAL);
    	lltSector.setGravity(Gravity.CENTER_VERTICAL);
    	ScrollView svSector = new ScrollView(this);
    	svSector.setBackgroundColor(Color.rgb(149, 227, 255));
    	svSector.setVerticalScrollBarEnabled(false);
    	svSector.addView(generateVFSector(diseaseName, nodeID));
    	lltSector.addView(svSector, sectorLP);
    	
    	ScrollView sv = new ScrollView(this);
    	LinearLayout lltSV = new LinearLayout(this);
    	lltSV.setBackgroundColor(Color.rgb(199, 239, 255));
    	lltSV.setOrientation(LinearLayout.HORIZONTAL);
    	lltSV.setGravity(Gravity.CENTER_VERTICAL);
    	
    	LinearLayout lltCut = new LinearLayout(this);
    	lltCut.setBackgroundResource(R.drawable.kuo);
    	lltSV.addView(lltCut, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
    	lltSV.addView(generateVFSubSector(diseaseName, nodeID));
    	sv.addView(lltSV);
    	lltSector.addView(sv, subSectorLP);
    	
    	if(scale == 1.0) {
    		lltMain.addView(createTitlePanel(nodeID), new LayoutParams(LayoutParams.FILL_PARENT, 25));
    	} else {
    		lltMain.addView(createTitlePanel(nodeID), new LayoutParams(LayoutParams.FILL_PARENT, 32));
    	}
    	
    	lltMain.addView(lltSector, sectorPanelLP);
    	return lltMain;
	}
	
	/**
	 * @author Rafeal Piao
	 * @Description Generate ViewFlipper's Sector view from diseaseName and nodeID
	 * @param diseaseName, nodeID
	 * @return LinearLayout
	 */
	public LinearLayout generateVFSector(String diseaseName, String nodeID) {
		LinearLayout lltSector = new LinearLayout(this);
    	lltSector.setOrientation(LinearLayout.HORIZONTAL);
    	lltSector.setGravity(Gravity.CENTER_VERTICAL);
    	
    	ImageView imgLineLeft = new ImageView(this);
    	
    	LinearLayout lltSectorNameArea = new LinearLayout(this);
    	lltSectorNameArea.setOrientation(LinearLayout.VERTICAL);
    	
    	ImageView imgLineRight = new ImageView(this);
    	
    	String sector[] = replaceSup(nccnAdapter.getContentFromNode(nodeID, diseaseID)).split("\\[nn\\]");
    	for(int i=0; i<sector.length; i++) {
    		LinearLayout lltSectorNamePanel = new LinearLayout(this);
    		lltSectorNamePanel.setOrientation(LinearLayout.HORIZONTAL);
    		lltSectorNamePanel.setGravity(Gravity.TOP);
    		ImageView img = new ImageView(this);
    		img.setBackgroundResource(R.drawable.dot);
    		TextView txt = new TextView(this);
    		txt.setBackgroundResource(R.drawable.gba_list_item);
    		if(i == 0) {
//    			String strText = "("+nodeID +")"+sector[i];
//    			int index = strText.indexOf(")");
//    			SpannableString sp = new SpannableString(strText);
//    			sp.setSpan(new ForegroundColorSpan(Color.RED), 0, index+1,
//						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    			txt.setText(Html.fromHtml(sector[i]));
    		} else {
    			txt.setText(Html.fromHtml(sector[i]));
    		}
    		
    		txt.setSingleLine(false);
    		txt.setTextColor(Color.BLACK);
    		LayoutParams imgLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    		imgLp.setMargins(10, 5, 4, 0);
    		lltSectorNamePanel.addView(img, imgLp);
    		lltSectorNamePanel.addView(txt, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		lltSectorNameArea.addView(lltSectorNamePanel);
    	}
    	lltSector.addView(imgLineLeft);
    	lltSector.addView(lltSectorNameArea);
    	lltSector.addView(imgLineRight);
    	
    	return lltSector;
	}
	
	/**
	 * @author Rafeal Piao
	 * @Description Generate ViewFlipper's subSector from diseaseName and nodeID
	 * @param diseaseName, nodeID
	 * @return LinearLayout
	 */
	public LinearLayout generateVFSubSector(String diseaseName, String nodeID) {
		int marginUnit = 5;
    	
		LinearLayout lltVFSub = new LinearLayout(this);
		lltVFSub.setOrientation(LinearLayout.VERTICAL);
		
    	
    	LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
    	String sql = "select * from NCCN_Disease_relationship where [Node] ='"+nodeID+"' and Disease_ID ='"+diseaseID+"'";
    	Cursor cursor = nccnAdapter.getCursor(sql, null);
    	int noFlag = 0;
    	
    	LinearLayout lltHorizontalSector = null;
    	while(cursor.moveToNext()) {
    		if(cursor.getString(6) == null) {
    			TextView txt = new TextView(this);
    			txt.setBackgroundResource(R.drawable.gba_list_item);
        		txt.setTextColor(Color.BLACK);
    			LayoutParams txtLP = new LayoutParams(screenWidth*3/14, LayoutParams.WRAP_CONTENT);
    			LayoutParams horizontalLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    			horizontalLP.setMargins(0, 10, 10, 0);
    			lltHorizontalSector = new LinearLayout(this);
    			lltHorizontalSector.setGravity(Gravity.CENTER_VERTICAL);
    	    	lltHorizontalSector.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	LinearLayout lltVerticalSector = new LinearLayout(this);
    	    	lltVerticalSector.setOrientation(LinearLayout.VERTICAL);
    	    	
    			String subNode = cursor.getString(5);
    			String sql2 = "select * from NCCN_Disease_content where NodeID ='"+subNode+"' and DiseaseID='"+diseaseID+"'";
    			Cursor subCursor = nccnAdapter.getCursor(sql2, null);
    			noFlag = noFlag + 1;
    			while(subCursor.moveToNext()) {
    				//TODO
        			String strText = ""+noFlag +". "+subCursor.getString(3)+"<br>";
        			int index = strText.indexOf(")");
        			SpannableString sp = new SpannableString(strText);
        			sp.setSpan(new ForegroundColorSpan(Color.RED), 0, index+1,
    						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        			txt.setText(Html.fromHtml(replaceSup(strText.replace("[nn]", "<br>"))));
//    				txt.setText("("+subNode+")   "+subCursor.getString(3));
    			}
    			
    			LinearLayout lltBrace = new LinearLayout(this);
    			lltBrace.setBackgroundResource(R.drawable.kuo);
    			
    			lltHorizontalSector.addView(txt, txtLP);
    			lltHorizontalSector.addView(lltBrace, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
    			
    			String sql3 =  "select * from NCCN_Disease_relationship where [Node] ='"+subNode+"' and Disease_ID ='"+diseaseID+"'";
    			Cursor ssCursor = nccnAdapter.getCursor(sql3, null);
    			int subNoFlag = 0;
    			while(ssCursor.moveToNext()) {
    	    		TextView sstxt = new TextView(this);
    	    		txt.setBackgroundResource(R.drawable.gba_list_item);
    	    		sstxt.setTextColor(Color.BLACK);
    	    		if(ssCursor.getString(6) == null) {
    	    		} else if(ssCursor.getString(6).equals("TD")) {
    	    			String ssNode = ssCursor.getString(5);
    	    			subNoFlag = subNoFlag + 1;
    	    			String sql4 = "select * from NCCN_Disease_content where NodeID ='"+ssNode+"' and DiseaseID ='"+diseaseID+"'";
    	    			Cursor sssCursor = nccnAdapter.getCursor(sql4, null);
    	    			sstxt.setTag(ssNode);
    	    			while(sssCursor.moveToNext()) {
    	    				//TODO
//    	    				sstxt.setText("("+ssNode+")  "+sssCursor.getString(3));
    	        			String strText = ""+subNoFlag+". "+sssCursor.getString(3)+"<br>";
    	        			int index = strText.indexOf(")");
    	        			SpannableString sp = new SpannableString(strText);
    	        			sp.setSpan(new ForegroundColorSpan(Color.RED), 0, index+1,
    	    						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    	        			sstxt.setText(Html.fromHtml(replaceSup(strText.replace("[nn]", "<br>"))));
    	    			}
    	    			sssCursor.close();
    	    		} else {
    	    			String ssNode = ssCursor.getString(5);
    	    			subNoFlag = subNoFlag + 1;
    	    			String sql4 = "select * from NCCN_Disease_content where NodeID ='"+ssNode+"' and DiseaseID ='"+diseaseID+"'";
    	    			Cursor sssCursor = nccnAdapter.getCursor(sql4, null);
    	    			sstxt.setTag(ssNode);
    	    			while(sssCursor.moveToNext()) {
    	    				//TODO
//    	    				sstxt.setText("("+ssNode+")   "+sssCursor.getString(3));
    	        			String strText = ""+subNoFlag+". "+sssCursor.getString(3)+"<br>";
    	        			int index = strText.indexOf(")");
    	        			SpannableString sp = new SpannableString(strText);
    	        			sp.setSpan(new ForegroundColorSpan(Color.RED), 0, index+1,
    	    						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    	        			sstxt.setText(Html.fromHtml(replaceSup(strText.replace("[nn]", "<br>"))));
    	    			}
    	    			sstxt.setOnClickListener(this);
    	    			sssCursor.close();
    	    		}
    	    		lltVerticalSector.addView(sstxt);
    			}
    			
    			lltHorizontalSector.addView(lltVerticalSector);
    			lltVFSub.addView(lltHorizontalSector, horizontalLP);
    			ssCursor.close();
    			subCursor.close();
    		} else if(cursor.getString(6).equals("TD")) {
    			lltHorizontalSector = new LinearLayout(this);
    			lltHorizontalSector.setGravity(Gravity.CENTER_VERTICAL);
    	    	lltHorizontalSector.setOrientation(LinearLayout.HORIZONTAL);
    			TextView txt = new TextView(this);
    			txt.setBackgroundResource(R.drawable.gba_list_item);
        		txt.setTextColor(Color.BLACK);
    			lp.setMargins(0, marginUnit, 0, marginUnit);
    			String subNode = cursor.getString(5);
    			String sql2 = "select * from NCCN_Disease_content where NodeID ='"+subNode+"' and DiseaseID ='"+diseaseID+"'";
    			Cursor subCursor = nccnAdapter.getCursor(sql2, null);
    			noFlag = noFlag + 1;
    			txt.setTag(subNode);
    			while(subCursor.moveToNext()) {
    				//TODO
//    				txt.setText("("+subNode+")   "+subCursor.getString(3));
        			String strText = ""+noFlag+". "+replaceSup(subCursor.getString(3))+"<br>";
        			int index = strText.indexOf(")");
        			SpannableString sp = new SpannableString(strText);
        			sp.setSpan(new ForegroundColorSpan(Color.RED), 0, index+1,
    						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        			txt.setText(Html.fromHtml(strText.replace("[nn]", "<br>")));
    			}
    			lltHorizontalSector.addView(txt);
    			lltVFSub.addView(lltHorizontalSector, lp);
    			subCursor.close();
    		} else {
    			lltHorizontalSector = new LinearLayout(this);
    			lltHorizontalSector.setGravity(Gravity.CENTER_VERTICAL);
    	    	lltHorizontalSector.setOrientation(LinearLayout.HORIZONTAL);
    			TextView txt = new TextView(this);
    			txt.setBackgroundResource(R.drawable.gba_list_item);
        		txt.setTextColor(Color.BLACK);
    			lp.setMargins(0, marginUnit, 0, marginUnit);
    			String subNode = cursor.getString(5);
    			String sql2 = "select * from NCCN_Disease_content where NodeID ='"+subNode+"' and DiseaseID ='"+diseaseID+"'";
    			Cursor subCursor = nccnAdapter.getCursor(sql2, null);
    			noFlag = noFlag + 1;
    			txt.setTag(subNode);
    			while(subCursor.moveToNext()) {
    				//TODO
//    				txt.setText("("+subNode+")   "+subCursor.getString(3));
        			String strText = ""+noFlag+". "+replaceSup(subCursor.getString(3))+"<br>";
        			System.out.println("strText!!!!!    "+strText);
        			int index = strText.indexOf(")");
        			SpannableString sp = new SpannableString(strText);
        			sp.setSpan(new ForegroundColorSpan(Color.RED), 0, index+1,
    						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        			txt.setText(Html.fromHtml(strText.replace("[nn]", "<br>")));
    			}
    			txt.setOnClickListener(this);
    			lltHorizontalSector.addView(txt);
    			lltVFSub.addView(lltHorizontalSector, lp);
    			subCursor.close();
    		}
    	}
    	cursor.close();
    	nccnAdapter.closeDB();
    	return lltVFSub;
	}
	
	/**
	 * @param Stfing a
	 * @return Replace "¡Ä" to sup
	 */
	public String replaceSup(String a) {
		String result = null;
		int flag = 1;
		String strTmp[] = a.split("¡Ä");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<strTmp.length; i++) {
			String tmp = "";
			if(i%2 == 0) {
				tmp=strTmp[i];
			} else {
				tmp="<sup><small>"+strTmp[i]+"</small></sup>";
			}
			sb.append(tmp);
		}
		return sb.toString();
	}
}
