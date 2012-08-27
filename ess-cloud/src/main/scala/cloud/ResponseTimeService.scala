package cloud

import org.jfree.data.xy.XYSeries
import org.jfree.chart.ChartFactory
import java.awt.geom.Rectangle2D
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.JFreeChart

object ResponseTimeService {

  val series = new XYSeries("ResponseTime");
  val start = System.currentTimeMillis();

  def add(completedRequests: List[RequestStatistic]) {
    completedRequests foreach { point ⇒ series.add(point.end - start, point.responseTime) }
  }

  def chart: JFreeChart = {
    val dataSet = new XYSeriesCollection()
    dataSet.addSeries(series);
    val chart = ChartFactory.createScatterPlot("Response Time", "Time (ms)", "ResponseTime (ms)", dataSet, PlotOrientation.VERTICAL, true, true, false)
    val plot = chart.getXYPlot()
    val renderer = new XYLineAndShapeRenderer()
    renderer.setSeriesLinesVisible(0, false)
    renderer.setSeriesShapesVisible(1, false)
    val shape = new Rectangle2D.Double(-1, -1, 2, 2)
    renderer.setSeriesShape(0, shape)
    plot.setRenderer(renderer)
    chart
  }

}