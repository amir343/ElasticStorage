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
package econtroller.modeler;

import org.jfree.chart.JFreeChart;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-18
 *
 */

public class ModelerSnapshot {
	
	private int id;
	private Date date = Calendar.getInstance().getTime();
	private JFreeChart cpuChart;
	private JFreeChart responseTimeChart;
	private JFreeChart AverageBandwidth;
	private JFreeChart nrInstancesChart;
	private JFreeChart totalCostChart;
	private JFreeChart averageThroughputChart;
	private String logText;
	
	public ModelerSnapshot(int id) {
		this.id = id;
	}

	public void setCpuChart(JFreeChart cpuChart) {
		this.cpuChart = cpuChart;
	}

	public void setResponseTimeChart(JFreeChart responseTimeChart) {
		this.responseTimeChart = responseTimeChart;
	}

	public void setAverageBandwidth(JFreeChart averageBandwidth) {
		this.AverageBandwidth = averageBandwidth;
	}

	public void setNrInstanceChart(JFreeChart nrInstancesChart) {
		this.nrInstancesChart = nrInstancesChart;
	}

	public void setTotalCostChart(JFreeChart totalCostChart) {
		this.totalCostChart = totalCostChart;		
	}

	public void setAverageThroughputChart(JFreeChart averageThroughputChart) {
		this.averageThroughputChart = averageThroughputChart;
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public JFreeChart getCpuChart() {
		return cpuChart;
	}

	public JFreeChart getResponseTimeChart() {
		return responseTimeChart;
	}

	public JFreeChart getAverageBandwidth() {
		return AverageBandwidth;
	}

	public JFreeChart getNrInstancesChart() {
		return nrInstancesChart;
	}

	public JFreeChart getTotalCostChart() {
		return totalCostChart;
	}

	public JFreeChart getAverageThroughputChart() {
		return averageThroughputChart;
	}

	public void addLogText(String text) {
		this.logText = text;		
	}

	public String getLogText() {
		return logText;
	}

}
