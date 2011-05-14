package cloud.common;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class SendRawData extends Event {
	
	private Address controller;
	private int nrNodes;
	private double totalCost;
	private double averageThroughput;
	private double averageResponseTime;

	public SendRawData(Address controller, int numberOfNodes) {
		this.controller = controller;
		this.nrNodes = numberOfNodes;
	}

	public Address getController() {
		return controller;
	}

	public int getNrNodes() {
		return nrNodes;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getAverageThroughput() {
		return averageThroughput;
	}

	public void setAverageThroughput(double averageThroughput) {
		this.averageThroughput = averageThroughput;
	}

	public void setAverageResponseTime(double averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public double getAverageResponseTime() {
		return averageResponseTime;
	}
	
}
