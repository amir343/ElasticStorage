package instance.os

import org.jfree.chart.JFreeChart

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class InstanceSnapshot(id:Int) extends Snapshot(id) {

  private var cpuChart:JFreeChart = _
  private var bandwidthChart:JFreeChart = _
  private var log:String = _

  def addCPULoadChart(chart: JFreeChart) = this.cpuChart = chart

  def addBandwidthChart(chart: JFreeChart) = this.bandwidthChart = chart

  def addLogText(log: String) = this.log = log

  def getCpuChart: JFreeChart = cpuChart

  def getBandwidthChart: JFreeChart = bandwidthChart

  def getLog: String = log

  def getId = id

}