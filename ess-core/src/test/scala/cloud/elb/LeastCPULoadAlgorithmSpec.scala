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

import cloud.elb.{ELBTestData, LeastCPULoadAlgorithm}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import instance.Node
import java.util.{List => JList}


@RunWith(classOf[JUnitRunner])
class LeastCPULoadAlgorithmSpec extends FlatSpec with ShouldMatchers with ELBTestData {

  val algorithm:LeastCPULoadAlgorithm = LeastCPULoadAlgorithm.getInstance()

  "algorithm" should "return the least CPU load from a set of nodes" in {
    algorithm.clear
    cpuLoad(List(34.0, 2.01, 0.01, 22.01, 78.2))
    algorithm.getNextNodeFrom(replicas) should equal (node3)

  }

  "algorithm" should "return the node with least number of previously sent request when the cpu loads are equal" in {
    algorithm.clear
    cpuLoad(List(34.0, 34.0, 34.0, 34.0, 34.0))
    sentRequests(List(10, 1, 130, 140, 160))
    algorithm.getNextNodeFrom(replicas) should equal (node2)
  }

  "algorithm" should "return the node with least cpu Load and least number of previously sent request" in {
    algorithm.clear
    cpuLoad(List(3.0, 134.0, 56.0, 45.0, 62.0))
    sentRequests(List(10, 1, 130, 140, 160))
    algorithm.getNextNodeFrom(replicas) should equal (node1)
  }

  "algorithm" should "return the node with least cpu Load and least number of previously sent request (a second variant)" in {
    algorithm.clear
    cpuLoad(List(3.0, 3.0, 56.0, 45.0, 62.0))
    sentRequests(List(10, 1, 120, 140, 160))
    algorithm.getNextNodeFrom(replicas) should equal (node2)
  }

  "algorithm" should "select the nodes with least cpu loads to be removed" in {
    algorithm.clear
    cpuLoad(List(3.0, 0.0, 34.2, 45.0, 62.0))
    sentRequests(List(10, 1, 120, 140, 160))
    val result = algorithm.selectNodesToRemove(replicas, 3)
    result.size() should equal (3)
    result.get(0) should equal (node2)
    result.get(1) should equal (node1)
    result.get(2) should equal (node3)
  }

  "algorithm" should "select all the members when the request number of nodes to be removed is greater than the actual number of nodes" in {
    algorithm.clear
    cpuLoad(List(3.0, 0.0, 34.2, 45.0, 62.0))
    sentRequests(List(10, 1, 120, 140, 160))
    val result = algorithm.selectNodesToRemove(replicas, 7)
    result.size() should equal (5)
    result.get(0) should equal (node2)
    result.get(1) should equal (node1)
    result.get(2) should equal (node3)
    result.get(3) should equal (node4)
    result.get(4) should equal (node5)
  }

  def sentRequests(list:List[Int]) = {
    for ( (n, node) <- list.zip(replicas.toArray.toList))
      for (i <- 0 to n) algorithm.increaseNrOfSentRequestFor(node.asInstanceOf[Node])
  }

  def cpuLoad(list:List[Double]) = {
    for ( (n, node) <- list.zip(replicas.toArray.toList))
      algorithm.updateCPULoadFor(node.asInstanceOf[Node], n)
  }

  type ? = this.type
}