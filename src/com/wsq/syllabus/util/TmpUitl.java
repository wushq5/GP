package com.wsq.syllabus.util;

import java.io.File;

import android.content.Context;

public class TmpUitl {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 删除指定文件
	 * 
	 * @param uri 文件URI
	 */
	public static void deleteFile(String uri) {

		if (uri.length() != 0) {
			File f = new File(uri);
			f.delete();
		}
	}
	
	/**
	 * 将数字1-7转化为星期一到星期日
	 * 
	 * @param day
	 * @return
	 */
	public static String getWeekday(int day) {
		String weekday = null;
		switch (day) {
		case 1:
			weekday = "星期一";
			break;
		case 2:
			weekday = "星期二";
			break;
		case 3:
			weekday = "星期三";
			break;
		case 4:
			weekday = "星期四";
			break;
		case 5:
			weekday = "星期五";
			break;
		case 6:
			weekday = "星期六";
			break;
		case 7:
			weekday = "星期日";
			break;
		}
		return weekday;
	}
}
