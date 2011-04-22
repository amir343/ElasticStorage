package cloud.api.address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class AddressRange {
	
	private String ip;
	private int startPort;
	private int endPort;
	
	public AddressRange(String ip, int startPort, int endPort) {
		this.ip = ip;
		this.startPort = startPort;
		this.endPort = endPort;
	}

	public String getIp() {
		return ip;
	}

	public int getStartPort() {
		return startPort;
	}

	public int getEndPort() {
		return endPort;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setStartPort(int startPort) {
		this.startPort = startPort;
	}

	public void setEndPort(int endPort) {
		this.endPort = endPort;
	}
	
}
