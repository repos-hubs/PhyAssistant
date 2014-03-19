/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.jibo.base.src.request.impl.soap;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;
import android.database.Cursor;

import com.jibo.base.src.request.CursorParser;
import com.jibo.base.src.request.CursorParser.CursorResult;
import com.jibo.base.src.request.impl.db.SQLiteAdapter;
import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.common.SoapRes;
import com.jibo.util.JiBoException;
import com.jibo.util.Logs;

/**
  * soap 数据请求类
  * @author peter.pan
 * */
public class AsyncRequest implements Runnable {
	public final static int VISIT_TYPE_SOAP = 0;
	public final static int VISIT_TYPE_LOCAL_DB = 1;
	/**请求的url*/
    private final String url;
    /**请求的方法Id*/
    private final int methodId;
    /**请求的方法*/
    private String method;
    /**请求的结果的处理类*/
    private final AsyncResponseHandler responseHandler;
    /**请求传递的参数*/
    private final Properties propertyInfos; 
    /**Soap的名称空间*/
    private static String NAME_SPACE=SoapRes.NAMESPACE;
    public int visitType = AsyncRequest.VISIT_TYPE_SOAP;
    
    /**
     * 必须首先设置名称空间
     * @param name 名称空间
     * */
    public static void setNameSpace(String name)
    {
    	NAME_SPACE=name;
    }
    /**
     * @param url 请求的url
     * @param methodID 请求的方法Id
     * @param propertyInfos 请求传递的参数
     * @param responseHandler 请求的结果的处理类
     * */
    public AsyncRequest(String url,  int methodID,Properties propertyInfos,AsyncResponseHandler responseHandler,int visitType) {
        this.url = url;
        this.methodId = methodID;
        method = SoapRes.getMethod(methodId);
        this.propertyInfos =propertyInfos;
        this.responseHandler = responseHandler;
        this.visitType = visitType;
    }
	public void run() {
    	Exception ex =null;

        try {
        	if(NAME_SPACE== null)
        	{
        		System.out.println(AsyncRequest.class.getName()+" no name space");
        		return;
        	}
            if(responseHandler != null){
                responseHandler.sendStartMessage();
            }
            if(this.visitType == AsyncRequest.VISIT_TYPE_LOCAL_DB)
            	makeLocalDBRequest();
            else if(this.visitType == AsyncRequest.VISIT_TYPE_SOAP){            	
            	makeSoapRequest();
            }else{
            	throw new IllegalStateException("unsupported visit type");
            }
        } 
        catch (SocketTimeoutException e)
        {
        	ex=new JiBoException(e,JiBoException.ERR_TIMEOUT);
        }
        catch (XmlPullParserException e) {
			//e.printStackTrace();
        	ex=new JiBoException(e,JiBoException.ERR_SOAP_PASER);
		} 
        catch (IOException e) 
        {
        	ex=new JiBoException(e,JiBoException.ERR_SOAP_IO);
        }
        catch (Exception e) {
            ex=new JiBoException(e);
        }
        finally
        {
        	if(ex!=null)
        		responseHandler.sendFailureMessage(ex, null);
        	if(responseHandler != null) 
        	{
        		 responseHandler.sendFinishMessage();
        	}
        }
    }
    
    /**
     * 发起请求
     * */
    private void makeSoapRequest() throws Exception{
    	
    	if(!Thread.currentThread().isInterrupted()) {
    		// soap request
//    		Log.i("lushan", (System.currentTimeMillis()-NewsGetInfoByIdPaser.mill)+"");
//    		NewsGetInfoByIdPaser.mill = System.currentTimeMillis();    		
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			SoapObject soapObject = new SoapObject(NAME_SPACE,
					method);
			if(propertyInfos!=null)
			{
				Iterator<Entry<Object, Object>> it=propertyInfos.entrySet().iterator();
				Entry<Object, Object> entry=null;
				while(it.hasNext())
				{
					entry =it.next();
					soapObject.addProperty(entry.getKey().toString(),entry.getValue().toString());
				}
			}
										
			AndroidHttpTransport ht = new AndroidHttpTransport(url);
			envelope.bodyOut = soapObject;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(soapObject);
			//System.out.println("NAME_SPACE + url="+url +"   ;method="+method);
			System.out.println("urlddd   "+NAME_SPACE + method);
			long a;
			long b;
			Logs.i("" + (a = System.currentTimeMillis()));
			ht.call(NAME_SPACE + method, envelope);
			Logs.i(""+(Logs.a = b = System.currentTimeMillis()));
			Logs.i(" b-a " +(b -a)/1000L+" "+NAME_SPACE + method);
			Logs.i(" b-a ==== "+System.currentTimeMillis()/1000);
			if(Thread.currentThread().isInterrupted()) {
    			//用户取消
    			
    		} else{
//    			Log.i("lushan", (System.currentTimeMillis()-NewsGetInfoByIdPaser.mill)+"");
//        		NewsGetInfoByIdPaser.mill = System.currentTimeMillis();
    			if(envelope.getResponse()!=null)
    			{
    				//数据请求成功
    				responseHandler.sendResponseMessage(methodId,envelope);  			
    			}
    		}
    	}
    }
    private void makeLocalDBRequest() throws Exception{
    	
    	if(!Thread.currentThread().isInterrupted()) {
    		String dbname = url;
    		SQLiteAdapter sAdapter = SqliteAdapterCentre.getInstance().get(dbname);
    		
    		CursorResult cursorResult = null;	
    		String sql = "";
			if(propertyInfos!=null)
			{
				String tag = url+"@";
				Object obj;
				if((obj = propertyInfos.get("sql"))==null){
					if((obj = propertyInfos.get("table"))==null){
						throw new IllegalArgumentException("empty args in DBInfo");
					}else
					sql = "select * from "+obj.toString();
				}else{
					sql = obj.toString();
				}
				tag +=obj.toString();
				Cursor cursor = sAdapter.getCursor(sql,null);
				cursorResult = new CursorResult();
				cursorResult.setMetaResult(cursor);
				cursorResult.setTag(tag);
				Logs.i("--- tag "+tag);
			}
			
			if(Thread.currentThread().isInterrupted()) {
    			//用户取消
    		} else{
    			if(cursorResult!=null)
    			{
    				//数据请求成功
    				responseHandler.sendResponseMessage(methodId,cursorResult);  			
    			}
    		}
    	}
    }
 
}