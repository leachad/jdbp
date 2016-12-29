package db.properties.info;

import java.io.Serializable;

/**
 * @since 12.29.16
 * @author andrew.leach
 */
public class JdbpDriverPropertiesInfo implements Serializable {
	private static final long serialVersionUID = -2943279356066920245L;

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
