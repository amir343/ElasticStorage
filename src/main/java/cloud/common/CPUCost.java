package cloud.common;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-28
 *
 * Responsibility: this enum shows the cost per hour in US dollars according to Amazon EC2 pricing
 * for Linux image in EU-WEST (Ireland) region.
 */

public enum CPUCost {
	MICRO(0.025),
	XSMALL(0.056),
	SMALL(0.095),
	LARGE(0.38),
	XLARGE(0.76),
	XXLARGE(1.14), 
	XXXLARGE(2.28);
	
	private double perhour;

	CPUCost(double perhour) {
		this.perhour = perhour;
	}

	public double getPerhour() {
		return perhour;
	}
	
}
