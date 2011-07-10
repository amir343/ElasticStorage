package cloud.common;

import instance.common.Size;

import java.io.Serializable;

/**
 * 
 * @author Amir Moulavi
 *
 */

public class BandwidthConfiguration implements Serializable {
	
	private static final long serialVersionUID = -7562773009462128893L;
	private double bandwidth;
	private BandwidthCost cost;
	
	public BandwidthConfiguration(double bandwidth) {
		this.bandwidth = bandwidth;
		determineCostPerGB();
	}
	

	public long getBandwidthMegaBytePerSecond() {
		long result = (long) ((bandwidth*10) * Size.MB.getSize());
		result /= 10;
		return result;
	}
	
	public double getBandwidth() {
		return bandwidth;
	}
	
	private void determineCostPerGB() {
		if (bandwidth < 1.0) cost = BandwidthCost.XLOW;
		else if (bandwidth >= 1.0 && bandwidth < 2.0) cost = BandwidthCost.LOW;
		else if (bandwidth >= 2.0 && bandwidth < 3.0) cost = BandwidthCost.MEDIUM;
		else if (bandwidth >= 3.0 && bandwidth < 4.0) cost = BandwidthCost.HIGH;
		else if (bandwidth >= 4.0 && bandwidth < 5.0) cost = BandwidthCost.XHIGH;
		else if (bandwidth >= 5.0 ) cost = BandwidthCost.XXHIGH;		
	}

	public BandwidthCost getCost() {
		return cost;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("bandwidth: ").append(bandwidth);
		return sb.toString();
	}

}
