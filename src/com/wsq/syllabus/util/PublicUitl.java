package com.wsq.syllabus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;

import android.content.Context;

import com.wsq.syllabus.syllabus.Course;

public class PublicUitl {

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
	
	/**
	 * 将 "星期一"等字符串处理，返回int 1
	 * 
	 * @param day
	 * @return
	 */
	public static int getWeekday(String day) {
		int result = 0;
		day = day.substring(2);

		if (day.equals("一")) {
			result = 1;
		} else if (day.equals("二")) {
			result = 2;
		} else if (day.equals("三")) {
			result = 3;
		} else if (day.equals("四")) {
			result = 4;
		} else if (day.equals("五")) {
			result = 5;
		} else if (day.equals("六")) {
			result = 6;
		} else if (day.equals("日")) {
			result = 7;
		}

		return result;
	}
	
	/**
	 * 解析html实体，返回有关课程的部分
	 * 
	 * @param entity
	 * @return
	 */
	public static List<StringBuffer> getCourse(HttpEntity entity) {
		List<StringBuffer> courseList = new ArrayList<StringBuffer>();
		boolean isIn_tbody = false; // 仅在<tbody>内才截取相关内容
		boolean isIn_tr_class = false;

		try {
			InputStream is;
			is = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));

			String data = "";
			StringBuffer sb = new StringBuffer();
			while ((data = br.readLine()) != null) {
				// System.out.println(data);

				if (isIn_tbody) {
					if (data.contains("odd") || data.contains("even")) {
						isIn_tr_class = true;
					}

					// 在class odd或even里，添加信息
					if (isIn_tr_class) {
						if (data.length() > 0) {
							sb.append(data + "\n");
							// System.out.println(data+ "   长度为"+data.length());
						}
					}

					// 结束，完成一个课程的添加
					if (data.contains("</tr>")) {
						isIn_tr_class = false;
						courseList.add(sb);
						// 清空
						sb = new StringBuffer();
					}
				} else if (data.contains("<tbody>")) {
					System.out.println("呵呵");
					isIn_tbody = true;
				} else if (data.contains("</tbody>")) {
					break;
				}
			}
			br.close();
			is.close();

			return courseList;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 提取关键信息
	 * @param htmStr
	 * @return
	 */
	public static List<Course> get(List<StringBuffer> htmStr) {
		List<Course> result = new ArrayList<Course>();
		if (htmStr == null) {
			return result;
		}
		
		for (int j = 0; j < htmStr.size(); j++) {
			String tmp = htmStr.get(j).toString();
			String strArr[] = tmp.split("\n");
			String name = null, classroom = null, week = null, day = null, teacher = null;
			int startLesson = 0, totalLesson = 0;

			boolean lack_msg = false; // 无上课信息
			// All:
			for (int i = 0; i < strArr.length; i++) {

				if (i == 5) {
					int first = strArr[i].indexOf(">", 30);
					int last = strArr[i].indexOf("<", 30);
					name = strArr[i].substring(first + 1, last);

				}
				if (i == 10) {
					// 软件综合实验这类没有上课信息的课程
					if (strArr[i].length() < 49) {
						lack_msg = true;
						break;
					}
					day = strArr[i].substring(20, 23);
					int mark = strArr[i].indexOf("-");
					int end;
					if (strArr[i].charAt(mark - 2) != ' ') {
						startLesson = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark - 2)))
								* 10
								+ Integer.parseInt(String.valueOf(strArr[i]
										.charAt(mark - 1)));
					} else {
						startLesson = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark - 1)));
					}
					// System.out.println("文冰的函数："+startLesson);
					if (strArr[i].charAt(mark + 2) != ' ') {
						end = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark + 1)))
								* 10
								+ Integer.parseInt(String.valueOf(strArr[i]
										.charAt(mark + 2)));
					} else {
						end = Integer.parseInt(String.valueOf(strArr[i]
								.charAt(mark + 1)));
					}
					totalLesson = end - startLesson + 1;
					int placeFirst = strArr[i].indexOf("/");
					int placeLast = strArr[i].indexOf("（");
					classroom = strArr[i].substring(placeFirst + 1, placeLast);
					int weekLast = strArr[i].indexOf("）");
					week = strArr[i].substring(placeLast + 1, weekLast);
				}
				if (i == 14) {
					int firstTeacher = strArr[i].indexOf(">");
					int lastTeacher = strArr[i].indexOf("<", 19);
					teacher = strArr[i]
							.substring(firstTeacher + 1, lastTeacher);
				}

			}
			if (lack_msg) {
				continue;
			}

			Course tmpCourse = new Course(name, day, classroom, startLesson,
					totalLesson, week, teacher);
			result.add(tmpCourse);
		}

		return result;
	}
}
