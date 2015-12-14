package com.wsq.syllabus.note;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.util.Config;

@EActivity(R.layout.aty_note_catalog)
public class NoteCatalogActivity extends Activity {

	@ViewById(R.id.lv_note_catalog)
	public ListView lvCatalog;
	
	@ViewById(R.id.tv_universal_top_bar_txt)
	public TextView tvTop;

	private NoteCatalogAdapter adapter;
	
	private List<NoteCatalogEntity> list;

	@AfterViews
	public void init() {
		tvTop.setText("课程笔记目录");
		
		this.getNotesCount();
	}

	/**
	 * 耗时操作，获取各课程的笔记数目
	 */
	@Background
	public void getNotesCount() {
		list = NotesDBOp.getNotesCount(this);
		
		// 设置列表视图
		this.setListView();
	}
	
	/**
	 * 设置列表视图
	 */
	@UiThread
	public void setListView() {
		adapter = new NoteCatalogAdapter(this, list);
		lvCatalog.setAdapter(adapter);
	}
	
	@ItemClick(R.id.lv_note_catalog)
	public void onItemClick(NoteCatalogEntity entity) {
		String courseName = entity.getName();
		
		Intent i = new Intent(this, NoteListActivity_.class);
		i.putExtra(Config.COURSE_NAME, courseName);
		startActivity(i);
	}
	
	@Click(R.id.iv_universal_top_bar_left)
	public void onBackClick() {
		finish();
	}
}
