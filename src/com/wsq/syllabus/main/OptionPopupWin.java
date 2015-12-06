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
import com.wsq.syllabus.syllabus.LoginActivity_;
import com.wsq.syllabus.syllabus.ManualAddActivity_;

public class OptionPopupWin extends PopupWindow implements OnClickListener {

	private View viewParent;
	
	private Context context;
	
	public OptionPopupWin(Context context, View parent) {
		super(context);
		
		this.context = context;
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
			Intent login = new Intent(context, LoginActivity_.class);
			context.startActivity(login);
			showOrHideWindow();
			break;
		case R.id.ppw_option_add:
			Intent add = new Intent(context, ManualAddActivity_.class);
			context.startActivity(add);
			showOrHideWindow();
			break;
		case R.id.ppw_option_help:
			
			showOrHideWindow();
			break;
		}
		
	}
}
