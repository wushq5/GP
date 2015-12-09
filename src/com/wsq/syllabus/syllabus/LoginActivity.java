package com.wsq.syllabus.syllabus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.Preferences;

@EActivity(R.layout.aty_login)
public class LoginActivity extends Activity {

	@ViewById(R.id.et_login_account)
	public EditText etAccount;
	
	@ViewById(R.id.et_login_password)
	public EditText etPassword;
	
	@ViewById(R.id.et_login_checkcode)
	public EditText etCheckcode;
	
	@ViewById(R.id.iv_login_code)
	public ImageView ivCheckCode;
	
	@ViewById(R.id.btn_login_login)
	public Button btnLogin;

	private HttpExecutor httpExecutor = null;

	// 登陆进度对话框
	private ProgressDialog progressDialog;
	
	@AfterViews
	public void init() {
		
		// 检查是否已经登陆过，是则自动输入学号与密码
		if (Preferences.hasUser(this)) {
			Intent i = new Intent(this, ImportActivity_.class);
			startActivity(i);
			finish();
		}
		
		// 获取网络访问实体
		httpExecutor = HttpExecutor.getInstance();
		
		// 加载验证码
		this.loadCheckCode();
	}
	
	/**
	 * 点击验证码图片将刷新验证码
	 */
	@Click(R.id.iv_login_code)
	public void onIvCodeClick() {
		this.loadCheckCode();
	}
	
	/**
	 * 点击登录按钮
	 */
	@Click(R.id.btn_login_login)
	public void onBtnLoginClick() {
		// 获取输入的学号密码
		String sid = etAccount.getText().toString();
		String password = etPassword.getText().toString();
		String checkCode = etCheckcode.getText().toString();
		
		// 检查是否为空
		if (sid.length() == 0 || password.length() == 0) {
			// 弹出提示框
			this.showErrorDialog("提示", "学号密码不能为空！", false);
			return ;
		}
		
		progressDialog = ProgressDialog.show(LoginActivity.this,
				"正在登陆", "请稍后...", true, false);
		
		// 登陆验证
		this.login(sid, password, checkCode);
	}
	
	/**
	 * 加载验证码图片
	 */
	@Background
	public void loadCheckCode() {
		// 先获取cookie
		if (!httpExecutor.getCookie()) {
			this.showErrorDialog("提示", "网络初始化失败，请稍后尝试", false);
			return;
		}
		
		// 获取验证码图片
		Bitmap bitmapCode = httpExecutor.getCodeImg();
		
		// 设置验证码图片
		this.setCodeImg(bitmapCode);
	}
	
	/**
	 * 登陆
	 * @param sid
	 * @param pwd
	 * @param code
	 */
	@Background
	public void login(String sid, String pwd, String code) {
		
		boolean isLogin = httpExecutor.checkLogin(sid, pwd, code);
		
		progressDialog.dismiss();
		
		// 检查登陆是否成功
		if (isLogin == false) {
			this.showErrorDialog("提示", "学号密码或验证码错误", true);
		}
		else {
			// 存储用户学号与密码
			Preferences.storeUserMsg(LoginActivity.this, sid, pwd);
			// 跳至导入页面
			Intent i = new Intent(LoginActivity.this, ImportActivity_.class);
			startActivity(i);
			finish();
		}
	}
	
	/**
	 * 设置验证码图片
	 * @param bitmap
	 */
	@UiThread
	public void setCodeImg(Bitmap bitmap) {
		ivCheckCode.setImageBitmap(bitmap);
	}
	
	/**
	 * 显示错误信息
	 */
	@UiThread
	public void showErrorDialog(String title, String msg, final boolean refresh) {
		new AlertDialog.Builder(LoginActivity.this)
			.setTitle(title)
			.setMessage(msg)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (refresh) {
						loadCheckCode();
					}
				}
			})
			.show();
	}
}
