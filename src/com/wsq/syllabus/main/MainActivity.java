package com.wsq.syllabus.main;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.CourseDB;
import com.wsq.syllabus.data.CourseDBOp;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.note.NoteCatalogActivity_;
import com.wsq.syllabus.syllabus.Course;
import com.wsq.syllabus.syllabus.LoginActivity_;
import com.wsq.syllabus.util.Config;
import com.wsq.syllabus.util.PublicUitl;

@EActivity(R.layout.aty_main)
public class MainActivity extends Activity implements OnClickListener {

	/** 
	 * 第一行空白格以及星期一到星期日的格子
	 */
	@ViewById(R.id.tv_empty)
	public TextView empty;
	@ViewById(R.id.tv_monday)
	public TextView tvMon;
	@ViewById(R.id.tv_tuesday)
	public TextView tvTue;
	@ViewById(R.id.tv_wednesday)
	public TextView tvWed;
	@ViewById(R.id.tv_thursday)
	public TextView tvThur;
	@ViewById(R.id.tv_friday)
	public TextView tvFri;
	@ViewById(R.id.tv_saturday)
	public TextView tvSat;
	@ViewById(R.id.tv_sunday)
	public TextView tvSun;
	
	/** 课程表主体布局 */
	@ViewById(R.id.rl_syllabus)
	public RelativeLayout rlSyllabus;
	
	/** 顶部栏相关控件 */
	@ViewById(R.id.iv_universal_top_bar_left)
	public ImageView ivLogo;
	@ViewById(R.id.tv_universal_top_bar_txt)
	public TextView tvTopbarTxt;
	@ViewById(R.id.btn_universal_top_bar_right_1)
	public Button btnOpt;
	@ViewById(R.id.btn_universal_top_bar_right_2)
	public Button btnNewNote;
	@ViewById(R.id.btn_universal_top_bar_right_3)
	public Button btnNoteList;
	
	/** 顶部栏选项按钮弹窗 */
	private OptionPopupWin ppwOption = null;
	/** 顶部栏新建笔记按钮弹窗 */
	private CreateNotePopupWin ppwNewNote = null;
	
	/** 屏幕宽度 */ 
	private int screenWidth;
	/** 课程格子宽度 */
	private int gridWidth;
	/** 课程格子高度 */
	private int gridHeight;
	
	@AfterViews
	public void init() {
		
		// 初始化顶部栏
		this.initTopBar();
		
		// 初始化课程表格子视图
		this.initGridView();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 载入课程
		this.loadCourses();
	}
	
	/**
	 * 初始化顶部栏
	 */
	public void initTopBar() {
		this.ppwOption = new OptionPopupWin(MainActivity.this, btnOpt);
		this.ppwNewNote = new CreateNotePopupWin(MainActivity.this, btnNewNote);
		
		btnNewNote.setVisibility(View.VISIBLE);
		btnOpt.setVisibility(View.VISIBLE);
		btnNoteList.setVisibility(View.VISIBLE);
		
		// 标志
		ivLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
		
		// App名称
		tvTopbarTxt.setText("东京冷");
	}
	
	/**
	 * 初始化课程表视图
	 */
	public void initGridView() {

		// 获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 屏幕宽度
		this.screenWidth = dm.widthPixels;
		// 平均宽度
		this.gridWidth = screenWidth / 8;
		// 格子高度
		this.gridHeight = dm.heightPixels / 10;
		
		empty.setWidth(gridWidth * 3 / 4);
		tvMon.setWidth(gridWidth * 33 / 32 + 1);
		tvTue.setWidth(gridWidth * 33 / 32 + 1);
		tvWed.setWidth(gridWidth * 33 / 32 + 1);
		tvThur.setWidth(gridWidth * 33 / 32 + 1);
		tvFri.setWidth(gridWidth * 33 / 32 + 1);
		tvSat.setWidth(gridWidth * 33 / 32 + 1);
		tvSun.setWidth(gridWidth * 33 / 32 + 1);

		// 设置课表界面
		// 动态生成15 * maxCourseNum个textview
		for (int i = 1; i <= 15; i++) {
			for (int j = 1; j <= 8; j++) {
				TextView tx = new TextView(MainActivity.this);
				tx.setId((i - 1) * 8 + j);
				// 除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
				if (j < 8)
					tx.setBackgroundDrawable(getResources().getDrawable(R.drawable.course_text_view_bg));
				else
					tx.setBackgroundDrawable(getResources().getDrawable(R.drawable.course_table_last_colum));
				// 相对布局参数
				RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
						gridWidth * 33 / 32 + 1, gridHeight);
				// 文字对齐方式
				tx.setGravity(Gravity.CENTER);
				// 字体样式
				tx.setTextAppearance(this, R.style.syllabusTxt);
				// 如果是第一列，需要设置课的序号（1 到 12）
				if (j == 1) {
					tx.setText(String.valueOf(i));
					rp.width = gridWidth * 3 / 4;
					// 设置他们的相对位置
					if (i == 1)
						rp.addRule(RelativeLayout.BELOW, empty.getId());
					else
						rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
				} else {
					rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
					rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
					tx.setText("");
				}

				tx.setLayoutParams(rp);
				rlSyllabus.addView(tx);
			}
		}
	}
	
	/**
	 * 载入课程表
	 */
	public void loadCourses() {
		List<Course> courses = CourseDBOp.loadCourses(this);

		// 数据库中不含任何内容
		if (courses.size() == 0) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("通知")
					.setMessage("当前无课程，是否进行导入?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Intent i = new Intent(MainActivity.this, LoginActivity_.class);
									startActivity(i);
								}
							}).setNegativeButton("取消", null).show();
		}
		// 数据库中已有课程信息
		else {
			// 将课程表绘制到UI
			addCourses2View(courses);
		}
	}
	
	/**
	 * 将课程表信息添加到主界面
	 * @param courses
	 */
	public void addCourses2View(List<Course> courses) {
		String courseName = "";
		int startLesson = 0;
		int totalLesson = 0;
		String classroom = "";
		int day = 0;

		for (int i = 0; i < courses.size(); i++) {
			courseName = courses.get(i).getName();
			startLesson = courses.get(i).getStartLesson();
			totalLesson = courses.get(i).getTotalLesson();
			classroom = courses.get(i).getClassroom();
			day = PublicUitl.getWeekday(courses.get(i).getDay());

			// 添加课程信息
			TextView courseInfo = new TextView(this);
			courseInfo.setText(courseName + "\n@" + classroom);
			// 该textview的高度根据其节数的跨度来设置
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					gridWidth * 33 / 32 - 6, gridHeight * totalLesson - 6);
			// textview的位置由课程开始节数和上课的时间（day of week）确定
			rlp.topMargin = (startLesson - 1) * gridHeight + 2;
			rlp.leftMargin = 2;
			
			// 偏移由这节课是星期几决定
			rlp.addRule(RelativeLayout.RIGHT_OF, day);
			// 字体居中
			courseInfo.setGravity(Gravity.CENTER);
			// 设置随机背景颜色
			courseInfo.setBackgroundResource(Config.BACKGROUND_COLOR[i % 7]);
			courseInfo.setTextSize(11);
			courseInfo.setLayoutParams(rlp);
			courseInfo.setTextColor(Color.WHITE);
			// 设置不透明度
			courseInfo.getBackground().setAlpha(255);
			rlSyllabus.addView(courseInfo);
			
			// 设置点击事件
			courseInfo.setOnClickListener(this);
		}
	}

	/**
	 * 点击选项
	 */
	@Click(R.id.btn_universal_top_bar_right_1)
	public void onBtnOptionClick() {
		ppwOption.showOrHideWindow();
	}
	
	/**
	 * 点击新建笔记
	 */
	@Click(R.id.btn_universal_top_bar_right_2)
	public void onBtnNewNoteClick() {
		ppwNewNote.showOrHideWindow();
	}
	
	/**
	 * 点击查看笔记列表
	 */
	@Click(R.id.btn_universal_top_bar_right_3)
	public void onBtnNoteListClick() {
		Intent i = new Intent(this, NoteCatalogActivity_.class);
		startActivity(i);
	}

	/**
	 * 处理课程表的点击事件
	 */
	@Override
	public void onClick(final View view) {
		// 获取课程信息，如“软件测试技术@C304”
		String tmp = ((TextView) view).getText().toString();
		// 切割字符串
		tmp = tmp.split("\n")[0];
		// 查询数据库，获取课程信息
		ArrayList<Course> courses = CourseDBOp.queryCourses(this,
				CourseDB.COURSE_NAME + "=?", new String[] { tmp });
		final Course course = courses.get(0);
		courses = null;
		
		
		// 要弹出显示的信息
		final String msg = "课程事务: " + course.getName() + "\n"
				+ "星期: " + course.getDay() + "\n"
				+ "节数: " + course.getStartLesson() + "-" + 
					(course.getStartLesson()  + course.getTotalLesson() - 1) + "\n"
				+ "课室: " + course.getClassroom() + "\n"
				+ "周跨度：" + course.getWeek() + "\n"
				+ "讲师: " + course.getTeacher() + "\n";
		
		new AlertDialog.Builder(this).setTitle("课程信息").setMessage(msg)
			.setPositiveButton("确定", null)
			.setNegativeButton("删除", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("警告")
							.setMessage("该操作将删除该课程与其相关笔记，是否继续?")
							.setNegativeButton("取消", null)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// 删除课程，包括笔记
							NotesDBOp.delSingleCourseNote(MainActivity.this, course.getName());
							CourseDBOp.deleteSingleCourse(MainActivity.this, course.getName());
							rlSyllabus.removeView((TextView) view);
							rlSyllabus.invalidate();
						}
					}).show();
				}
			}).show();
	}
}
