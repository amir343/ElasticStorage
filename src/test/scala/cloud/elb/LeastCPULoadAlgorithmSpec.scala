import cloud.elb.{ELBTestData, LeastCPULoadAlgorithm}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import instance.Node


@RunWith(classOf[JUnitRunner])
class LeastCPULoadAlgorithmSpec extends FlatSpec with ShouldMatchers with ELBTestData {

  "algorithm" should "return the least CPU load from a set of nodes" in {
    val algorithm:LeastCPULoadAlgorithm = LeastCPULoadAlgorithm.getInstance()
    algorithm.updateCPULoadFor(node1, 34.0)
    algorithm.updateCPULoadFor(node2, 2.01)
    algorithm.updateCPULoadFor(node3, 0.01)
    algorithm.updateCPULoadFor(node4, 22.01)
    algorithm.updateCPULoadFor(node5, 78.1)
    algorithm.getNextNodeFrom(replicas) should equal (node3)

  }

  "algorithm" should "return the node with least number of previously sent request when the cpu loads are equal" in {
    val algorithm:LeastCPULoadAlgorithm = LeastCPULoadAlgorithm.getInstance()
    algorithm.updateCPULoadFor(node1, 34.0)
    algorithm.updateCPULoadFor(node2, 34.0)
    algorithm.updateCPULoadFor(node3, 34.0)
    algorithm.updateCPULoadFor(node4, 34.0)
    algorithm.updateCPULoadFor(node5, 34.0)
    increase(10, node1, algorithm)
    increase(1, node2, algorithm)
    increase(130, node3, algorithm)
    increase(140, node4, algorithm)
    increase(160, node5, algorithm)
    algorithm.getNextNodeFrom(replicas) should equal (node2)
  }

  def increase(n:Int, node:Node, algorithm:LeastCPULoadAlgorithm):LeastCPULoadAlgorithm = {
    for (i <- 0 to n) algorithm.increaseNrOfSentRequestFor(node)
    algorithm
  }
}