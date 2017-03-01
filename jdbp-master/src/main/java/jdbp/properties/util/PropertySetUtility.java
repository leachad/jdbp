/**
 * 
 */
package jdbp.properties.util;

/**
 * @author andrew.leach
 */
public interface PropertySetUtility {

	/**
	 * Read method to implement by each property set in Jdbp
	 */
	void readPropertiesForJdbpUtility();

	/**
	 * @return the instance of the implementing instance
	 */
	PropertySetUtility getInstance();

}
