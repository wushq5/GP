package com.wsq.syllabus.syllabus;

import java.util.List;

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
import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.CourseDBOp;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.data.Preferences;
import com.wsq.syllabus.util.PublicUitl;

@EActivity(R.layout.aty_import)
public class ImportActivity extends Activity implements OnValueChangeListener{

	@ViewById(R.id.np_import_year1)
	public NumberPicker npYear1;
	
	@ViewById(R.id.np_import_year2)
	public NumberPicker npYear2;
	
	@ViewById(R.id.np_import_term)
	public NumberPicker npTerm;
	
	@ViewById(R.id.et_import_code)
	public EditText etCode;

	@ViewById(R.id.iv_import_code)
	public ImageView ivCode;
	
	/**
	 * 顶部栏控件
	 */
	@ViewById(R.id.tv_universal_top_bar_txt)
	public TextView tvTopTxt;
	@ViewById(R.id.iv_universal_top_bar_left)
	public ImageView ivTopPic;
	
	// 网络访问执行器
	private HttpExecutor httpExecutor = null;

	private int year1 = 2010, year2 = 2010;
	private int term = 1;
	private String code = "";

	// 联网读取课程表进度对话框
	private ProgressDialog progressDialog;
	
	@AfterViews
	public void init() {
		tvTopTxt.setText("导入课表");
		
		npYear1.setOnValueChangedListener(this);
		npYear2.setOnValueChangedListener(this);
		npTerm.setOnValueChangedListener(this);
		
		npYear1.setMinValue(2010);
		npYear1.setMaxValue(2050);
		npYear2.setMinValue(2010);
		npYear2.setMaxValue(2050);
		npTerm.setMinValue(1);
		npTerm.setMaxValue(3);
		
		httpExecutor = HttpExecutor.getInstance();
		
		// 载入验证码
		this.loadCheckCode();
	}
	
	/**
	 * 点击确认导入
	 */
	@Click(R.id.btn_import_ok)
	public void onBtnImportClick() {
		// 检查学年度是否合法
		if (year2 - year1 != 1) {
			this.showErrorDialog("提示", "学年度选择不规范", false);
		}
		else {
			code = etCode.getText().toString();
			// 检查验证码是否为空
			if (code.length() == 0) {
				this.showErrorDialog("提示", "验证码不可为空", false);
				return ;
			}
			
			// 提示进度
			progressDialog = ProgressDialog.show(ImportActivity.this,
					"正在读取课表信息", "请稍后...", true, false);
			
			importAndStore(year1, year2, term, code);
		}
	}
	
	/**
	 * 点击返回
	 */
	@Click(R.id.iv_universal_top_bar_left)
	public void onBackClick() {
		finish();
	}
	
	/**
	 * 导入课程表，储存在数据库
	 * @param year1
	 * @param year2
	 * @param term
	 * @param code
	 */
	@Background
	public void importAndStore(final int year1, final int year2,
			final int term, final String code) {
		// 获取储存的账号密码
		String[] msg = Preferences.getUserMsg(ImportActivity.this);

		// 获取sid
		httpExecutor.getSid(msg[0], msg[1], code);
		// 获取原始html文本
		List<StringBuffer> list = httpExecutor.getTable(year1, year2, term);
		// 解析html文本
		List<Course> courses = PublicUitl.get(list);
		
		// 进度取消
		dismiss();
		
		// 导入成功
		if (courses.size() != 0) {
			// 存储在数据库
			CourseDBOp.storeCourses(ImportActivity.this, courses);
			// 为各个课程创建笔记Table
			NotesDBOp.createNoteTable(ImportActivity.this);
			
			// 结束当前活动，返回主界面
			finish();
		}
		// 导入失败
		else {
			showErrorDialog("提示", "导入失败", true);
		}
	}
	
	@Override
	public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
		int id = arg0.getId();
		switch (id) {
		case R.id.np_import_year1:
			year1 = newVal;
			break;
		case R.id.np_import_year2:
			year2 = newVal;
			break;
		case R.id.np_import_term:
			term = newVal;
			break;
		}
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
	 * 设置验证码图片
	 * @param bitmap
	 */
	@UiThread
	public void setCodeImg(Bitmap bitmap) {
		ivCode.setImageBitmap(bitmap);
	}
	
	/**
	 * 显示错误信息
	 */
	@UiThread
	public void showErrorDialog(String title, String msg, final boolean refresh) {
		new AlertDialog.Builder(ImportActivity.this)
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
	
	/**
	 * 取消进度框
	 */
	@UiThread
	public void dismiss() {
		progressDialog.dismiss();
	}
}
