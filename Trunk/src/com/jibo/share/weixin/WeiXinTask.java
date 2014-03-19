package com.jibo.share.weixin;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.base.ContextUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WeiXinTask {
	public static IWXAPI api;
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	static {
		if (api == null) {
			api = WXAPIFactory.createWXAPI(ContextUtil.context,
					Constants.APP_ID, false);
			api.registerApp(Constants.APP_ID);
		}
	}

	public void regHandle(WXEntryActivity acty) {
		api.handleIntent(acty.getIntent(), acty);
	}

	public static void regHandle(Intent intent, IWXAPI api,
			IWXAPIEventHandler wxapiEventHandler) {
		api.handleIntent(intent, wxapiEventHandler);
	}

	public void sendReq(String text) {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(ContextUtil.context, R.string.not_install_wx,
					Toast.LENGTH_SHORT).show();
			return;
		} else if (!api.isWXAppSupportAPI()) {
			Toast.makeText(ContextUtil.context, R.string.wx_ver_not_supported,
					Toast.LENGTH_SHORT).show();
			return;
		}
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// msg.title = "Will be ignored";
		msg.description = text;

		// 鏋勯�涓�釜Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis()); // transaction瀛楁鐢ㄤ簬鍞竴鏍囪瘑涓�釜璇锋眰
		req.message = msg;
		req.scene = 
//				SendMessageToWX.Req.WXSceneSession;
		api.getWXAppSupportAPI() >= TIMELINE_SUPPORTED_VERSION ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;

		// 璋冪敤api鎺ュ彛鍙戦�鏁版嵁鍒板井淇�
		api.sendReq(req);
	}

}
