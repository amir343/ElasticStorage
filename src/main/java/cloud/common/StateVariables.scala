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
	var totalCost:Double = _
  @BeanProperty
	var responseTimeMean:Double = _
  @BeanProperty
	var throughputMean:Double = _

  def getStringPresentation: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("{")
      sb.append("# nodes: ").append(nrNodes).append(", ")
      sb.append("throughput: ").append(throughputMean).append(", ")
      sb.append("cpuMean: ").append(cpuLoadMean).append(", ")
      sb.append("cpuSTD: ").append(cpuLoadSTD).append(", ")
      sb.append("bandwidth: ").append(bandwidthMean).append(", ")
      sb.append("cost: ").append(totalCost).append(", ")
      sb.append("responseTime: ").append(responseTimeMean)
    sb.append("}")
    sb.toString
  }

}