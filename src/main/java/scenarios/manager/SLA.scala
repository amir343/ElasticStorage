package scenarios.manager

import scala.Double

/**
 * @author Amir Moulavi
 * @date 2011-08-12
 */

class SLA extends Serializable {

  private var cpuLoad:Double = _
  private var responseTime:Long = _
  private var cpuLoadList:List[Double] = List[Double]()
  private var rtList:List[Long] = List[Long]()

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

  def addResponseTime(rt:Long) {
    rtList = rtList ::: List(rt)
  }

  def getCpuLoadViolation():Double = 100.0 * cpuLoadList.filter(_ >= cpuLoad).size.asInstanceOf[Double] / cpuLoadList.size.asInstanceOf[Double]

  def getResponseTimeViolation():Double = 100.0 * rtList.filter(_ >= responseTime).size.asInstanceOf[Double] / rtList.size.asInstanceOf[Double]

  override def toString():String = {
    val sb:StringBuilder = new StringBuilder
    sb.append("SLA:{")
      .append("cpuLoad: ").append(cpuLoad).append(", ")
      .append("responseTime: ").append(responseTime);
    sb.append("}")
    sb.toString
  }

}