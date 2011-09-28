/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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