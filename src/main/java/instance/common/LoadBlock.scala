package instance.common

import se.sics.kompics.Event
import java.util.{List => JList}

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class LoadBlock(blocks:JList[Block]) extends Event {
  def getBlocks = blocks
}