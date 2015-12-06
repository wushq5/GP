package com.wsq.syllabus.syllabus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpExecutor {

	// 保存cookies
	public static List<Cookie> cookies;
	
	/** 主页uri */
	private final String uriHome = "http://uems.sysu.edu.cn/elect/index.html";
	/** 验证码uri */
	private final String uriCode = "http://uems.sysu.edu.cn/elect/login/code";
	/** 登陆uri */
	private final String uriLogin = "http://uems.sysu.edu.cn/elect/login";
	
	/** 单例对象 */
	private static final HttpExecutor INSTANCE = new HttpExecutor();
	
	/**
	 * 单例模式，私有化构造函数
	 */
	private HttpExecutor() {
		
	}
	
	/**
	 * 返回单例对象
	 * @return
	 */
	public static HttpExecutor getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 首次访问网页，先获取cookie
	 * @return
	 */
	public boolean getCookie() {
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;

		HttpPost httpRequest = new HttpPost(uriHome);
		
		try {
			// 设置实体，添加参数对
			httpRequest.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");

			// 执行HTTP请求，获取response
			httpResponse = client.execute(httpRequest);

			// 访问成功
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 获取cookie
				cookies = ((AbstractHttpClient) client).getCookieStore()
						.getCookies();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 获取验证码图片
	 * @return
	 */
	public Bitmap getCodeImg() {
		
	    HttpPost post = new HttpPost(uriCode); 
	    HttpClient client = new DefaultHttpClient(); 
	    Bitmap pic = null;
	    
	    try {
	    	post.setHeader("Cookie", "JSESSIONID=" + cookies.get(0).getValue());
	    	post.setHeader("Host", "uems.sysu.edu.cn");
	    	post.setHeader("Referer", "http://uems.sysu.edu.cn/elect/");
	    	post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");
			
	        HttpResponse response = client.execute(post);
	        
	        // 访问成功
	        if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity(); 
				InputStream is = entity.getContent(); 
				
				pic = BitmapFactory.decodeStream(is);   // 关键代码
			}

	    } catch (ClientProtocolException e) { 
	        e.printStackTrace(); 
	    } catch (IOException e) { 
	        e.printStackTrace(); 
	    }
	    
	    return pic; 
	}
	
	/**
	 * 检查是否能登陆
	 * @param sid
	 * @param password
	 * @param code
	 * @return
	 */
	public boolean checkLogin(final String sid, final String password, final String code) {
		// indicate the validity of msg that are inputed
		boolean validity = false;
		
		HttpClient client = new DefaultHttpClient();

		HttpPost httpRequest = new HttpPost(uriLogin);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("_eventId", "submit"));
		params.add(new BasicNameValuePair("gateway", "true"));
		params.add(new BasicNameValuePair("j_code", code));
		params.add(new BasicNameValuePair("lt", ""));
		params.add(new BasicNameValuePair("password", "84337FE41AD4CB4EF49363241951D491"));
		params.add(new BasicNameValuePair("username", sid));
		
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpRequest.setHeader("Cookie", "JSESSIONID="
					+ cookies.get(0).getValue());
			
			HttpResponse httpResponse = client.execute(httpRequest);

			// 返回200，说明帐号密码正确
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 释放连接
				client.getConnectionManager().shutdown();
				return validity = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 释放连接
		client.getConnectionManager().shutdown();
		
		// 帐号与密码不对应，登陆失败
		return validity;
	}
}
