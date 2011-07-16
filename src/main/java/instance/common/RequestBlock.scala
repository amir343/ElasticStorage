package instance.common

import se.sics.kompics.Event
import instance.os.Process

/**
 * @author Amir Moulavi
 *
 */
class RequestBlock(process:Process) extends Event {

  def getProcess = process

}