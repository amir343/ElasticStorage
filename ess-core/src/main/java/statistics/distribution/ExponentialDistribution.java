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
 * F(u) = -ln(1-u)/λ   for (0 < u <1)
 */

public class ExponentialDistribution implements Distribution {

	private double lambda;
	private Random random = new Random();
	private double minInterval;

	public ExponentialDistribution() {
		this.lambda = 1;
		this.minInterval = 10;
	}
	
	@Override
	public long getNextValue() {
		double re = (-1/lambda)*Math.log(1-random.nextDouble());
		re += minInterval;
		return (long) ( 1000 * re );
	}
	
	@Override
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Exponential Cumulative Distribution", "", "", getDataset(), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
	
	@Override
	public void setParameters(String... params) {
		if (params.length >= 1) {
			minInterval = Double.parseDouble(params[0]);
		}
	}

	public void setMinInterval(double minInterval) {
		this.minInterval = minInterval;
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
		return 1-Math.exp(-1*lambda*x);
	}
	
}
