package com.andrewdleach.jdbp.tools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.andrewdleach.jdbp.annotation.NoSQLCollection;
import com.andrewdleach.jdbp.annotation.NoSQLUpsertCondition;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class JdbpTypeUtil {

	public static Map<String, Class<?>> getAllEntryFields(Class<? extends DBInfo> containerClass) throws JdbpException {
		Map<String, Class<?>> entryFields = new HashMap<>();

		DBInfo transposedObject = null;
		try {
			transposedObject = containerClass.newInstance();
		}
		catch(InstantiationException | IllegalAccessException e) {
			JdbpException.throwException(e);
		}

		if(transposedObject != null) {
			entryFields = findAllAccessibleFieldTypesByFieldName(transposedObject);
		}
		return entryFields;
	}
	

	public static Map<String, Object> findNoSqlCollectionUpsertConditions(DBInfo dbInfo) throws JdbpException {
		Map<String, Object> noSqlUpsertConditions = new HashMap<>();

		if(dbInfo != null) {
			constructCurrentUpsertConditionMap(noSqlUpsertConditions, dbInfo);
		}
		return noSqlUpsertConditions;
	}

	private static void constructCurrentUpsertConditionMap(Map<String, Object> noSqlUpsertConditions, DBInfo originalInstance) throws JdbpException {
		Field[] fields = originalInstance.getClass().getDeclaredFields();
		for(Field field: fields) {
			if(field.getAnnotation(NoSQLUpsertCondition.class) != null){
				Object value = null;
				try {
					field.setAccessible(true);
					value = field.get(originalInstance);
				}
				catch(IllegalArgumentException | IllegalAccessException e) {
					JdbpException.throwException(e);
				}
				noSqlUpsertConditions.put(field.getName(), value);
			}
		}
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
	
	public static String convertDBInfoToJson(DBInfo originalInstance) {
		Gson gson = new Gson();
		return gson.toJson(originalInstance);
	}
	
	public static DBInfo convertJsonToDBInfo(String originalInstanceAsJson, Class<? extends DBInfo> containerClass) {
		Gson gson = new Gson();
		return gson.fromJson(originalInstanceAsJson, containerClass);
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
	
	private static Map<String, Class<?>> findAllAccessibleFieldTypesByFieldName(DBInfo newInstance) {
		Map<String, Class<?>> fieldTypesByName = new HashMap<>();
		Field[] fields = newInstance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if(field != null && !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				Class<?> declaredFieldType = field.getType();
				String declaredFieldName = field.getName();
				fieldTypesByName.put(declaredFieldName, declaredFieldType);
			}
		}
		return fieldTypesByName;
	}

}
