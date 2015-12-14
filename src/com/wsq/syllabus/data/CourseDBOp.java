package com.wsq.syllabus.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wsq.syllabus.syllabus.Course;
import com.wsq.syllabus.util.Config;

public class CourseDBOp {
	
	/**
	 * 存储课程表信息
	 * @param context
	 * @param courses
	 */
	public static void storeCourses(Context context, List<Course> courses) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();
		
		for (int i = 0; i < courses.size(); i++) {
			ContentValues values = new ContentValues();
			values.put(CourseDB.COURSE_NAME, courses.get(i).getName());
			values.put(CourseDB.DAY, courses.get(i).getDay());
			values.put(CourseDB.CLASSROOM, courses.get(i).getClassroom());
			values.put(CourseDB.START_LESSON, courses.get(i).getStartLesson());
			values.put(CourseDB.TOTAL_LESSON, courses.get(i).getTotalLesson());
			values.put(CourseDB.WEEK, courses.get(i).getWeek());
			values.put(CourseDB.TEACHER, courses.get(i).getTeacher());
			
			dbWriter.insert(CourseDB.TABLE_NAME, null, values);
			values.clear();
		}
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 载入课程表信息
	 * @param context
	 * @return
	 */
	public static List<Course> loadCourses(Context context) {
		List<Course> courses = new ArrayList<Course>();
		
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbReader = courseDB.getReadableDatabase();
		
		Cursor cursor = dbReader.query(CourseDB.TABLE_NAME, null, null, null, null, null, null);

		// 没有课程信息
		if (cursor.getCount() == 0) {
			cursor.close();
			dbReader.close();
			courseDB.close();
			return courses;
		}
		
		String courseName = "";
		int startLesson = 0;
		int totalLesson = 0;
		String classroom = "";
		String day = "";
		String week = "";
		String teacher = "";
		
		cursor.moveToFirst();  
		while (!cursor.isAfterLast()) {
			// 获取各字段名称
		    courseName = cursor.getString(cursor.getColumnIndex(CourseDB.COURSE_NAME));
		    startLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.START_LESSON));
		    totalLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.TOTAL_LESSON));
		    classroom = cursor.getString(cursor.getColumnIndex(CourseDB.CLASSROOM));
		    day = cursor.getString(cursor.getColumnIndex(CourseDB.DAY));
		    week = cursor.getString(cursor.getColumnIndex(CourseDB.WEEK));
		    teacher = cursor.getString(cursor.getColumnIndex(CourseDB.TEACHER));
		    
		    Course tmp = new Course(courseName, day, classroom, startLesson, totalLesson, week, teacher);
		    courses.add(tmp);
		    
		    cursor.moveToNext();  
		}
		// 关闭
		cursor.close();
		dbReader.close();
		courseDB.close();
		
		return courses;
	}
	
	/**
	 * 按照指定条件查询课表数据
	 * @param context
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static ArrayList<Course> queryCourses(Context context,
			String selection, String[] selectionArgs) {
		
		ArrayList<Course> courses = new ArrayList<Course>();
		
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbReader = courseDB.getReadableDatabase();
		
		Cursor cursor = dbReader.query(CourseDB.TABLE_NAME, null, selection,
				selectionArgs, null, null, null);

		// 没有课程信息
		if (cursor.getCount() == 0) {
			cursor.close();
			dbReader.close();
			courseDB.close();
			return courses;
		}
		
		String courseName = "";
		int startLesson = 0;
		int totalLesson = 0;
		String classroom = "";
		String day = "";
		String week = "";
		String teacher = "";
		
		cursor.moveToFirst();  
		while (!cursor.isAfterLast()) {
			// 获取各字段名称
		    courseName = cursor.getString(cursor.getColumnIndex(CourseDB.COURSE_NAME));
		    startLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.START_LESSON));
		    totalLesson = cursor.getInt(cursor.getColumnIndex(CourseDB.TOTAL_LESSON));
		    classroom = cursor.getString(cursor.getColumnIndex(CourseDB.CLASSROOM));
		    day = cursor.getString(cursor.getColumnIndex(CourseDB.DAY));
		    week = cursor.getString(cursor.getColumnIndex(CourseDB.WEEK));
		    teacher = cursor.getString(cursor.getColumnIndex(CourseDB.TEACHER));
		    
		    Course tmp = new Course(courseName, day, classroom, startLesson, totalLesson, week, teacher);
		    courses.add(tmp);
		    
		    cursor.moveToNext();  
		}
		// 关闭
		cursor.close();
		dbReader.close();
		courseDB.close();
		
		return courses;
	}
	
	/**
	 * 删除所有课程
	 * @param context
	 */
	public static void deleteAllCourses(Context context) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();
		
		String sql_delTable = "DELETE FROM "+ CourseDB.TABLE_NAME;
		dbWriter.execSQL(sql_delTable);
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 存储单个课程
	 * @param context
	 * @param course
	 */
	public static void storeSingleCourse(Context context, Course course) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(CourseDB.COURSE_NAME, course.getName());
		values.put(CourseDB.DAY, course.getDay());
		values.put(CourseDB.CLASSROOM, course.getClassroom());
		values.put(CourseDB.START_LESSON, course.getStartLesson());
		values.put(CourseDB.TOTAL_LESSON, course.getTotalLesson());
		values.put(CourseDB.WEEK, course.getWeek());
		values.put(CourseDB.TEACHER, course.getTeacher());
		
		dbWriter.insert(CourseDB.TABLE_NAME, null, values);
		values.clear();
			
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 删除单个课程
	 * @param context
	 * @param name
	 */
	public static void deleteSingleCourse(Context context, String name) {
		CourseDB courseDB = new CourseDB(context);
		SQLiteDatabase dbWriter = courseDB.getWritableDatabase();

		dbWriter.delete(CourseDB.TABLE_NAME, CourseDB.COURSE_NAME+"=?", new String[]{ name });
		
		dbWriter.close();
		courseDB.close();
	}
	
	/**
	 * 获取所有课程名称，并且在末尾添加一个缺省字符串变量 用于打开数据库操作
	 * 
	 * @param context 上下文变量
	 * @return 所有课程名称，加一个缺省字符串""
	 */
	public static List<String> getCoursesName(Context context) {
		// 载入课表信息
		List<Course> courses = (ArrayList<Course>) CourseDBOp
				.loadCourses(context);

		if (courses.size() > 0) {
			ArrayList<String> courseList = new ArrayList<String>();

			for (int i = 0; i < courses.size(); i++) {
				courseList.add(courses.get(i).getName());
			}
			// 添加缺省字符串
			courseList.add(Config.DEFAULT);

			return courseList;
		}

		return new ArrayList<String>();
	}
	
	/**
	 * 检查添加的单个课程是否与现有课程产生冲突
	 * @param context
	 * @param course
	 * @return
	 */
	public static boolean checkConflict(Context context, Course course) {
		boolean conflict = false;
		
		int start_1 = course.getStartLesson();
		int end_1 = start_1 + course.getTotalLesson() - 1;
		
		// 获取当天的所有课程
		String weekday = course.getDay();
		ArrayList<Course> courses = CourseDBOp.queryCourses(context,
				CourseDB.DAY + "=?", new String[] { weekday });
		
		// 遍历检查冲突
		for (int i = 0; i < courses.size(); i++) {
			// 检查是否发生交叉
			int start_2 = courses.get(i).getStartLesson();
			int end_2 = start_2 + courses.get(i).getTotalLesson() - 1;

			if (isOverlap(start_1, end_1, start_2, end_2)) {
				conflict = true;
				break;
			}
		}
		
		return conflict;
	}
	
	/**
	 * 获取当前的课程名称，没有则返回缺省字符串"默认"
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentCourse(Context context) {
		String weekday = "";
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		Date d = new Date();
		cal.setTime(d);
		int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (day < 0)
			day = 0;
		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);
		int current_total_minutes = currentHour * 60 + currentMinute;

		weekday = weekDays[day];

		ArrayList<Course> courses = CourseDBOp.queryCourses(context,
				CourseDB.DAY + "=?", new String[] { weekday });

		// 计算当前时间是否处于某节课中
		for (int i = 0; i < courses.size(); i++) {
			// 某节课开始的时间，以总分钟计算
			int min_m = 8 * 60 + (courses.get(i).getStartLesson() - 1) * 55;
			// 某节课结束的时间，以总分钟计算
			int max_m = min_m + courses.get(i).getTotalLesson() * 55 - 10;

			if (min_m <= current_total_minutes
					&& max_m >= current_total_minutes) {
				return courses.get(i).getName();
			}
		}

		return Config.DEFAULT;
	}
	
	/**
	 * 判断区间是否重叠
	 * @param begin1
	 * @param end1
	 * @param begin2
	 * @param end2
	 * @return
	 */
	public static boolean isOverlap(int begin1, int end1, int begin2, int end2) {
		int begin = begin1 > begin2 ? begin1 : begin2;
		int end = end1 < end2 ? end1 : end2;
		
		int len = end - begin;
		return len >= 0 ? true : false;
	}
}
