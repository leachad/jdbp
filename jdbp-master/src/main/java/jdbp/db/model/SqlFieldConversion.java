package jdbp.db.model;

import java.lang.reflect.Field;
import java.util.List;

public interface SqlFieldConversion {

	List<String> convertCamelCaseAttributesToSql(Field[] fieldsToConvert);

	String toCommaSeparatedString();
}
