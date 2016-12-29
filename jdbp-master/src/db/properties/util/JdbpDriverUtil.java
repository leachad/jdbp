package db.properties.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.properties.info.JdbpDriverPropertiesInfo;

/**
 * @since 12.24.16
 * @author andrew.leach
 */
public class JdbpDriverUtil implements JdbpUtilityPropertySet {
	private JdbpDriverUtil driverUtil;
	private static Map<String, JdbpDriverPropertiesInfo> driverUtilProperties = new HashMap<>();

	@Override
	public void readJdbpUtilProperties() {
		ResourceBundle jdbpUtilProps = ResourceBundle.getBundle("resources.jdbpdriver", Locale.getDefault(), JdbpDriverUtil.class.getClassLoader());
		Set<String> keySet = jdbpUtilProps.keySet();
		for(String key: keySet) {
			driverUtilProperties.put(key.toLowerCase(), buildPropertiesInfo(jdbpUtilProps.getString(key)));
		}

	}

	@Override
	public JdbpUtilityPropertySet getInstance() {
		if(driverUtil == null) {
			driverUtil = new JdbpDriverUtil();
		}
		return driverUtil;
	}

	/**
	 * @param requestedDriverName
	 * @return
	 */
	public static String getDriverClassFlagForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).getDriverClassLabel();
	}

	/**
	 * @param requestedDriverName
	 * @return
	 */
	public static String getLoadBalancedFlagForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).getLoadBalancedLabel();
	}

	/**
	 * @param requestedDriverName
	 * @return
	 */
	public static boolean isLoadBalancedSupportedForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).isSupportsLoadBalancing();
	}

	/**
	 * @param requestedDriverName
	 * @return
	 */
	public static boolean isReplicationSupportedForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).isSupportsReplication();
	}

	private static JdbpDriverPropertiesInfo buildPropertiesInfo(String utilPropsForDriver) {
		JdbpDriverPropertiesInfo propsInfo = new JdbpDriverPropertiesInfo();
		String[] valueSubList = utilPropsForDriver.split("[;]");
		for(String indexedValue: valueSubList) {
			if(indexedValue.contains("=")) {
				String[] subKeyValue = indexedValue.split("=");
				String fieldName = subKeyValue[0];
				if(fieldName.equals(PropertyAccessConstants.DRIVER_CLASS_LABEL)) {
					propsInfo.setDriverClassLabel(subKeyValue[1]);
				}
				else if(fieldName.equals(PropertyAccessConstants.SUPPORTS_LOAD_BALANCING)) {
					propsInfo.setSupportsLoadBalancing(Boolean.getBoolean(subKeyValue[1]));
				}
				else if(fieldName.equals(PropertyAccessConstants.SUPPORTS_REPLICATION)) {
					propsInfo.setSupportsReplication(Boolean.getBoolean(subKeyValue[1]));
				}
				else if(fieldName.equals(PropertyAccessConstants.LOAD_BALANCING_LABEL)) {
					propsInfo.setLoadBalancedLabel(subKeyValue[1]);
				}
			}
		}
		return propsInfo;
	}

	private class PropertyAccessConstants {
		public static final String DRIVER_CLASS_LABEL = "driverClassLabel";
		public static final String SUPPORTS_LOAD_BALANCING = "supportsLoadBalancing";
		public static final String SUPPORTS_REPLICATION = "supportsReplication";
		public static final String LOAD_BALANCING_LABEL = "loadBalancingLabel";
	}
}
