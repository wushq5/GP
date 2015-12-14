package com.wsq.syllabus.note;

/**
 * 笔记目录的实体
 * 
 * @author Charlie
 */
public class NoteCatalogEntity {
	private String name;

	private int count;

	public NoteCatalogEntity(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
