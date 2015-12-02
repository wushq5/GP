package com.wsq.syllabus.main;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.wsq.syllabus.R;

public class OptionPopupWin extends PopupWindow implements OnClickListener {

	private View viewParent;
	
	public OptionPopupWin(Context context, View parent) {
		super(context);
		
		this.viewParent = parent;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View view = inflater.inflate(R.layout.ppw_aty_main_option, null);
        
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
        view.findViewById(R.id.ppw_option_import).setOnClickListener(this);
        view.findViewById(R.id.ppw_option_add).setOnClickListener(this);
        view.findViewById(R.id.ppw_option_help).setOnClickListener(this);
	}
	
	/**
	 * 显示或隐藏弹窗
	 */
	public void showOrHideWindow() {
		if (!this.isShowing()) {
			this.showAsDropDown(viewParent, 0, 20);
		} else {
			this.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ppw_option_import:
			
			showOrHideWindow();
			break;
		case R.id.ppw_option_add:
			
			showOrHideWindow();
			break;
		case R.id.ppw_option_help:
			
			showOrHideWindow();
			break;
		}
		
	}
}
