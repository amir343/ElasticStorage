package cloud.common

import se.sics.kompics.Event
import se.sics.kompics.address.Address
import scala.reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class SendRawData(@BeanProperty var controller:Address,
                  @BeanProperty var numberOfNodes:Int,
                  @BeanProperty var trainingData:Boolean) extends Event {

  @BeanProperty
  var totalCost: Double = _
  @BeanProperty
  var averageThroughput: Double = _
  @BeanProperty
  var averageResponseTime: Double = _
}