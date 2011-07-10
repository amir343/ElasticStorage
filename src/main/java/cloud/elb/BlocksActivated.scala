package cloud.elb

import se.sics.kompics.Event
import instance.common.Block
import java.util.{Collections, Set => JSet, HashSet => JHashSet, List => JList}

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class BlocksActivated(blks:JList[Block]) extends Event {

  val blocks: JSet[Block] = new JHashSet[Block]

  blocks.addAll(blks)

  def this() = this(Collections.emptyList())

  def addBlock(block:Block) = {
    blocks.add(block)
  }

}