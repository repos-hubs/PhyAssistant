package com.jibo.activity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.weibo.net.AccessToken;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.DialogError;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

/**
 * 第三方登录OAuth2.0认证 监听器
 * @author simon
 *
 */
public class WeiboLoginListener implements WeiboDialogListener {

	private int platForm;// 类别：0代表分享，1代表关注
	private Context context;

//	private RequestListener callback = new RequestListener() {
//
//		@Override
//		public void onComplete(String response, int platForm) {
//			Toast.makeText(context,
//					R.string.createfriend_success+response, Toast.LENGTH_LONG)
//					.show();
//		}
//
//		@Override
//		public void onIOException(IOException e) {
//
//		}
//
//		@Override
//		public void onError(WeiboException e, int platForm) {
//
//		}
//	};

	public WeiboLoginListener(int platForm, Context context) {
		this.platForm = platForm;
		this.context = context;
	}

	@Override
	public void onComplete(Bundle values) {// 验证通过后回调方法
		String token = values.getString("access_token");
		String expires_in = values.getString("expires_in");
		String openId = values.getString("openid");
		String uid = values.getString("uid");

		// SharedPreferencesMgr.setAccessToken(token);
		// SharedPreferencesMgr.setExpiresIn(expires_in);

		Weibo weibo = Weibo.getInstance();
		SharedPreferencesMgr.setCurrentWeiboPlatForm(platForm);
		try {
			switch (platForm) {
			case Weibo.WEIBO_SINAL:
				initWeibo(weibo, token, expires_in,
						Constant.CONSUMER_SECRET_SINA, Weibo.WEIBO_SINAL);
				SharedPreferencesMgr.setAccessToken_SINA(token);
				SharedPreferencesMgr.setExpiresIn_SINA(expires_in);
				SharedPreferencesMgr.setUid_SINA(uid);
				SharedPreferencesMgr.setAccessToken_QQ("");
				SharedPreferencesMgr.setExpiresIn_QQ("");
				SharedPreferencesMgr.setOpenId_QQ("");
				
				((BaseActivity)context).onComplete(null, platForm);
//				getUserInfo_SINA(weibo, uid);
				break;
			case Weibo.WEIBO_QQ:
				initWeibo(weibo, token, expires_in,
						Constant.CONSUMER_SECRET_QQ, Weibo.WEIBO_QQ);
				SharedPreferencesMgr.setAccessToken_QQ(token);
				SharedPreferencesMgr.setExpiresIn_QQ(expires_in);
				SharedPreferencesMgr.setOpenId_QQ(openId);
				SharedPreferencesMgr.setAccessToken_SINA("");
				SharedPreferencesMgr.setExpiresIn_SINA("");
				SharedPreferencesMgr.setUid_SINA("");
				getUserInfo_QQ(weibo, openId);
				break;
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(DialogError e) {
		Toast.makeText(context, "Auth error : " + e.getMessage(),
				Toast.LENGTH_LONG).show();
		e.printStackTrace();
	}

	@Override
	public void onCancel() {
		Toast.makeText(context, "Auth cancel", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(
				context,
				"Auth exception : " + e.getMessage()
						+ ">>>>>>>>>errorStatusCode:" + e.getStatusCode(),
				Toast.LENGTH_LONG).show();
		e.printStackTrace();
	}

	/**
	 * 初始化OAuth信息
	 * @param weibo
	 * @param token
	 * @param expires_in
	 * @param secret
	 * @param platForm  平台
	 */
	private void initWeibo(Weibo weibo, String token, String expires_in,
			String secret, int platForm) {
		AccessToken accessToken = new AccessToken(token, secret, platForm);
		accessToken.setExpiresIn(expires_in);
		weibo.setAccessToken(accessToken);
	}

//	/**
//	 * 获取用户信息
//	 * @param weibo
//	 * @param uid 用户id
//	 * @throws WeiboException
//	 */
//	private void getUserInfo_SINA(Weibo weibo, String uid)
//			throws WeiboException {
//		WeiboParameters bundle = new WeiboParameters();
//		bundle.add("access_token", Weibo.getInstance().getAccessToken()
//				.getToken());
//		bundle.add("uid", uid);
//
//		// String url = Weibo.SERVER_SINA + "users/show.json";
//		String url = "https://api.weibo.com/2/account/profile/basic.json";
//		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
//		weiboRunner.request(context, url, bundle, Utility.HTTPMETHOD_GET,
//				(BaseActivity)context, Weibo.WEIBO_SINAL);
//	}

	/**
	 * 获取用户信息
	 * @param weibo
	 * @param openId 用户id
	 * @throws WeiboException
	 */
	private void getUserInfo_QQ(Weibo weibo, String openId)
			throws WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("oauth_consumer_key", Constant.CONSUMER_KEY_QQ);
		bundle.add("access_token", Weibo.getInstance().getAccessToken()
				.getToken());
		bundle.add("openid", openId);
		bundle.add("clientip", getIpAddress());
		bundle.add("oauth_version", "2.a");
		bundle.add("scope", "all");
		bundle.add("format", "json");

		String url = Weibo.SERVER_QQ + "user/info";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(context, url, bundle, Utility.HTTPMETHOD_POST,
				(BaseActivity)context, Weibo.WEIBO_QQ);
	}

	/**
	 * 获取ip
	 * @return
	 */
	private String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return null;
	}

}
