package com.wsq.syllabus.note;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.TextView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.NotesDB;
import com.wsq.syllabus.util.Config;

@EActivity(R.layout.aty_note_list)
public class NoteListActivity extends Activity{
	
	@Extra(Config.COURSE_NAME)
	public String curCourse = "";
	
	@ViewById(R.id.lv_note_list)
	public ListView lvNotezList;
	
	@ViewById(R.id.tv_universal_top_bar_txt)
	public TextView tvTop;

	private NoteListAdapter adapter;
	private NotesDB notesDB;
	private SQLiteDatabase dbReader;
	private Cursor cursor;
	
	@AfterViews
	public void init() {
		tvTop.setText(curCourse);
		
		// 获取数据库查询记录，设置列表内容
		notesDB = new NotesDB(this);
		dbReader = notesDB.getReadableDatabase();
		cursor = dbReader.query(curCourse, null, null, null, null, null, null);
		adapter = new NoteListAdapter(this, cursor);
		lvNotezList.setAdapter(adapter);
	}
	
	/**
	 * 点击笔记，跳转至查看笔记详情
	 * @param position
	 */
	@ItemClick(R.id.lv_note_list)
	public void onItemClick(int position) {
		cursor.moveToPosition(position);
		Intent i = new Intent(this, NoteDetailActivity_.class);
		i.putExtra(Config.COURSE_NAME, curCourse);
		i.putExtra(NotesDB.ID,
				cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
		i.putExtra(NotesDB.CONTENT, cursor.getString(cursor
				.getColumnIndex(NotesDB.CONTENT)));
		i.putExtra(NotesDB.TIME,
				cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
		i.putExtra(NotesDB.IMAGE,
				cursor.getString(cursor.getColumnIndex(NotesDB.IMAGE)));
		i.putExtra(NotesDB.VIDEO,
				cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
		startActivity(i);
	}
	
	@Click(R.id.iv_universal_top_bar_left)
	public void onBackClick() {
		finish();
	}
}
