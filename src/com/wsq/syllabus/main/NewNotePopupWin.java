package com.wsq.syllabus.main;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.wsq.syllabus.R;
import com.wsq.syllabus.util.PublicUitl;

public class NewNotePopupWin extends PopupWindow implements OnClickListener {

	private View viewParent;
	private Context context;
	
	public NewNotePopupWin(Context context, View parent) {
		super(context);
		this.context = context;
		this.viewParent = parent;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View view = inflater.inflate(R.layout.ppw_aty_main_new_note, (ViewGroup) parent.getRootView());
        
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ppw_new_note_txt:
			
			showOrHideWindow();
			break;
		case R.id.ppw_new_note_photo:
			
			showOrHideWindow();
			break;
		case R.id.ppw_new_note_video:
			
			showOrHideWindow();
			break;
		case R.id.ppw_new_note_audio:
			
			showOrHideWindow();
			break;
		}
		
	}
}
