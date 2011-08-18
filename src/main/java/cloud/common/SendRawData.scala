package cloud.common

import se.sics.kompics.Event
import se.sics.kompics.address.Address
import scala.reflect.BeanProperty
import scenarios.manager.SLA

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class SendRawData(val controller:Address,
                  val numberOfNodes:Int,
                  val trainingData:Boolean) extends Event {

  @BeanProperty
  var totalCost: Double = _
  @BeanProperty
  var averageThroughput: Double = _
  @BeanProperty
  var averageResponseTime: Double = _

  var rt:List[Long] = List[Long]()

  def addResponseTime(responseTime:Long) {
    rt = rt ::: List(responseTime)
  }

  def updateSLA(sla:SLA) {
    for (element <- rt)
      sla.addResponseTime(element)
  }

}