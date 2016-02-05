package com.wsq.syllabus.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.wsq.syllabus.R;
import com.wsq.syllabus.note.CreateNodeActivity_;
import com.wsq.syllabus.util.Config;
import com.wsq.syllabus.util.PublicUitl;

public class CreateNotePopupWin extends PopupWindow implements OnClickListener {

	private View viewParent;
	private Context context;
	
	public CreateNotePopupWin(Context context, View parent) {
		super(context);
		this.context = context;
		this.viewParent = parent;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View view = inflater.inflate(R.layout.ppw_aty_main_new_note, null);
        
        this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();  
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);
        
        // 添加点击事件
        view.findViewById(R.id.ppw_new_note_txt).setOnClickListener(this);
        view.findViewById(R.id.ppw_new_note_photo).setOnClickListener(this);
        view.findViewById(R.id.ppw_new_note_video).setOnClickListener(this);
        view.findViewById(R.id.ppw_new_note_audio).setOnClickListener(this);
	}
	
	/**
	 * 显示或隐藏弹窗
	 */
	public void showOrHideWindow() {
		if (!this.isShowing()) {
			this.showAsDropDown(viewParent, -PublicUitl.dip2px(context, 120f) / 2, 20);
		} else {
			this.dismiss();
		}
	}

	/**
	 * 按照选择的笔记类型进行相应的创建
	 */
	@Override
	public void onClick(View v) {
		int noteType = 0;
		switch (v.getId()) {
		case R.id.ppw_new_note_txt:
			noteType = Config.NOTE_TYPE_TEXT;
			showOrHideWindow();
			break;
		case R.id.ppw_new_note_photo:
			noteType = Config.NOTE_TYPE_IMAGE;
			showOrHideWindow();
			break;
		case R.id.ppw_new_note_video:
			noteType = Config.NOTE_TYPE_VIDEO;
			showOrHideWindow();
			break;
		case R.id.ppw_new_note_audio:
			noteType = Config.NOTE_TYPE_VOICE;
			showOrHideWindow();
			break;
		}
		
		Intent i = new Intent(context, CreateNodeActivity_.class);
		i.putExtra(Config.NOTE_TYPE, noteType);
		context.startActivity(i);
	}
}
