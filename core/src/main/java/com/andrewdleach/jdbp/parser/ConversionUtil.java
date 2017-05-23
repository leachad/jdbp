package com.andrewdleach.jdbp.parser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.andrewdleach.jdbp.annotation.NoSQLCollection;
import com.andrewdleach.jdbp.annotation.SQLTable;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.logger.JdbpLogger;
import com.andrewdleach.jdbp.logger.JdbpLoggerConstants;
import com.andrewdleach.jdbp.model.DBInfo;
import com.andrewdleach.jdbp.statement.syntax.SyntaxUtilConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public static List<String> findNoSqlCollectionExcludedFields(Class<? extends DBInfo> containerClass) throws JdbpException {
		List<String> collectionExcludedFields = new ArrayList<>();

		DBInfo transposedObject = null;
		try {
			transposedObject = containerClass.newInstance();
		}
		catch(InstantiationException | IllegalAccessException e) {
			JdbpException.throwException(e);
		}

		if(transposedObject != null && transposedObject.getClass().isAnnotationPresent(NoSQLCollection.class)) {
			collectionExcludedFields = constructCurrentExcludedFieldNames(transposedObject);
		}
		return collectionExcludedFields;

	}

	private static List<String> constructCurrentExcludedFieldNames(DBInfo transposedObject) throws JdbpException {
		List<String> collectionExcludedFields = new ArrayList<>();
		String excludedFieldsAsJson = transposedObject.getClass().getAnnotation(NoSQLCollection.class).excludedFields();
		ObjectMapper mapper = new ObjectMapper();
		try {
			TypeReference<HashMap<String, String[]>> typeRef = new TypeReference<HashMap<String, String[]>>() {};
			Map<String, String[]> map = mapper.readValue(excludedFieldsAsJson, typeRef);
			if(map.get("excludedFields") != null) {
				collectionExcludedFields.addAll(Arrays.asList(map.get("excludedFields")));
			}
		}
		catch(IOException e) {
			JdbpException.throwException(e);
		}
		return collectionExcludedFields;
	}
}
