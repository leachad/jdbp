/**
 * 
 */
package jdbp.host;

/**
 * @author andrew.leach
 */
public class JdbpHost extends AbstractHost {

	private String hostName;

	/**
	 * @param hostName
	 */
	public JdbpHost(String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return hostName;
	}
}
