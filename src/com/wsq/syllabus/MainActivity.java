package com.wsq.syllabus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

@EActivity(R.layout.aty_main)
public class MainActivity extends Activity {

	/** 
	 * 第一行星期一到星期日的格子以及空白格
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
	
	/** 屏幕宽度 */ 
	private int screenWidth;
	/** 课程格子宽度 */
	private int gridWidth;
	/** 课程格子高度 */
	private int gridHeight;
	
	@AfterViews
	public void init() {
		
		// 初始化课程表格子视图
		this.initGridView();
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
}
