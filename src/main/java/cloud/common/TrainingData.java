package cloud.common;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class TrainingData extends Message {

	private static final long serialVersionUID = -4971188701344810800L;
	private int nrNodes;
	private Double cpuLoadMean;
	private Double bandwidthMean;
	private Double totalCost;
	private Double responseTimeMean;
	private Double throughputMean;
	
	public TrainingData(Address source, Address destination, int nrNodes) {
		super(source, destination);
		this.nrNodes = nrNodes;
	}

	public Double getCPULoadMean() {
		return cpuLoadMean;
	}

	public Double getBandwidthMean() {
		return bandwidthMean;
	}

	public int getNrNodes() {
		return nrNodes;
	}

	public void setCpuLoalMean(Double cpuLoadMean) {
		this.cpuLoadMean = cpuLoadMean;
	}

	public void setBandwidthMean(Double bandwidthMean) {
		this.bandwidthMean = bandwidthMean;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;		
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setResponseTimeMean(Double mean) {
		this.responseTimeMean = mean;
	}

	public Double getResponseTimeMean() {
		return responseTimeMean;
	}

	public Double getThroughputMean() {
		return throughputMean;
	}

	public void setThroughputMean(Double throughputMean) {
		this.throughputMean = throughputMean;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("throughput: ").append(throughputMean).append(", ");
			sb.append("cpu: ").append(cpuLoadMean).append(", ");
			sb.append("bandwidth: ").append(bandwidthMean).append(", ");
			sb.append("cost: ").append(totalCost).append(", ");
			sb.append("responseTime: ").append(responseTimeMean);
		sb.append("}");
		return sb.toString();
	}
	

}
