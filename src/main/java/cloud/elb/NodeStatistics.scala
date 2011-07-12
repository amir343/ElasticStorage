package cloud.elb

import instance.Node
import scala.reflect._

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class NodeStatistics(val node:Node) {

  @BeanProperty
  var cpuLoad: Double = _

  private var nrOfSentRequest: Int = _

  def increaseNrOfSentRequest(): Unit = {
      nrOfSentRequest += 1
  }

  def getNrOfSentRequest: Int = {
    return nrOfSentRequest
  }
}