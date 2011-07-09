package cloud.elb;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */

public class SenseData extends Message {

	private static final long serialVersionUID = 8999243260455843795L;
	private int numberOfNodes;
	private double averageResponseTime;
	private double averageThroughput;
	private double cpuLoad;
	private double bandwidthMean;
	private double totalCost;

	public SenseData(Address source, Address destination, int numberOfNodes) {
		super(source, destination);
		this.numberOfNodes = numberOfNodes;
	}

	public void setResponseTimeMean(double averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public void setThroughputMean(double averageThroughput) {
		this.averageThroughput = averageThroughput;
	}

	public void setCpuLoalMean(double cPULoadMean) {
		this.cpuLoad = cPULoadMean;
	}

	public void setBandwidthMean(double bandwidthMean) {
		this.bandwidthMean = bandwidthMean;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public double getAverageResponseTime() {
		return averageResponseTime;
	}

	public double getAverageThroughput() {
		return averageThroughput;
	}

	public double getCpuLoad() {
		return cpuLoad;
	}

	public double getBandwidthMean() {
		return bandwidthMean;
	}

	public double getTotalCost() {
		return totalCost;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SenseData: \n{\n");
		sb.append("\tcpuAverage: ").append(cpuLoad).append("\n");
		sb.append("\tbandwidthAverage: ").append(bandwidthMean).append("\n");
		sb.append("\ttotalCost: ").append(totalCost).append("\n");
		sb.append("\taverageResponseTime: ").append(averageResponseTime).append("\n");
		sb.append("}");
		return sb.toString();
	}
	
}
