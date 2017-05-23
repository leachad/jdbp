package com.andrewdleach.jdbp.demo.driver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.andrewdleach.jdbp.Jdbp;
import com.andrewdleach.jdbp.demo.model.BlogPostDBInfo;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;
import com.andrewdleach.jdbp.parser.ConversionUtil;
import com.andrewdleach.jdbp.schema.JdbpNoSqlSchema;

public class Driver {

	public static void main(String[] args) throws JdbpException {
		Jdbp jdbp = Jdbp.getInstance();
		List<DBInfo> blogPosts = new ArrayList<>();
		BlogPostDBInfo blogPost = new BlogPostDBInfo();
		blogPost.setAuthor("Abraham Lincoln");
		blogPost.setBlogContent("4 score and 7 years ago...");
		blogPost.setBlogName("gettysburg address");
		blogPost.setPostedDate(Calendar.getInstance().toString());

		blogPosts.add(blogPost);
		JdbpNoSqlSchema noSqlSchema = (JdbpNoSqlSchema)jdbp.getDatabase("siteBlog");
		List<DBInfo> dbInfos = noSqlSchema.getCollection("blogPosts", BlogPostDBInfo.class);
		if(dbInfos.isEmpty()) {
			boolean isSuccess = noSqlSchema.insertCollection("blogPosts", blogPosts, BlogPostDBInfo.class);
			Logger.getLogger("info").log(Level.ALL, "Insert is successful: " + isSuccess);
		}
		else {
			for(DBInfo dbInfo: dbInfos) {
				Logger.getLogger("info").log(Level.ALL, ConversionUtil.toCommaSeparatedString(dbInfo));
			}
		}

	}

}
