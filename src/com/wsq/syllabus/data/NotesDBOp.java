package com.wsq.syllabus.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wsq.syllabus.note.NoteCatalogEntity;
import com.wsq.syllabus.util.Config;
import com.wsq.syllabus.util.PublicUitl;

public class NotesDBOp {
	
	/**
	 * 创建笔记Table
	 * @param context
	 */
	public static void createNoteTable(Context context) {
		
		ArrayList<String> list = (ArrayList<String>) CourseDBOp.getCoursesName(context);
		
		if (list == null) {
			return ;
		}
		
		NotesDB notesDB = new NotesDB(context, list);
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();
		
		for (int i = 0; i < list.size(); i++) {
			String courseName = list.get(i).replace(" ", "");
			
			dbWriter.execSQL("CREATE TABLE IF NOT EXISTS " + courseName + " (" 
					+ Config.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Config.CONTENT + " TEXT NOT NULL," 
					+ Config.IMAGE + " TEXT NOT NULL,"
					+ Config.VIDEO + " TEXT NOT NULL,"
					+ Config.TIME + " TEXT NOT NULL)");
		}
		
		dbWriter.close();
		notesDB.close();
	}

	/**
	 * 删除所有笔记
	 * @param context
	 */
	public static void deleteAllNotes(Context context) {
		ArrayList<String> list = (ArrayList<String>) CourseDBOp.getCoursesName(context);
		NotesDB notesDB = new NotesDB(context, list);
		
		// 当前不存在课程，直接退出
		if (list == null) {
			notesDB.close();
			return ;
		}

		SQLiteDatabase dbReader = notesDB.getReadableDatabase();
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();

		// 每一个课程是一个表
		// 先将表中的图片与录像删除，再删除该表
		for (int i = 0; i < list.size(); i++) {
			// 去空格
			String courseName = list.get(i).replace(" ", "");
			Cursor cursor = dbReader.query(courseName, new String[] {
					NotesDB.IMAGE, NotesDB.VIDEO }, null, null, null, null,
					null);
			
			if (cursor.getCount() == 0) {
				continue;
			}
			
			cursor.moveToFirst();  
			while (!cursor.isAfterLast()) {
				
				// 获取图片与录像路径
				String imageUri = cursor.getString(cursor.getColumnIndex(NotesDB.IMAGE));
				String videoUri = cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO));

				// 删除文件
				PublicUitl.deleteFile(imageUri);
				PublicUitl.deleteFile(videoUri);
			    
			    cursor.moveToNext();  
			}
			
			cursor.close();
			String sql_delTable = "DROP TABLE "+courseName;
			dbWriter.execSQL(sql_delTable);
		}
		
		// 关闭数据库
		dbReader.close();
		dbWriter.close();
		notesDB.close();
	}
	
	public static void delSingleCourseNote(Context context, String courseName) {
		ArrayList<String> list = (ArrayList<String>) CourseDBOp.getCoursesName(context);
		NotesDB notesDB = new NotesDB(context, list);

		SQLiteDatabase dbReader = notesDB.getReadableDatabase();
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();

		// 每一个课程是一个表
		// 先将表中的图片与录像删除，再删除该表
		courseName = courseName.replace(" ", "");
		Cursor cursor = dbReader.query(courseName, new String[] {
				NotesDB.IMAGE, NotesDB.VIDEO }, null, null, null, null,
				null);
	
		if (cursor.getCount() != 0) {

			cursor.moveToFirst();  
			while (!cursor.isAfterLast()) {
				
				// 获取图片与录像路径
				String imageUri = cursor.getString(cursor.getColumnIndex(NotesDB.IMAGE));
				String videoUri = cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO));

				// 删除文件
				PublicUitl.deleteFile(imageUri);
				PublicUitl.deleteFile(videoUri);
			    
			    cursor.moveToNext();  
			}
		}

		cursor.close();
		// 删除表
		String sql_delTable = "DROP TABLE IF EXISTS "+courseName;
		dbWriter.execSQL(sql_delTable);
		
		// 关闭数据库
		dbReader.close();
		dbWriter.close();
		notesDB.close();
	}
	
	/**
	 * 存储单个笔记
	 * @param context 上下文实例
	 * @param content 笔记文字
	 * @param picDir 图片路径
	 * @param videoDir 录像路径
	 */
	public static void storeNote(Context context, String content,
			String picDir, String videoDir) {
		NotesDB notesDB = new NotesDB(context);

		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(NotesDB.CONTENT, content);
		cv.put(NotesDB.TIME, PublicUitl.getCurrentTime("yyyy-MM-dd HH:mm"));
		cv.put(NotesDB.IMAGE, picDir);
		cv.put(NotesDB.VIDEO, videoDir);
		
		// 计算出当前时间所属的课程，若不属于所有课程则属于"默认"
		String tableName = CourseDBOp.getCurrentCourse(context);
		dbWriter.insert(tableName, null, cv);
		
		dbWriter.close();
		notesDB.close();
	}
	
	/**
	 * 删除笔记
	 * @param context
	 * @param table 表名
	 * @param id 笔记的id，唯一
	 */
	public static void deleteNote(Context context, String table, String id) {
		NotesDB notesDB = new NotesDB(context);
		SQLiteDatabase dbWriter = notesDB.getWritableDatabase();
		
		dbWriter.delete(table, "_id="+id, null);
		
		dbWriter.close();
		notesDB.close();
	}
	
	/**
	 * 获取各个课程的笔记数量
	 * 
	 * @param context
	 * @return
	 */
	public static List<NoteCatalogEntity> getNotesCount(Context context) {
		// 先获取课程名称
		List<String> courses = CourseDBOp.getCoursesName(context);

		NotesDB notesDB = new NotesDB(context);
		SQLiteDatabase dbReader = notesDB.getReadableDatabase();

		List<NoteCatalogEntity> noteNums = new ArrayList<NoteCatalogEntity>();

		Cursor cursor;
		for (int i = 0; i < courses.size(); i++) {
			String course = courses.get(i).replace(" ", "");
			cursor = dbReader.query(course, null, null, null, null, null, null);
			int count = cursor.getCount();
			noteNums.add(new NoteCatalogEntity(courses.get(i), count));
		}

		cursor = null;
		dbReader.close();
		notesDB.close();
		
		return noteNums;
	}
}
