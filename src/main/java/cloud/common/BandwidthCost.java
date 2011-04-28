package cloud.common;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-28
 *
 */

public enum BandwidthCost {
	XLOW(0.1),
	LOW(0.25),
	MEDIUM(0.45),
	HIGH(0.65),
	XHIGH(0.8), 
	XXHIGH(1.0);
	
	private double cost;

	BandwidthCost(double cost) {
		this.cost = cost;
	}

	public double getCostPerGB() {
		return cost;
	}
	
}
