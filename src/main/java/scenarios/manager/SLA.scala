package scenarios.manager

import scala.Double

/**
 * @author Amir Moulavi
 * @date 2011-08-12
 */

class SLA extends Serializable {

  private var cpuLoad:Double = _
  private var responseTime:Long = _
  private var bandwidth:Double = _
  private var cpuLoadList:List[Double] = List[Double]()
  private var rtList:List[Long] = List[Long]()
  private var bandwidthList:List[Double] = List[Double]()

  def getCpuLoad = cpuLoad

  def getResponseTime = responseTime

  def cpuLoad(load:Double):SLA = {
    cpuLoad = load
    this
  }

  def cpuLoad(load:Int):SLA = {
    cpuLoad = load.asInstanceOf[Double]
    this
  }

  def bandwidth(b:Long):SLA = {
    bandwidth =  b.asInstanceOf[Double]
    this
  }

  def responseTime(rt:Long):SLA = {
    responseTime = rt
    this
  }

  def responseTime(rt:Int):SLA = {
    responseTime = rt.asInstanceOf[Long]
    this
  }

  def addCpuLoad(load:Double) {
    cpuLoadList = cpuLoadList ::: List(load)
  }

  def addBandwidth(band:Double) {
    bandwidthList = bandwidthList ::: List(band)
  }

  def addResponseTime(rt:Long) {
    rtList = rtList ::: List(rt)
  }

  def getCpuLoadViolation():Double = 100.0 * cpuLoadList.filter(_ >= cpuLoad).size.asInstanceOf[Double] / cpuLoadList.size.asInstanceOf[Double]

  def getResponseTimeViolation():Double = 100.0 * rtList.filter(_ >= responseTime).size.asInstanceOf[Double] / rtList.size.asInstanceOf[Double]

  def getBandwidthViolation():Double = 100.0 * bandwidthList.filter(_ <= bandwidth).size.asInstanceOf[Double] / bandwidthList.size.asInstanceOf[Double]

  override def toString():String = {
    val sb:StringBuilder = new StringBuilder
    sb.append("SLA:{")
      .append("cpuLoad: ").append(cpuLoad).append(", ")
      .append("bandwidth: ").append(bandwidth).append(", ")
      .append("responseTime: ").append(responseTime);
    sb.append("}")
    sb.toString
  }

}