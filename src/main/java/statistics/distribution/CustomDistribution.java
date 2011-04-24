package statistics.distribution;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-07
 *
 */

public class CustomDistribution implements Distribution {

	private List<String> lines;
	private int currentCounter;
	private int previousValue;

	public CustomDistribution(List<String> lines) {
		this.lines = lines;
		this.currentCounter = 0;
		this.previousValue = 5000;
	}

	@Override
	public long getNextValue() {
		if (currentCounter >= lines.size()-1 ) {
			return previousValue;
		} else {
			previousValue = Integer.parseInt(lines.get(currentCounter).trim())*1000;
			if (previousValue <= 0) previousValue = 500;
			currentCounter++;
			return previousValue;
		}
	}

	@Override
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Custom Distribution", "", "", getDataset(), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
	
	private XYDataset getDataset() {
		XYSeriesCollection dataSet = new XYSeriesCollection();
		XYSeries xySeries = new XYSeries("PDF");
		double x = 1;
		for (int i=0; i<lines.size(); i++) {
			int y = Integer.parseInt(lines.get(i).trim());
			x += y;
			xySeries.add(x, y);
		}
		for (int i=0; i<30; i++) {
			int y = Integer.parseInt(lines.get(lines.size()-1).trim());
			x += y;
			xySeries.add(x, y);
		}
		dataSet.addSeries(xySeries);
		return dataSet;
	}

	@Override
	public void setParameters(String... params) {
		// TODO Auto-generated method stub

	}

}
