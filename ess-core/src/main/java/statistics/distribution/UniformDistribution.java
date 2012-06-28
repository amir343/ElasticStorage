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
package statistics.distribution;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Random;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class UniformDistribution implements Distribution {

	private double a = 0;
	private double b = 10;
	private Random random = new Random();
	
	public UniformDistribution() {
		
	}
	
	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	@Override
	public long getNextValue() {
		double re = random.nextDouble()*(b-a)+a; 
		return (long) (1000 * re);
	}

	@Override
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Uniform Cumulative Distribution", "", "", getDataset(), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	@Override
	public void setParameters(String... params) {
		if (params.length >= 2) {
			a = Double.parseDouble(params[0]);
			b = Double.parseDouble(params[1]);
		}
	}

	private XYDataset getDataset() {
		XYSeriesCollection dataSet = new XYSeriesCollection();
		XYSeries xySeries = new XYSeries("CDF");
		double x = 0.0;
		for (int i=0; i<50; i++) {
			xySeries.add(x, CDF(x));
			x += 0.2;
		}
		dataSet.addSeries(xySeries);
		return dataSet;
	}

	private double CDF(double x) {
		return (x-a)/(b-a);
	}

}
