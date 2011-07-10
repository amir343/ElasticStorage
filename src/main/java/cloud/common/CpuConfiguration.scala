package cloud.common

import instance.common.Size

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class CpuConfiguration(cpuSpeed:Double) extends Serializable {

  val cost:CPUCost = cpuSpeed match {
    case m if cpuSpeed < 1.8 => CPUCost.MICRO
    case m if (cpuSpeed >= 1.8 && cpuSpeed < 2.0) => CPUCost.XSMALL
    case m if (cpuSpeed >= 2.0 && cpuSpeed < 2.4) => CPUCost.SMALL
    case m if (cpuSpeed >= 2.4 && cpuSpeed < 2.8) => CPUCost.LARGE
    case m if (cpuSpeed >= 2.8 && cpuSpeed < 3.2) => CPUCost.XLARGE
    case m if (cpuSpeed >= 3.2 && cpuSpeed < 3.8) => CPUCost.XXLARGE
    case _ => CPUCost.XXXLARGE
  }

  def getCost:CPUCost = cost

  def getCpuSpeedInstructionPerSecond: Long = (((cpuSpeed * 10) * Size.GHertz.getSize).asInstanceOf[Long]) / 10

  def getCpuSpeed: Double = cpuSpeed

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("cpu: ").append(cpuSpeed)
    sb.toString
  }
}