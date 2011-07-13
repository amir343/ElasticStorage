package cloud.common

import se.sics.kompics.Event
import java.util.List
import instance.Node

/**
 * @author Amir Moulavi
 * @date 2011-07-13
 *
 */
class SelectNodesToRemove(val nodes:List[Node], val numberOfNodesToRemove:Int) extends Event