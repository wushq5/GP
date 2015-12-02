package com.wsq.syllabus.main;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wsq.syllabus.R;

@EActivity(R.layout.aty_main)
public class MainActivity extends Activity {

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
	
	/** 顶部栏选项按钮弹窗 */
	private OptionPopupWin ppwOption = null;
	/** 顶部栏新建笔记按钮弹窗 */
	private NewNotePopupWin ppwNewNote = null;
	
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
	
	/**
	 * 初始化顶部栏
	 */
	public void initTopBar() {
		this.ppwOption = new OptionPopupWin(MainActivity.this, btnOpt);
		this.ppwNewNote = new NewNotePopupWin(MainActivity.this, btnNewNote);
		
		// 标志
		ivLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo));
		ivLogo.setVisibility(View.VISIBLE);
		
		// App名称
		tvTopbarTxt.setText("东京冷");
		tvTopbarTxt.setVisibility(View.VISIBLE);
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
					tx.setBackgroundDrawable(MainActivity.this.getResources()
							.getDrawable(R.drawable.course_text_view_bg));
				else
					tx.setBackgroundDrawable(MainActivity.this.getResources()
							.getDrawable(R.drawable.course_table_last_colum));
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
}
