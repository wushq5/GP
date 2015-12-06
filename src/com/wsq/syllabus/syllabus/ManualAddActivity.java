package com.wsq.syllabus.syllabus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.CourseDBOp;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.util.TmpUitl;

@EActivity(R.layout.aty_manual_add)
public class ManualAddActivity extends Activity {

	@ViewById(R.id.et_manual_name)
	public EditText etName;
	
	@ViewById(R.id.et_manual_week)
	public EditText etWeek;
	
	@ViewById(R.id.et_manual_start)
	public EditText etStart;
	
	@ViewById(R.id.et_manual_total)
	public EditText etTotal;
	
	@ViewById(R.id.et_manual_day)
	public EditText etDay;
	
	@ViewById(R.id.et_manual_room)
	public EditText etRoom;
	
	@ViewById(R.id.et_manual_teacher)
	public EditText etTeacher;
	
	private String name = "";
	private int start = 0;
	private int total = 0;
	private String day = "";
	private String room = "";
	private String week = "";
	private String teacher = "";
	
	@AfterViews
	public void init() {
		
	}
	
	/**
	 * 点击保存
	 */
	@Click(R.id.btn_manual_save)
	public void onBtnSaveClick() {
		// 检查输入是否合法
		if (!checkInput()) {
			return;
		}

		final Course course = new Course(name, day, room, start, total, week,
				teacher);

		// 检查是否产生冲突
		if (CourseDBOp.checkConflict(this, course)) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("与已有课程产生冲突，请检查！").setPositiveButton("确定", null)
					.show();
			return ;
		}
		
		// 要弹出显示的信息
		final String msg = "课程事务：" + name + "\n"
				+ "星期：" + day + "\n"
				+ "开始节数：" + start + "\n"
				+ "总共节数：" + total + "\n"
				+ "课室：" + room + "\n"
				+ "周跨度：" + week + "\n"
				+ "讲师：" + teacher + "\n";
		
		// 合格则先进行确定，再进行存储操作
		new AlertDialog.Builder(ManualAddActivity.this)
				.setTitle("确认添加")
				.setMessage(msg)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								CourseDBOp.storeSingleCourse(ManualAddActivity.this, course);
								NotesDBOp.createNoteTable(ManualAddActivity.this);
								finish();
							}
						}).setNegativeButton("取消", null).show();

	}
	
	/**
	 * 点击取消
	 */
	@Click(R.id.btn_manual_cancel)
	public void onBtnCancelClick() {
		finish();
	}
	
	/**
	 * 检查输入是否合法
	 */
	public boolean checkInput() {
		boolean legality = true;
		String tmp;

		// 检查课程名称，非空即可
		tmp = etName.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("课程名称不可为空！").setPositiveButton("确定", null)
					.show();
			return legality = false;
		}
		name = tmp;

		// 检查开始节号，必须为1-15的数字
		tmp = etStart.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0 || Integer.parseInt(tmp) > 15
				|| Integer.parseInt(tmp) < 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("开始节数应为1到15之间的数字！")
					.setPositiveButton("确定", null).show();
			return legality = false;
		}
		start = Integer.parseInt(tmp);

		// 检查总共节数，必须为1-15的数字
		tmp = etTotal.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0 || Integer.parseInt(tmp) > 15
				|| Integer.parseInt(tmp) < 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("总共节数应为1到15之间的数字！")
					.setPositiveButton("确定", null).show();
			return legality = false;
		}
		total = Integer.parseInt(tmp);

		// 检查每一天的课，必须为1-7的数字
		tmp = etDay.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0 || Integer.parseInt(tmp) > 7
				|| Integer.parseInt(tmp) < 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("星期应为1到7之间的数字！").setPositiveButton("确定", null)
					.show();
			return legality = false;
		}
		day = TmpUitl.getWeekday(Integer.parseInt(tmp));

		// 检查课室，非空即可
		tmp = etRoom.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("课室不可为空！")
					.setPositiveButton("确定", null).show();
			return legality = false;
		}
		room = tmp;

		// 检查持续周数，非空即可
		tmp = etWeek.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("持续周数不可为空！").setPositiveButton("确定", null)
					.show();
			return legality = false;
		}
		week = tmp;

		// 检查讲师名称，非空即可
		tmp = etTeacher.getText().toString();
		if (tmp.equals(null) || tmp.length() == 0) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("讲师名称不可为空！").setPositiveButton("确定", null)
					.show();
			return legality = false;
		}
		teacher = tmp;

		// 检查节数是否超出范围
		if (start + total - 1 > 15) {
			return legality = false;
		}

		return legality;
	}
}
