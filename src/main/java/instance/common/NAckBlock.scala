package instance.common

import se.sics.kompics.Event
import instance.os.Process

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class NAckBlock(process:Process) extends Event {
  def getProcess = process
}