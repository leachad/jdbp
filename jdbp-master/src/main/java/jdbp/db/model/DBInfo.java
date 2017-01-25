package jdbp.db.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import jdbp.db.statement.syntax.SyntaxUtilConstants;

/**
 * @author andrew.leach
 */
public abstract class DBInfo implements SqlFieldConversion {

	@Override
	public List<String> convertCamelCaseAttributesToSql(Field[] fieldsToConvert) {
		List<String> convertedFieldNames = new ArrayList<>();
		List<Character> fieldChars = new LinkedList<>();
		StringBuilder convertedFieldName = new StringBuilder();
		for(Field field: fieldsToConvert) {
			String fieldName = field.getName();
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
			convertedFieldNames.add(convertedFieldName.toString());
			fieldChars.clear();
			convertedFieldName.setLength(0);
		}
		return convertedFieldNames;
	}
}
