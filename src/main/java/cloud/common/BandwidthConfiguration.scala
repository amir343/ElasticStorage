package cloud.common

import instance.common.Size

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class BandwidthConfiguration(bandwidth:Double) extends Serializable {

  val cost:BandwidthCost = bandwidth match {
    case m if (bandwidth < 1.0) => BandwidthCost.XLOW
    case m if (bandwidth >= 1.0 && bandwidth < 2.0) => BandwidthCost.LOW
    case m if (bandwidth >= 2.0 && bandwidth < 3.0) => BandwidthCost.MEDIUM
    case m if (bandwidth >= 3.0 && bandwidth < 4.0) => BandwidthCost.HIGH
    case m if (bandwidth >= 4.0 && bandwidth < 5.0) => BandwidthCost.XHIGH
    case _ => BandwidthCost.XXHIGH
  }

  def getBandwidthMegaBytePerSecond: Long = (((bandwidth * 10) * Size.MB.getSize).asInstanceOf[Long]) / 10

  def getBandwidth: Double = bandwidth

  def getCost: BandwidthCost = cost

  override def toString: String = {
      val sb: StringBuilder = new StringBuilder
      sb.append("bandwidth: ").append(bandwidth)
      sb.toString
  }
}