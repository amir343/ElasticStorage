package cloud.common;

import java.io.Serializable;

import instance.common.Size;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-28
 *
 */

public class CpuConfiguration implements Serializable {

	private static final long serialVersionUID = 1638296255236995956L;
	private double cpuSpeed;
	private CPUCost cost;

	public CpuConfiguration(double cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
		determineCostForThisSpeed();
	}
	
	public long getCpuSpeedInstructionPerSecond() {
		long result = (long) ((cpuSpeed*10) * Size.GHertz.getSize());
		result /= 10;
		return result;
	}
	
	public double getCpuSpeed() {
		return cpuSpeed;
	}

	private void determineCostForThisSpeed() {
		if (cpuSpeed < 1.8) cost = CPUCost.MICRO;
		else if (cpuSpeed >= 1.8 && cpuSpeed < 2.0) cost = CPUCost.XSMALL;
		else if (cpuSpeed >= 2.0 && cpuSpeed < 2.4) cost = CPUCost.SMALL;
		else if (cpuSpeed >= 2.4 && cpuSpeed < 2.8) cost = CPUCost.LARGE;
		else if (cpuSpeed >= 2.8 && cpuSpeed < 3.2) cost = CPUCost.XLARGE;
		else if (cpuSpeed >= 3.2 && cpuSpeed < 3.8) cost = CPUCost.XXLARGE;
		else if (cpuSpeed >= 3.8) cost = CPUCost.XXXLARGE;		
	}

	public CPUCost getCost() {
		return cost;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("cpu: ").append(cpuSpeed);
		return sb.toString();
	}
	
}
