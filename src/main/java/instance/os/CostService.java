package instance.os;

import cloud.common.BandwidthCost;
import cloud.common.CPUCost;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-27
 * 
 * Responsibility: This class is responsible for computing the total cost of the current instance
 * according to Amazon EC2 and S3 service in EU-WEST (Ireland) region for a Linux instance.
 *
 */

public class CostService {

	private long start;
	private BandwidthCost bandwidthCost;
	private CPUCost cpuCost;
	private Double previousCost;
    private Double costOfExistence = 0.001;

	public CostService() {
		start = System.currentTimeMillis();
	}
	
	public void init(NodeConfiguration nodeConfiguration) {
		this.bandwidthCost = nodeConfiguration.getBandwidthConfiguration().getCost();
		this.cpuCost = nodeConfiguration.getCpuConfiguration().getCost();
	}
	
	public double computeCostSoFar(int megaBytesDownloadedSoFar) {
		double totalCost = 0.0;
		
		totalCost += computeCpuCost();
		totalCost += computeDataTransferCost(megaBytesDownloadedSoFar);
        totalCost += computeCostOfExistence();
		
		return totalCost;
	}

    private double computeCostOfExistence() {
        long runningTimeInSeconds = (System.currentTimeMillis() - start)/1000;
        return costOfExistence*runningTimeInSeconds;
    }

    public double computeCostInThisPeriod(int megaBytesDownloadedSoFar) {
		if (previousCost == null) {
			previousCost = computeCostSoFar(megaBytesDownloadedSoFar);
			return previousCost;
		} else {
			double costSoFar = computeCostSoFar(megaBytesDownloadedSoFar);
			double newCost = costSoFar - previousCost;
			previousCost = costSoFar;
			return newCost;
			
		}
	}

	private double computeDataTransferCost(int megaBytesDownloadedSoFar) {
        return (bandwidthCost.getCostPerGB()/1000)*megaBytesDownloadedSoFar;
	}

	private double computeCpuCost() {
		long runningTimeInSeconds = (System.currentTimeMillis() - start)/1000;
        return (cpuCost.getPerhour()/3600)*runningTimeInSeconds;
	}
	
}
