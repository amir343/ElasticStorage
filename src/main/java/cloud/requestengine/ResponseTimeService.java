package cloud.requestengine;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class ResponseTimeService {

	private static ResponseTimeService instance = new ResponseTimeService();
	private XYSeries series = new XYSeries("ResponseTime");
	private Long start = System.currentTimeMillis();
	
	public static ResponseTimeService getInstance() {
		return instance;
	}
	
	private ResponseTimeService() {
		
	}

	public void add(List<RequestStatistic> completedRequest) {
		for (RequestStatistic rs : completedRequest) {
			series.add(rs.getEnd()-start, rs.getResponseTime());
		}		
	}

	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createScatterPlot("Response Time", "Time (ms)", "ResponseTime (ms)", getDataset(), PlotOrientation.VERTICAL, true, true, false);
        final XYPlot plot = chart.getXYPlot();
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	    renderer.setSeriesLinesVisible(0, false);
	    renderer.setSeriesShapesVisible(1, false);

	    Shape shape = new Rectangle2D.Double(-1, -1, 2, 2);
	    renderer.setSeriesShape(0, shape);
	    

	    plot.setRenderer(renderer);

		return chart;
	}

	private XYDataset getDataset() {
		XYSeriesCollection dataSet = new XYSeriesCollection();
		dataSet.addSeries(series);
		return dataSet;
	}
	
	
}
