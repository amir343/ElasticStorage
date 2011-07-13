package cloud.elb

import se.sics.kompics.Event
import java.util.List
import instance.Node

/**
 * @author Amir Moulavi
 * @date 2011-07-13
 *
 */
class NodesToRemove(val nodes:List[Node]) extends Event