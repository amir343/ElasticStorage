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
	private Double loadMean;
	private Double bandwidthMean;
	private Double totalCost;
	private Double responseTimeMean;

	public TrainingData(Address source, Address destination, int nrNodes) {
		super(source, destination);
		this.nrNodes = nrNodes;
	}

	public Double getLoadMean() {
		return loadMean;
	}

	public Double getBandwidthMean() {
		return bandwidthMean;
	}

	public int getNrNodes() {
		return nrNodes;
	}

	public void setCpuLoalMean(Double cpuLoadMean) {
		this.loadMean = cpuLoadMean;
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

}
