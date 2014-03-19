package com.jibo.app.research;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;

public class SpecialListActivity extends BaseSearchActivity implements OnItemClickListener{
	private String specialDir = null; // 总目录
	private String jsonFileDir = null;
	private String logoFileDir = null;
	private String jsonFilename = "index.txt";
	private String logoFilename = "index.jpg";
	
	private ImageView logoImageview = null;
	private ListView articleListview = null;
	
	private ArrayList<Object> articleList = new ArrayList<Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.research_special_list);
		super.onCreate(savedInstanceState);
		((TextView) findViewById(R.id.txt_header_title)).setText(R.string.research);
		
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		specialDir = data.getString("specialDir");
		if(specialDir != null){
			jsonFileDir = specialDir + "/" + jsonFilename;
			logoFileDir = specialDir + "/" + logoFilename;
		}
		
		inits();
	}
	
	private void inits(){
		logoImageview = (ImageView) findViewById(R.id.img_logo);
		articleListview = (ListView) findViewById(R.id.special_list);
		
		Bitmap bitmap = BitmapFactory.decodeFile(logoFileDir);
		logoImageview.setImageBitmap(bitmap);
		
		showListView();
	}
	
	private void showListView(){
		articleListview.setOnItemClickListener(this);
		JsonHandler jsonHandler = new JsonHandler();
		jsonHandler.post(new JsonParserRunnable(jsonHandler, jsonFileDir));
	}
	
	private class JsonHandler extends Handler{
        @Override  
        public void handleMessage(Message msg)   
        {  
            Bundle msgBundle=msg.getData();  
            ArrayList value = msgBundle.getParcelableArrayList("list");  
            articleList = (ArrayList<Object>) value.get(0);
            if(articleList != null && articleList.size() > 0){
            	articleListview.setAdapter(new ArticleListAdapter(SpecialListActivity.this, articleList));
            	articleListview.setOnItemClickListener(new ArticleListItemClickListener());
            }
        }  
	}
	
	private class ArticleListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String pmcId = ((HashMap<String, String>)articleList.get(arg2)).get("PmcId");
			String fileName = getNumber(pmcId) + ".xml";
			String fileDir = specialDir + "/" + pmcId + "/" + fileName;
			File file = new File(fileDir);
			if(!file.exists()) return;
			Intent intent = new Intent(SpecialListActivity.this, SpecialDetailActivity.class);
			intent.putExtra("articalHtmlDir", fileDir);
			startActivity(intent);
		}
		
		private String getNumber(String par)
		{
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(par);
			return m.replaceAll("").trim();
		}
		
	}
	
	private static class ArticleListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        private Bitmap mIcon2;
        private ArrayList<Object> list = new ArrayList<Object>();

        public ArticleListAdapter(Context context, ArrayList<Object> list) {
        	this.list = list;
            mInflater = LayoutInflater.from(context);

        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.research_special_list_item, null);

                holder = new ViewHolder();
                holder.titleText = (TextView) convertView.findViewById(R.id.ArticleTitle);
                holder.periodicalTitleText = (TextView) convertView.findViewById(R.id.PeriodicalTitle);
                holder.ifText = (TextView) convertView.findViewById(R.id.IF);
                holder.dateText = (TextView) convertView.findViewById(R.id.date);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            HashMap<String, String> info = (HashMap<String, String>) list.get(position);
            String date = info.get("PublishDate").substring(0, info.get("PublishDate").indexOf(" "));
            holder.titleText.setText(info.get("Title"));
            holder.periodicalTitleText.setText(info.get("Journal_name"));
            holder.ifText.setText(formatIF(info.get("IF")));
            holder.dateText.setText(date);

            return convertView;
        }

        static class ViewHolder {
            TextView titleText;
            TextView periodicalTitleText;
            TextView ifText;
            TextView dateText;
        }
        
        private String formatIF(String IF){
    		float x1 = 0.0f;
    		try{
    			x1 = Float.parseFloat(IF);
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    		IF = String.format("%.2f", x1);
    		return IF;
    	}
    }
	
	private class JsonParserRunnable implements Runnable{
		private Handler mHandler;
		private String jsonDir;
		private ArrayList<Object> list;
		
		public JsonParserRunnable(Handler mHandler, String jsonDir){
			this.mHandler = mHandler;
			this.jsonDir = jsonDir;
			list = new ArrayList<Object>();
		}

		@Override
		public void run() {
			list = parserJson();
			Message msg = new Message();
			Bundle data = new Bundle();
			ArrayList list1 = new ArrayList(); 
			list1.add(list);
			data.putParcelableArrayList("list", list1);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}

		private ArrayList<Object> parserJson() {
			File file = new File(jsonDir);
			if(!file.exists()) return null;
			
			String jsonStr = readFileByLines(jsonDir);
			if(TextUtils.isEmpty(jsonStr)) return null;
			
			ArrayList<Object> list = new ArrayList<Object>();
			try {  
				JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("root");   
			    for(int i = 0; i < jsonObjs.length() ; i++){   
			    	 JSONObject article = (JSONObject) jsonObjs.get(i); 
			    	 HashMap<String, String> map = new HashMap<String, String>();
			    	 map.put("IF", article.getString("IF"));
			    	 map.put("JournalId", article.getString("JournalId"));
			    	 map.put("Journal_name",  article.getString("Journal_name"));
			    	 map.put("PmcId", article.getString("PmcId"));
			    	 map.put("PublishDate", article.getString("PublishDate"));
			    	 map.put("Title", article.getString("Title"));
			    	 map.put("id", article.getString("id"));
			    	 list.add(map);
			    }
			} catch (JSONException ex) {  
			    ex.printStackTrace();
			}  
			
			return list;
		}
		
		/**
	     * 以行为单位读取文件，常用于读面向行的格式化文件
	     */
	    private String readFileByLines(String fileName) {
	    	String jsonStr = "";
	        File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            while ((tempString = reader.readLine()) != null) {
	                jsonStr += tempString;
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	        return jsonStr;
	    }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
