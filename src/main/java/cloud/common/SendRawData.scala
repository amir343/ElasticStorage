package cloud.common

import se.sics.kompics.Event
import se.sics.kompics.address.Address
import scala.reflect.BeanProperty

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
}