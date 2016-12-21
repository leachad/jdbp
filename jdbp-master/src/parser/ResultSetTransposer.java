package parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.JdbpException;
import model.DBInfo;

public class ResultSetTransposer {

	// TODO Fully implement this method
	public static List<DBInfo> transposeResultSet(ResultSet resultSet, Class<? extends DBInfo> clazz) throws JdbpException {
		List<DBInfo> infoList = new ArrayList<>();
		try {
			while(resultSet.next()) {
				ResultSetMetaData resultSetData;

				resultSetData = resultSet.getMetaData();

				int columnCount = resultSetData.getColumnCount();
				DBInfo dbInfo = null;
				try {
					dbInfo = clazz.newInstance();
				}
				catch(InstantiationException | IllegalAccessException e) {
					JdbpException.throwException(e);
				}
				if(dbInfo != null) {
					for(int i = 1; i < columnCount + 1; i++) {
						if(resultSetData != null && resultSetData.getColumnType(i) == Types.INTEGER) {
							dbInfo.putInteger(i, resultSet.getInt(i));
						}
						else if(resultSetData != null && resultSetData.getColumnType(i) == Types.VARCHAR) {
							dbInfo.putString(i, resultSet.getString(i));
						}
						else if(resultSetData != null && resultSetData.getColumnType(i) == Types.FLOAT) {
							dbInfo.putFloat(i, resultSet.getFloat(i));
						}
						else if(resultSetData != null && resultSetData.getColumnType(i) == Types.DATE) {
							dbInfo.putDate(i, resultSet.getDate(i));
						}
					}
					infoList.add(dbInfo);
				}
			}
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return infoList;
	}

	public static <T extends DBInfo> T transposeResultSetRow(ResultSet resultSet, Class<T> returnClass) throws JdbpException {
		if(!returnClass.getSuperclass().equals(DBInfo.class)) {
			JdbpException.throwException("Cannot process the requested class safely");
		}
		T transposedObject = null;
		try {
			transposedObject = returnClass.newInstance();
		}
		catch(InstantiationException | IllegalAccessException e) {
			JdbpException.throwException(e);
		}
		Map<String, Field> allFieldsInClassHierarchy = new HashMap<>();
		Class<?> tempClass = returnClass;
		Field[] fields = null;
		while(!returnClass.equals(Object.class)) {
			fields = tempClass.getDeclaredFields();
			if(fields != null) {
				for(Field field: fields) {
					allFieldsInClassHierarchy.put(field.getName().toUpperCase(), field);
				}
			}
			tempClass = tempClass.getSuperclass();
		}

		fields = allFieldsInClassHierarchy.values().toArray(new Field[0]);
		ResultSetMetaData resultSetMetaData;
		try {
			resultSetMetaData = resultSet.getMetaData();

			for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
				String columnName = resultSetMetaData.getColumnLabel(i);
				columnName = columnName.replaceAll("[\\s_]+", "").trim().toUpperCase();
				Field currentField = allFieldsInClassHierarchy.get(columnName);
				Object fieldValue = null;
				if(currentField != null && !Modifier.isFinal(currentField.getModifiers()) && !Modifier.isStatic(currentField.getModifiers())) {
					if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {
						fieldValue = new Integer(resultSet.getInt(i));
					}
					else if(currentField.getType().equals(double.class) || currentField.getType().equals(Double.class)) {
						fieldValue = new Double(resultSet.getDouble(i));
					}
					else if(currentField.getType().equals(short.class) || currentField.getType().equals(Short.class)) {
						fieldValue = new Short(resultSet.getShort(i));
					}
					else if(currentField.getType().equals(long.class) || currentField.getType().equals(Long.class)) {
						fieldValue = new Long(resultSet.getLong(i));
					}
					else if(currentField.getType().equals(byte.class) || currentField.getType().equals(Byte.class)) {
						fieldValue = new Byte(resultSet.getByte(i));
					}
					else if(currentField.getType().equals(float.class) || currentField.getType().equals(Float.class)) {
						fieldValue = new Float(resultSet.getFloat(i));
					}
					else if(currentField.getType().equals(boolean.class) || currentField.getType().equals(Boolean.class)) {
						fieldValue = new Boolean(resultSet.getBoolean(i));
					}
					else if((currentField.getType().equals(String.class)) || (currentField.getType().equals(char.class) || currentField.getType().equals(Character.class))) {
						fieldValue = new String(resultSet.getString(i));
					}
				}
			}
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return transposedObject;
	}
}
