package com.andrewdleach.jdbp.demo.model;

import com.andrewdleach.jdbp.annotation.NoSQLCollection;
import com.andrewdleach.jdbp.model.DBInfo;

@NoSQLCollection
public class BlogPostDBInfo extends DBInfo {

	private String blogName;
	private String blogContent;
	private String postedDate;
	private String author;

	public String getBlogName() {
		return blogName;
	}

	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}

	public String getBlogContent() {
		return blogContent;
	}

	public void setBlogContent(String blogContent) {
		this.blogContent = blogContent;
	}

	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
