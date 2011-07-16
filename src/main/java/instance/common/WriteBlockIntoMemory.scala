package instance.common

import se.sics.kompics.Event

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class WriteBlockIntoMemory(block:Block) extends Event {
  def getBlock = block
}