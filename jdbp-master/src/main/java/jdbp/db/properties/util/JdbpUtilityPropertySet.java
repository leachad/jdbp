/**
 * 
 */
package jdbp.db.properties.util;

/**
 * @author andrew.leach
 */
public interface JdbpUtilityPropertySet {

	/**
	 * Read method to implement by each property set in Jdbp
	 */
	void readJdbpUtilProperties();

	/**
	 * @return the instance of the implementing instance
	 */
	JdbpUtilityPropertySet getInstance();

}
