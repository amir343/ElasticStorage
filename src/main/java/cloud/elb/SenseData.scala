package cloud.elb

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class SenseData(source:Address, destination:Address, numberOfNodes:Int) extends Message(source, destination) {

  @BeanProperty
  var averageResponseTime: Double = _
  @BeanProperty
  var averageThroughput: Double = _
  @BeanProperty
  var cpuLoad: Double = _
  @BeanProperty
  var bandwidthMean: Double = _
  @BeanProperty
  var totalCost: Double = _

  override def toString: String = {
      val sb: StringBuilder = new StringBuilder
      sb.append("SenseData: \n{\n")
        sb.append("\tcpuAverage: ").append(cpuLoad).append("\n")
        sb.append("\tbandwidthAverage: ").append(bandwidthMean).append("\n")
        sb.append("\ttotalCost: ").append(totalCost).append("\n")
        sb.append("\taverageResponseTime: ").append(averageResponseTime).append("\n")
      sb.append("}")
      sb.toString
    }
  }