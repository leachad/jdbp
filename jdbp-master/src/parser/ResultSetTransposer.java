package parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import model.DBInfo;

public class ResultSetTransposer {

	public static <T extends DBInfo> T transposeResultSet(ResultSet resultSet, Class<T> returnClass) throws SQLException, InstantiationException, IllegalAccessException {
		if(!returnClass.getSuperclass().equals(DBInfo.class)) {
			throw new SQLException("Cannot process the requested class safely");
		}
		T transposedObject = returnClass.newInstance();
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
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
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
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
				else if(currentField.getType().equals(int.class) || currentField.getType().equals(Integer.class)) {

				}
			}
		}
		return transposedObject;
	}
}
