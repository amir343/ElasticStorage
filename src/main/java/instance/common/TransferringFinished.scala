package instance.common

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class TransferringFinished(request:ScheduleTimeout) extends Timeout(request) {
  @BeanProperty var pid:String = _
}