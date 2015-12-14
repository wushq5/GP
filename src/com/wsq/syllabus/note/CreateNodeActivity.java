package com.wsq.syllabus.note;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.wsq.syllabus.R;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.util.Config;
import com.wsq.syllabus.util.PublicUitl;

@EActivity(R.layout.aty_create_note)
public class CreateNodeActivity extends Activity {

	public String ROOT_DIR;
	public String PIC_DIR;
	public String VIDEO_DIR;
	
	/** 呈现拍摄的图片 */
	@ViewById(R.id.iv_createnote_pic)
	public ImageView ivPic;
	
	/** 呈现拍摄的录像 */
	@ViewById(R.id.vv_createnote_video)
	public VideoView vvVideo;
	
	/** 文字记录 */
	@ViewById(R.id.et_createnote_txt)
	public EditText etTxt;
	
	/** 用户选择的笔记类型 */
	@Extra(Config.NOTE_TYPE)
	public int noteType = 0;

	private File photoFile;
	private File videoFile;
	
	@AfterViews
	public void init() {
		
		// 初始化文件保存路径
		this.initDir();
		
		this.takeNote();
	}
	
	/**
	 * 初始化文件保存路径
	 */
	public void initDir() {
		ROOT_DIR = getFilesDir().getAbsolutePath() + "/NoteSyllabus";
		PIC_DIR = ROOT_DIR + "/Picture/";
		VIDEO_DIR = ROOT_DIR + "/Video/";

		File rootDir = new File(ROOT_DIR);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
		
		File picDir = new File(PIC_DIR);
		if (!picDir.exists()) {
			picDir.mkdirs();
		}
		
		File videoDir = new File(VIDEO_DIR);
		if (!videoDir.exists()) {
			videoDir.mkdirs();
		}
	}
	
	/**
	 * 根据参数创建目标类型笔记
	 */
	public void takeNote() {
		switch (noteType) {
		case Config.NOTE_TYPE_TEXT:
			ivPic.setVisibility(View.GONE);
			vvVideo.setVisibility(View.GONE);
			break;

		case Config.NOTE_TYPE_IMAGE:
			ivPic.setVisibility(View.VISIBLE);
			vvVideo.setVisibility(View.GONE);
			// 调用系统摄像头拍照
			Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoFile = new File(PIC_DIR + PublicUitl.getCurrentTime("yyyyMMdd_HHmmss") + ".jpg");

			iimg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(iimg, 1);

		case Config.NOTE_TYPE_VIDEO:
			ivPic.setVisibility(View.GONE);
			vvVideo.setVisibility(View.VISIBLE);
			// 调用系统摄像头录像
			Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			videoFile = new File(VIDEO_DIR + PublicUitl.getCurrentTime("yyyyMMdd_HHmmss") + ".mp4");
			
			video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
			startActivityForResult(video, 2);
		default:
			break;
		}
	}
	
	/**
	 * 点击保存笔记
	 */
	@Click(R.id.btn_createnote_save)
	public void onBtnSaveClick() {
		NotesDBOp.storeNote(this, etTxt.getText().toString(),
				photoFile.getPath(), videoFile.getPath());
	}
	
	@Click(R.id.btn_createnote_cancel)
	public void onBtnCancelClick() {
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			Bitmap bitmap = BitmapFactory.decodeFile(photoFile
					.getAbsolutePath());
			ivPic.setImageBitmap(bitmap);
		}
		if (requestCode == 2) {
			vvVideo.setVideoURI(Uri.fromFile(videoFile));
			vvVideo.start();
		}
	}
}
