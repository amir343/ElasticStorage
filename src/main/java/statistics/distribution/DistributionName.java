package statistics.distribution;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public enum DistributionName {
	UNIFORM("Uniform"),
	EXPONENTIAL("Exponential"),
	CONSTANT("Constant"),
	CUSTOM("Custom");
	
	private String name;

	private DistributionName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
