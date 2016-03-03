package com.wsq.syllabus.note;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.NotesDB;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.util.Config;

@EActivity(R.layout.aty_note_detail)
public class NoteDetailActivity extends Activity {

	/** 从NoteListActivity传过来的数据 */
	@Extra(NotesDB.ID)
	public String mId = "";
	@Extra(NotesDB.CONTENT)
	public String mContent = "";
	@Extra(NotesDB.TIME)
	public String mTime = "";
	@Extra(NotesDB.IMAGE)
	public String mPicPath = "";
	@Extra(NotesDB.VIDEO)
	public String mVideoPath = "";
	@Extra(Config.COURSE_NAME)
	public String mCurCourse = "";
	
	@ViewById(R.id.iv_note_detail_pic)
	public ImageView ivPic;
	
	@ViewById(R.id.vv_note_detail_video)
	public VideoView vvVideo;
	
	@ViewById(R.id.tv_note_detail_content)
	public TextView tvContent;
	
	@ViewById(R.id.tv_universal_top_bar_txt)
	public TextView tvTopTitle;
	
	/** 顶部栏分享按钮 */
	@ViewById(R.id.btn_universal_top_bar_right_1)
	public Button btnShare;
	
	@SuppressLint("NewApi")
	@AfterViews
	public void init() {
		tvTopTitle.setText("笔记");
		
		btnShare.setBackground(getResources().getDrawable(R.drawable.bt_share));
		btnShare.setVisibility(View.VISIBLE);
		
		// 显示图片
		if (mPicPath != null && !mPicPath.equals("null")) {
			Bitmap bitmap = BitmapFactory.decodeFile(mPicPath);
			ivPic.setImageBitmap(bitmap);
			ivPic.setVisibility(View.VISIBLE);
		} else if (mVideoPath != null && !mVideoPath.equals("null")) {	// 播放视频
			vvVideo.setVideoURI(Uri.parse(mVideoPath));
			vvVideo.setVisibility(View.VISIBLE);
			vvVideo.start();
		}
		// 文本
		tvContent.setText(mContent);
	}
	
	
	/**
	 * 点击分享
	 */
	@Click(R.id.btn_universal_top_bar_right_1)
	public void onBtnShareClick() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		
		if (mPicPath != null && !mPicPath.equals("null")) {
			File f = new File(mPicPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/*"); // 图片
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		} else if (mVideoPath != null && !mVideoPath.equals("null")) {
			File f = new File(mVideoPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("video/*"); // 视频
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		} else {
			intent.setType("text/plain"); // 纯文本
		}
		
		intent.putExtra(Intent.EXTRA_SUBJECT, "My Note");
		intent.putExtra(Intent.EXTRA_TEXT, mContent);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(Intent.createChooser(intent, getTitle()));
	}

	/**
	 * 调用系统应用查看图片或视频
	 */
	@Click(R.id.btn_note_detail_check)
	public void onBtnCheckClick() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (vvVideo.getVisibility() == View.VISIBLE) {
			Uri uri = Uri.fromFile(new File(mVideoPath));
			intent.setDataAndType(uri, "video/*");
		} else if (ivPic.getVisibility() == View.VISIBLE) {
			Uri uri = Uri.fromFile(new File(mPicPath));
			intent.setDataAndType(uri, "image/*");
		} else {
			return;
		}
		startActivity(intent);
	}
	
	/**
	 * 点击删除
	 */
	@Click(R.id.btn_note_detail_del)
	public void onBtnDeleteClick() {
		new AlertDialog.Builder(this).setTitle("警告").setMessage("确定要删除该笔记？")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						NotesDBOp.deleteNote(NoteDetailActivity.this, mCurCourse, mId);
						finish();
					}
				}).show();
	}
	
	/**
	 * 点击视频，重播
	 */
	@Touch(R.id.vv_note_detail_video)
	public void onVideoReplayClick(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			vvVideo.start();
		}
	}
	
	@Click(R.id.iv_universal_top_bar_left)
	public void onBtnBackClick() {
		finish();
	}
}
