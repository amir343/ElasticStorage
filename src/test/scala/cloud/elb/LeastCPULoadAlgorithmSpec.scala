import cloud.elb.{ELBTestData, LeastCPULoadAlgorithm}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import instance.Node


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