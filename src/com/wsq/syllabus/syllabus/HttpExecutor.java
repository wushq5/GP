package com.wsq.syllabus.syllabus;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.wsq.syllabus.util.Config;
import com.wsq.syllabus.util.PublicUitl;

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
	
	// 重定向后的sid字符串
	private String sid = null;
	
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
		params.add(new BasicNameValuePair("password", getMD5(password)));
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
	
	/**
	 * 获取重定向后的sid字段
	 * @param username
	 * @param password
	 * @param code
	 */
	public void getSid(String username, String password, String code) {
		HttpClient client = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();

		// 声明HttpPost方法，指定URI
		HttpPost httpRequest = new HttpPost(uriLogin);
		
		// 设置参数对
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("_eventId", "submit"));
		params.add(new BasicNameValuePair("gateway", "true"));
		params.add(new BasicNameValuePair("j_code", code));
		params.add(new BasicNameValuePair("lt", ""));
		params.add(new BasicNameValuePair("password", getMD5(password)));
		params.add(new BasicNameValuePair("username", username));

		try {
			httpRequest.setHeader("Cookie", "JSESSIONID="
					+ cookies.get(0).getValue());
			httpRequest.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest.setHeader("Referer", "http://uems.sysu.edu.cn/elect/");
			httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C)");
			
			// 设置实体，添加参数对
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 执行HTTP请求
			client.execute(httpRequest, httpContext);

			HttpUriRequest realRequest = (HttpUriRequest) httpContext
					.getAttribute(ExecutionContext.HTTP_REQUEST);

			// 重定向后的URI为/elect/s/types?sid=94fab0dc-b777-4f47-b728-7c8d622809da
			String redirectUri = realRequest.getURI().toString();
			// 截取sid部分
			sid = redirectUri.split("\\?")[1];
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 获取课程表信息
	 * @param year1
	 * @param year2
	 * @param term
	 * @return
	 */
	public List<StringBuffer> getTable(int year1, int year2, int term) {

		// 获取选课结果的URI
		String uriAPI = "http://uems.sysu.edu.cn/elect/s/courseAll?xnd="+year1+"-"+year2+"&xq="+term+"&"
				+ sid;

		HttpGet httpRequest3 = new HttpGet(uriAPI);

		try {
			// 使用HttpGet方法，设置Header相关信息
			httpRequest3.setHeader("Cookie", "JSESSIONID="
					+ cookies.get(0).getValue());
			httpRequest3.setHeader("Host", "uems.sysu.edu.cn");
			httpRequest3.setHeader("Referer",
					"http://uems.sysu.edu.cn/elect/s/types?" + sid);

			// 执行Get请求
			HttpResponse httpResponse2 = new DefaultHttpClient()
					.execute(httpRequest3);


			// 返回码为200即成功
			if (httpResponse2.getStatusLine().getStatusCode() == 200) {
				// 获取返回的实体
				HttpEntity entity = httpResponse2.getEntity();
				return PublicUitl.getCourse(entity);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取MD5加密后的字符串
	 * @param source
	 * @return
	 */
	public final String getMD5(String source) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = source.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
