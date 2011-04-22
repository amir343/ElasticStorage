package statistics.distribution;

import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
