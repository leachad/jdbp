package com.andrewdleach.jdbp.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.andrewdleach.jdbp.annotation.SQLTable;
import com.andrewdleach.jdbp.logger.JdbpLogger;
import com.andrewdleach.jdbp.logger.JdbpLoggerConstants;
import com.andrewdleach.jdbp.model.DBInfo;
import com.andrewdleach.jdbp.statement.syntax.SyntaxUtilConstants;

public class ConversionUtil {

	public static List<String> convertCamelCaseAttributesToSql(Field[] fieldsToConvert) {
		List<String> convertedFieldNames = new ArrayList<>();
		for(Field field: fieldsToConvert) {
			String fieldName = field.getName();
			String convertedName = ConversionUtil.convertCamelCaseAttributeToSql(fieldName);
			convertedFieldNames.add(convertedName);
		}
		return convertedFieldNames;
	}

	public static String convertCamelCaseAttributeToSql(String fieldName) {
		List<Character> fieldChars = new LinkedList<>();
		StringBuilder convertedFieldName = new StringBuilder();
		for(int i = 0; i < fieldName.length(); i++) {
			if(Character.isUpperCase(fieldName.charAt(i))) {
				char lowerCaseChar = Character.toLowerCase(fieldName.charAt(i));
				fieldChars.add(SyntaxUtilConstants.UNDERSCORE);
				fieldChars.add(lowerCaseChar);
			}
			else {
				fieldChars.add(fieldName.charAt(i));
			}
		}
		ListIterator<Character> itr = fieldChars.listIterator();
		while(itr.hasNext()) {
			char nextChar = itr.next();
			convertedFieldName.append(nextChar);
		}
		return convertedFieldName.toString();
	}

	public static String toCommaSeparatedString(DBInfo dbInfo) {
		Field[] fields = dbInfo.getClass().getDeclaredFields();
		StringBuilder csvString = new StringBuilder();
		for(int i = 0; i < fields.length; i++) {
			Field current = fields[i];
			current.setAccessible(true);
			if(dbInfo.getClass().isAnnotationPresent(SQLTable.class) && dbInfo.getClass().getAnnotation(SQLTable.class).hasPrimaryKey() && !dbInfo.getClass().getAnnotation(SQLTable.class).primaryKeyColumn().equals(current.getName())) {
				try {
					if(current.getType().isPrimitive()) {
						csvString.append(current.get(dbInfo));
					}
					else {
						csvString.append(SyntaxUtilConstants.DOUBLE_QUOTED_SINGLE_QUOTE);
						csvString.append(current.get(dbInfo));
						csvString.append(SyntaxUtilConstants.DOUBLE_QUOTED_SINGLE_QUOTE);
					}

				}
				catch(IllegalArgumentException | IllegalAccessException e) {
					JdbpLogger.logInfo(JdbpLoggerConstants.CONVERSION, e);
				}
				csvString.append(SyntaxUtilConstants.COMMA);
			}
		}
		if(csvString.length() != 0 && csvString.lastIndexOf(Character.toString(SyntaxUtilConstants.COMMA)) == csvString.length() - 1) {
			csvString.replace(csvString.lastIndexOf(Character.toString(SyntaxUtilConstants.COMMA)), csvString.length(), SyntaxUtilConstants.NO_SPACE);
		}
		return csvString.toString();
	}
}
