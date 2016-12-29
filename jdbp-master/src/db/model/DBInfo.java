package db.model;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO Refactor DBInfo into an abstract class AND Add realistic method signatures that maintain order based on column index in DBinfo implementation
 * 
 * @author andrew.leach
 */
public class DBInfo {

	Map<Integer, Object> infoList = null;

	public DBInfo() {
		infoList = new HashMap<>();
	}

	public void putInteger(int i, int int1) {
		infoList.put(i, int1);

	}

	public void putString(int i, String string) {
		infoList.put(i, string);

	}

	public void putFloat(int i, float float1) {
		infoList.put(i, float1);

	}

	public void putDate(int i, Date date) {
		infoList.put(i, date);
	}

	@Override
	public String toString() {
		return Arrays.toString(infoList.values().toArray());
	}

}
