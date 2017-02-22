package jdbp;

import jdbp.db.properties.util.DriverUtil;
import jdbp.db.properties.util.PropertySetUtility;
import jdbp.db.properties.util.SyntaxUtil;
import jdbp.exception.JdbpException;

/**
 * @since 12.28.16
 * @author andrew.leach
 */
class PropertySetManager {

	private static Class<?>[] jdbpUtilityPropertySets = {DriverUtil.class, SyntaxUtil.class};

	public PropertySetManager() {
		throw new UnsupportedOperationException();
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
