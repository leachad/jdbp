package com.andrewdleach.jdbp.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;

public class ResultSetTransposer {

	/**
	 * @param resultSet
	 * @param clazz
	 * @return List<Class ? extends DBInfo>
	 * @throws JdbpException
	 */
	public static List<DBInfo> transposeResultSet(ResultSet resultSet, Class<? extends DBInfo> clazz) throws JdbpException {
		List<DBInfo> infoList = new ArrayList<>();
		try {
			while(resultSet.next()) {
				DBInfo dbInfo = transposeResultSetRow(resultSet, clazz);
				if(dbInfo != null) {
					infoList.add(dbInfo);
				}
			}
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return infoList;
	}

	/**
	 * @param resultSet
	 * @param returnClass
	 * @return
	 * @throws JdbpException
	 */
	private static <T extends DBInfo> T transposeResultSetRow(ResultSet resultSet, Class<T> returnClass) throws JdbpException {
		if(!returnClass.getSuperclass().equals(DBInfo.class)) {
			JdbpException.throwException(ResultSetTransposerConstants.ERROR_REQUESTED_OBJECT_NOT_CHILD_OF_DBINFO);
		}
		T transposedObject = null;
		try {
			transposedObject = returnClass.newInstance();
		}
		catch(InstantiationException | IllegalAccessException e) {
			JdbpException.throwException(e);
		}

		Map<String, Field> fieldsInClassHierarchy = retrieveAllFieldsInClassHierarchy(returnClass);

		try {

			setDbValuesObtainedFromResultSetRow(resultSet, transposedObject, fieldsInClassHierarchy);

		}
		catch(SQLException | IllegalArgumentException | IllegalAccessException e) {
			JdbpException.throwException(e);
		}
		return transposedObject;
	}

	private static <T> void setDbValuesObtainedFromResultSetRow(ResultSet resultSet, T transposedObject, Map<String, Field> fieldsInClassHierarchy) throws IllegalArgumentException, IllegalAccessException, SQLException {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			String columnName = resultSetMetaData.getColumnName(i);
			columnName = columnName.replaceAll("[\\s_]+", "").trim().toUpperCase();
			Field currentField = fieldsInClassHierarchy.get(columnName);
			Object fieldValue = null;
			if(currentField != null && !Modifier.isFinal(currentField.getModifiers()) && !Modifier.isStatic(currentField.getModifiers())) {
				currentField.setAccessible(true);
				if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {
					fieldValue = new Integer(resultSet.getInt(i));
					currentField.setInt(transposedObject, (int)fieldValue);
				}
				else if(currentField.getType().equals(double.class) || currentField.getType().equals(Double.class)) {
					fieldValue = new Double(resultSet.getDouble(i));
					currentField.setDouble(transposedObject, (double)fieldValue);
				}
				else if(currentField.getType().equals(short.class) || currentField.getType().equals(Short.class)) {
					fieldValue = new Short(resultSet.getShort(i));
					currentField.setShort(transposedObject, (short)fieldValue);
				}
				else if(currentField.getType().equals(long.class) || currentField.getType().equals(Long.class)) {
					fieldValue = new Long(resultSet.getLong(i));
					currentField.setLong(transposedObject, (long)fieldValue);
				}
				else if(currentField.getType().equals(byte.class) || currentField.getType().equals(Byte.class)) {
					fieldValue = new Byte(resultSet.getByte(i));
					currentField.setByte(transposedObject, (byte)fieldValue);
				}
				else if(currentField.getType().equals(float.class) || currentField.getType().equals(Float.class)) {
					fieldValue = new Float(resultSet.getFloat(i));
					currentField.setFloat(transposedObject, (float)fieldValue);
				}
				else if(currentField.getType().equals(boolean.class) || currentField.getType().equals(Boolean.class)) {
					fieldValue = new Boolean(resultSet.getBoolean(i));
					currentField.setBoolean(transposedObject, (boolean)fieldValue);
				}
				else if(currentField.getType().equals(char.class) || currentField.getType().equals(Character.class)) {
					String charValue = new String(resultSet.getString(i));
					char[] charArray = charValue.toCharArray();
					fieldValue = new Character(charArray[0]);
					currentField.setChar(transposedObject, (char)fieldValue);
				}
				else if(currentField.getType().equals(String.class)) {
					fieldValue = new String(resultSet.getString(i));
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(BigDecimal.class)) {
					fieldValue = resultSet.getBigDecimal(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(byte[].class)) {
					fieldValue = resultSet.getBytes(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Date.class)) {
					fieldValue = resultSet.getDate(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Time.class)) {
					fieldValue = resultSet.getTime(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Timestamp.class)) {
					fieldValue = resultSet.getTimestamp(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Blob.class)) {
					fieldValue = resultSet.getBlob(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Clob.class)) {
					fieldValue = resultSet.getClob(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Array.class)) {
					fieldValue = resultSet.getArray(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Ref.class)) {
					fieldValue = resultSet.getRef(i);
					currentField.set(transposedObject, fieldValue);
				}
				else if(currentField.getType().equals(Struct.class)) {
					fieldValue = resultSet.getObject(i, Struct.class);
					currentField.set(transposedObject, fieldValue);
				}
				else {
					Object object = resultSet.getObject(i);
					currentField.set(transposedObject, object);
				}
			}
		}

	}

	private static <T> Map<String, Field> retrieveAllFieldsInClassHierarchy(Class<T> returnClass) {
		Map<String, Field> allFieldsInClassHierarchy = new HashMap<>();
		Class<?> tempClass = returnClass;
		Field[] fields = null;
		while(!tempClass.equals(Object.class)) {
			fields = tempClass.getDeclaredFields();
			if(fields != null) {
				for(Field field: fields) {
					allFieldsInClassHierarchy.put(field.getName().toUpperCase(), field);
				}
			}
			tempClass = tempClass.getSuperclass();
		}
		return allFieldsInClassHierarchy;
	}
}
