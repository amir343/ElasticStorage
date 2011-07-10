package cloud.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import scala.reflect.BeanProperty

/*
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class TrainingData(source:Address, destination:Address) extends Message(source, destination) {

  @BeanProperty
  var nrNodes:Int = _
  @BeanProperty
  var cpuLoadMean:Double = _
  @BeanProperty
	var bandwidthMean:Double = _
  @BeanProperty
	var totalCost:Double = _
  @BeanProperty
	var responseTimeMean:Double = _
  @BeanProperty
	var throughputMean:Double = _

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("{")
      sb.append("# nodes: ").append(nrNodes).append(", ")
      sb.append("throughput: ").append(throughputMean).append(", ")
      sb.append("cpu: ").append(cpuLoadMean).append(", ")
      sb.append("bandwidth: ").append(bandwidthMean).append(", ")
      sb.append("cost: ").append(totalCost).append(", ")
      sb.append("responseTime: ").append(responseTimeMean)
    sb.append("}")
    sb.toString
  }
}