package com.wsq.syllabus.note;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.wsq.syllabus.R;
import com.wsq.syllabus.data.NotesDBOp;
import com.wsq.syllabus.util.Config;
import com.wsq.syllabus.util.JsonParser;
import com.wsq.syllabus.util.PublicUitl;

@EActivity(R.layout.aty_create_note)
public class CreateNodeActivity extends Activity {

	private static String TAG = CreateNodeActivity.class.getSimpleName();
	
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

	private File rootDir;
	private File picDir;
	private File videoDir;
	
	private File photoFile;
	private File videoFile;
	
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	private Toast mToast;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	
	@AfterViews
	public void init() {
		
		// 初始化文件保存路径
		this.initDir();
		
		// 初始化讯飞语音相关组件
		this.initXunfei();
		
		this.takeNote();
	}
	
	/**
	 * 初始化文件保存路径
	 */
	public void initDir() {
		// 若插入sdcard，则在sdcard内创建新目录
		if (PublicUitl.hasSdcard()) {
			ROOT_DIR = Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/Syllabus";
		}
		// 否则在data目录下创建新目录
		else {
			ROOT_DIR = getFilesDir().getAbsolutePath() + "/Syllabus";
		}
		
		PIC_DIR = ROOT_DIR + "/Pictures";
		VIDEO_DIR = ROOT_DIR + "/Videos";

		rootDir = new File(ROOT_DIR);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
		
		picDir = new File(PIC_DIR);
		if (!picDir.exists()) {
			picDir.mkdirs();
		}
		
		videoDir = new File(VIDEO_DIR);
		if (!videoDir.exists()) {
			videoDir.mkdirs();
		}
	}
	
	/**
	 * 初始化讯飞语音相关组件
	 */
	public void initXunfei() {
		SpeechUtility.createUtility(this, SpeechConstant.APPID+"=5670327f");
		
		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
		mIatDialog = new RecognizerDialog(CreateNodeActivity.this, null);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
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
			photoFile = new File(picDir.getAbsoluteFile() + "/" + PublicUitl.getCurrentTime("yyyyMMdd_HHmmss") + ".jpg");

			iimg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(iimg, 1);
			break;
		case Config.NOTE_TYPE_VIDEO:
			ivPic.setVisibility(View.GONE);
			vvVideo.setVisibility(View.VISIBLE);
			// 调用系统摄像头录像
			Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			videoFile = new File(videoDir.getAbsoluteFile() + "/" + PublicUitl.getCurrentTime("yyyyMMdd_HHmmss") + ".mp4");
			
			video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
			startActivityForResult(video, 2);
			break;
		
		case Config.NOTE_TYPE_VOICE:
			setParam();
			
			// 显示听写对话框
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			break;
			
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
				photoFile + "", videoFile + "");
		finish();
	}
	
	@Click(R.id.btn_createnote_cancel)
	public void onBtnCancelClick() {
		finish();
	}
	
	@Click(R.id.iv_universal_top_bar_left)
	public void OnBtnBackClick() {
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
	
	//-----------------------------下面主要为讯飞语音SDK相关代码---------------------------------------//
	
	
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			
			printResult(results);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	
	/**
	 * 设置听写参数
	 */
	private void setParam() {
		// 设置accent、language等参数
		mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
		// 设置听写引擎
		mIatDialog.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIatDialog.setParameter(SpeechConstant.VAD_BOS, "4000");
		
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIatDialog.setParameter(SpeechConstant.VAD_EOS, "1000");
		
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIatDialog.setParameter(SpeechConstant.ASR_PTT, "1");
	}
	
	/**
	 * 输出听写结果
	 * @param results
	 */
	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		etTxt.setText(resultBuffer.toString());
		etTxt.setSelection(etTxt.length());
	}
	
	/**
	 * Toast显示函数
	 * @param str
	 */
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	
}
