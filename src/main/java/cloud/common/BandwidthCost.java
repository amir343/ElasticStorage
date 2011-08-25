package cloud.common;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-28
 *
 */

public enum BandwidthCost {
	XLOW(0.10),
	LOW(0.15),
	MEDIUM(0.2),
	HIGH(0.25),
	XHIGH(0.3),
	XXHIGH(.35);
	
	private double cost;

	BandwidthCost(double cost) {
		this.cost = cost;
	}

	public double getCostPerGB() {
		return cost;
	}
	
}
