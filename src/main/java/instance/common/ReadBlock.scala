package instance.common

import se.sics.kompics.Event
import instance.os.Process
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class ReadBlock(id:String) extends Event {

  @BeanProperty var process:Process = _

  def getId = id

}