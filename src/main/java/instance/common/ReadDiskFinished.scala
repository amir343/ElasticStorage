package instance.common

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 *
 */
class ReadDiskFinished(request:ScheduleTimeout) extends Timeout(request) {

  @BeanProperty var pid:String = _

}