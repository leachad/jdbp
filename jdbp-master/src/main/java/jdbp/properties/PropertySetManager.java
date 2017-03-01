package jdbp.properties;

import jdbp.exception.JdbpException;
import jdbp.properties.util.DriverUtil;
import jdbp.properties.util.PropertySetUtility;
import jdbp.properties.util.SyntaxUtil;

/**
 * @since 12.28.16
 * @author andrew.leach
 */
public class PropertySetManager {

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
