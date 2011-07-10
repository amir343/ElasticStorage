package cloud.elb

import instance.Node

/**
 * @author Amir Moulavi
 * @date 2011-07-10
 */

trait ELBTestData {
  val node1 = new Node("A", "127.0.0.1", 4551)
  val node2 = new Node("B", "127.0.0.1", 4552)
  val node3 = new Node("C", "127.0.0.1", 4553)
  val node4 = new Node("D", "127.0.0.1", 4554)
  val node5 = new Node("E", "127.0.0.1", 4555)
  val replicas = new java.util.ArrayList[Node]()
  replicas.add(node1)
  replicas.add(node2)
  replicas.add(node3)
  replicas.add(node4)
  replicas.add(node5)
}