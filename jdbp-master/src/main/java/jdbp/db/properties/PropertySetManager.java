package jdbp.db.properties;

import jdbp.db.properties.util.DriverUtil;
import jdbp.db.properties.util.PropertySetUtility;
import jdbp.exception.JdbpException;

/**
 * @since 12.28.16
 * @author andrew.leach
 */
public class PropertySetManager {

	private static Class<?>[] jdbpUtilityPropertySets = {DriverUtil.class};

	private PropertySetManager() {
		// private constructor to avoid instantiation of the implicit constructor
	}

	/**
	 * Invokes the 'readJdbpUtilProperties()' of any classes implementing the PropertySetUtility interface in Jdbp
	 * 
	 * @throws JdbpException
	 */
	public static void loadAllProperties() throws JdbpException {
		for(Class<?> clazz: jdbpUtilityPropertySets) {
			if(PropertySetUtility.class.isAssignableFrom(clazz)) {
				PropertySetUtility instance = null;
				try {
					instance = (PropertySetUtility)clazz.newInstance();
				}
				catch(InstantiationException | IllegalAccessException e) {
					JdbpException.throwException(e);
				}
				if(instance != null) {
					instance.readPropertiesForJdbpUtility();
				}
			}
		}
	}

}
