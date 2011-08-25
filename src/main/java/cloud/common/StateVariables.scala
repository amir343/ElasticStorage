package cloud.common

import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-15
 * 
 */
trait StateVariables {

  @BeanProperty
  var nrNodes:Int = _
  @BeanProperty
  var cpuLoadMean:Double = _
  @BeanProperty
  var cpuLoadSTD:Double = _
  @BeanProperty
	var bandwidthMean:Double = _
  @BeanProperty
	var periodicTotalCost:Double = _
  @BeanProperty
	var totalCost:Double = _
  @BeanProperty
	var responseTimeMean:Double = _
  @BeanProperty
	var throughputMean:Double = _

  def getStringPresentation: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("\n{\n")
      sb.append("\t# nodes: ").append(nrNodes).append("\n")
      sb.append("\tthroughput: ").append(throughputMean).append("\n")
      sb.append("\tcpuMean: ").append(cpuLoadMean).append("\n")
      sb.append("\tcpuSTD: ").append(cpuLoadSTD).append("\n")
      sb.append("\tbandwidth: ").append(bandwidthMean).append("\n")
      sb.append("\tcost: ").append(periodicTotalCost).append("\n")
      sb.append("\tresponseTime: ").append(responseTimeMean).append("\n")
    sb.append("}")
    sb.toString
  }

}