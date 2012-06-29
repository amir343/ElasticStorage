/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
		this.bandwidthCost = nodeConfiguration.bandwidthConfiguration().cost();
		this.cpuCost = nodeConfiguration.cpuConfiguration().cost();
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
