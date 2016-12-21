/**
 * 
 */
package db;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author andrew.leach
 */
public class JdbpStatementUtil {

	private static JdbpStatementUtil jdbpStatementUtil = new JdbpStatementUtil();
	private static Map<String, JdbpStatementUtilPropertiesInfo> statementUtilProperties = new HashMap<>();

	/**
	 * Called on Jdbp.initialize(). Reads in locale specific information about provided list of JDBC Type 4 Drivers
	 */
	public static void readDriverUtilProperties() {
		ResourceBundle jdbpUtilProps = ResourceBundle.getBundle("resources.jdbp_statements", Locale.getDefault(), JdbpStatementUtil.class.getClassLoader());
		Set<String> keySet = jdbpUtilProps.keySet();
		for(String key: keySet) {
			statementUtilProperties.put(key.toLowerCase(), buildPropertiesInfo(jdbpUtilProps.getString(key)));
		}
	}

	private static JdbpStatementUtilPropertiesInfo buildPropertiesInfo(String utilPropsForDriver) {
		JdbpStatementUtilPropertiesInfo propsInfo = jdbpStatementUtil.new JdbpStatementUtilPropertiesInfo();
		String[] valueSubList = utilPropsForDriver.split("[;]");
		for(String indexedValue: valueSubList) {
			if(indexedValue.contains("=")) {
				String[] subKeyValue = indexedValue.split("=");
				String fieldName = subKeyValue[0];
				if(fieldName.equals("driverClassLabel")) {
					// propsInfo.setDriverClassLabel(subKeyValue[1]);
				}
				else if(fieldName.equals("supportsLoadBalancing")) {
					// propsInfo.setSupportsLoadBalancing(Boolean.getBoolean(subKeyValue[1]));
				}
				else if(fieldName.equals("supportsReplication")) {
					// propsInfo.setSupportsReplication(Boolean.getBoolean(subKeyValue[1]));
				}
				else if(fieldName.equals("loadBalancingLabel")) {
					// propsInfo.setLoadBalancedLabel(subKeyValue[1]);
				}
			}
		}
		return propsInfo;
	}

	private class JdbpStatementUtilPropertiesInfo {

	}
}
