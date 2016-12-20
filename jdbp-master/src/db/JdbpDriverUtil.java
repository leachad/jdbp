package db;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class JdbpDriverUtil {

	private static JdbpDriverUtil jdbpDriverUtil = new JdbpDriverUtil();
	private static Map<String, JdbpDriverUtilPropertiesInfo> driverUtilProperties = new HashMap<>();

	/**
	 * Called on Jdbp.initialize(). Reads in locale specific information about provided list of JDBC Type 4 Drivers
	 */
	public static void readDriverUtilProperties() {
		ResourceBundle jdbpUtilProps = ResourceBundle.getBundle("resources.jdbputil", Locale.getDefault(), JdbpDriverUtil.class.getClassLoader());
		Set<String> keySet = jdbpUtilProps.keySet();
		for(String key: keySet) {
			driverUtilProperties.put(key.toLowerCase(), buildPropertiesInfo(jdbpUtilProps.getString(key)));
		}
	}

	/**
	 * @param requestedDriverName
	 * @return driver class label
	 */
	public static String getDriverClassFlagForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).getDriverClassLabel();
	}

	public static String getLoadBalancedFlagForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).getLoadBalancedLabel();
	}

	public static boolean isLoadBalancedSupportedForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).isSupportsLoadBalancing();
	}

	public static boolean isReplicationSupportedForDriverName(String requestedDriverName) {
		return driverUtilProperties.get(requestedDriverName).isSupportsReplication();
	}

	private static JdbpDriverUtilPropertiesInfo buildPropertiesInfo(String utilPropsForDriver) {
		JdbpDriverUtilPropertiesInfo propsInfo = jdbpDriverUtil.new JdbpDriverUtilPropertiesInfo();
		String[] valueSubList = utilPropsForDriver.split("[;]");
		for(String indexedValue: valueSubList) {
			if(indexedValue.contains("=")) {
				String[] subKeyValue = indexedValue.split("=");
				String fieldName = subKeyValue[0];
				if(fieldName.equals("driverClassLabel")) {
					propsInfo.setDriverClassLabel(subKeyValue[1]);
				}
				else if(fieldName.equals("supportsLoadBalancing")) {
					propsInfo.setSupportsLoadBalancing(Boolean.getBoolean(subKeyValue[1]));
				}
				else if(fieldName.equals("supportsReplication")) {
					propsInfo.setSupportsReplication(Boolean.getBoolean(subKeyValue[1]));
				}
				else if(fieldName.equals("loadBalancingLabel")) {
					propsInfo.setLoadBalancedLabel(subKeyValue[1]);
				}
			}
		}
		return propsInfo;
	}

	private class JdbpDriverUtilPropertiesInfo {
		private String driverClassLabel;
		private String loadBalancedLabel;
		private boolean supportsLoadBalancing;
		private boolean supportsReplication;

		public String getDriverClassLabel() {
			return driverClassLabel;
		}

		public void setDriverClassLabel(String driverClassLabel) {
			this.driverClassLabel = driverClassLabel;
		}

		public String getLoadBalancedLabel() {
			return loadBalancedLabel;
		}

		public void setLoadBalancedLabel(String loadBalancedLabel) {
			this.loadBalancedLabel = loadBalancedLabel;
		}

		public boolean isSupportsLoadBalancing() {
			return supportsLoadBalancing;
		}

		public void setSupportsLoadBalancing(boolean supportsLoadBalancing) {
			this.supportsLoadBalancing = supportsLoadBalancing;
		}

		public boolean isSupportsReplication() {
			return supportsReplication;
		}

		public void setSupportsReplication(boolean supportsReplication) {
			this.supportsReplication = supportsReplication;
		}

	}
}
