package statistics.distribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class DistributionRepository {
	
	private static DistributionRepository instance = new DistributionRepository();
	private Map<String, Distribution> distributions = new HashMap<String, Distribution>();
	
	public static DistributionRepository getInstance() {
		return instance;
	}
	
	private DistributionRepository() {}

	public String[] getDistributions() {
		DistributionName[] values = DistributionName.values();
		String[] names = new String[values.length];
		for (int i=0; i<names.length; i++) {
			names[i]=values[i].getName();
		}
		return names;
	}

	public Distribution getExponentialDistribution() {
		Distribution dist = distributions.get(DistributionName.EXPONENTIAL.getName());
		if (dist == null) {
			dist = new ExponentialDistribution();
		}
		return dist;
	}

	public Distribution getUniformDistribution() {
		Distribution dist = distributions.get(DistributionName.UNIFORM.getName());
		if (dist == null) {
			dist = new UniformDistribution();
		}
		return dist;
	}

	public Distribution getCustomDistribution(List<String> lines) {
		Distribution dist = new CustomDistribution(lines);
		return dist;
	}

	public Distribution getConstantDistribution() {
		Distribution dist = distributions.get(DistributionName.CONSTANT.getName());
		if (dist == null) {
			dist = new ConstantDistribution();
		}
		return dist;
	}
	
}
