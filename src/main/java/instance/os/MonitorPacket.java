package instance.os;

import java.io.Serializable;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class MonitorPacket implements Serializable {

	private static final long serialVersionUID = -1976964145880092096L;
	private double cpuLoad;
	private long bandwidth;

	public MonitorPacket(double cpuLoad, long bandwidth) {
		this.cpuLoad = cpuLoad;
		this.bandwidth = bandwidth;
	}

	public double getCpuLoad() {
		return cpuLoad;
	}

	public long getBandwidth() {
		return bandwidth;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("cpu: ").append(cpuLoad).append(",");
			sb.append("bandwidth: ").append(bandwidth);
		sb.append("}");
		return sb.toString();
	}
	
}
