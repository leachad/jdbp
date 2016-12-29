package db.properties;

import db.properties.util.JdbpDriverUtil;
import db.properties.util.JdbpUtilityPropertySet;
import exception.JdbpException;

/**
 * @since 12.28.16
 * @author andrew.leach
 */
public class JdbpPropertySetManager {

	private static Class<?>[] jdbpUtilityPropertySets = {JdbpDriverUtil.class};

	/**
	 * Invokes the 'readJdbpUtilProperties()' of any classes implementing the JdbpUtilityPropertySet interface in Jdbp
	 * 
	 * @throws JdbpException
	 */
	public static void loadAllProperties() throws JdbpException {
		for(Class<?> clazz: jdbpUtilityPropertySets) {
			if(JdbpUtilityPropertySet.class.isAssignableFrom(clazz)) {
				JdbpUtilityPropertySet instance = null;
				try {
					instance = (JdbpUtilityPropertySet)clazz.newInstance();
				}
				catch(InstantiationException | IllegalAccessException e) {
					JdbpException.throwException(e);
				}
				if(instance != null) {
					instance.readJdbpUtilProperties();
				}
			}
		}
	}
}
