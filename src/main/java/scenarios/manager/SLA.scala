package scenarios.manager

/**
 * @author Amir Moulavi
 * @date 2011-08-12
 */

class SLA extends Serializable {

  private var cpuLoad:Double = _
  private var responseTime:Double = _
  private var cpuLoadList:List[Double] = List[Double]()
  private var rtList:List[Double] = List[Double]()

  def getCpuLoad = cpuLoad

  def getResponseTime = responseTime

  def cpuLoad(load:Double):SLA = {
    cpuLoad = load
    this
  }

  def responseTime(rt:Double):SLA = {
    responseTime = rt
    this
  }

  def addCpuLoad(load:Double) {
    cpuLoadList = cpuLoadList ::: List(load)
  }

  def addResponseTime(rt:Double) {
    rtList = rtList ::: List(rt)
  }

  def getCpuLoadViolation():Double = cpuLoadList.filter(_ >= cpuLoad).size.asInstanceOf[Double] / cpuLoadList.size.asInstanceOf[Double]

  def getResponseTimeViolation():Double = rtList.filter(_ >= responseTime).size.asInstanceOf[Double] / rtList.size.asInstanceOf[Double]

  override def toString():String = {
    val sb:StringBuilder = new StringBuilder
    sb.append("SLA:{")
      .append("cpuLoad: ").append(cpuLoad).append(", ")
      .append("responseTime: ").append(responseTime);
    sb.append("}")
    sb.toString
  }

}