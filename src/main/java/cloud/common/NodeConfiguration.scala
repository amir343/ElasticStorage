package cloud.common

import instance.Node
import se.sics.kompics.address.Address
import instance.common.Block
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class NodeConfiguration(cpuSpeed:Double,
                        bandwidth:Double,
                        @BeanProperty var memory:Int,
                        @BeanProperty var simultaneousDownloads:Int) extends Serializable {

  def this() = this(2.0, 2.0, 4, 20)

  @BeanProperty
  var cpuConfiguration: CpuConfiguration = new CpuConfiguration (cpuSpeed)
  @BeanProperty
  var bandwidthConfiguration: BandwidthConfiguration = new BandwidthConfiguration(bandwidth)
  @BeanProperty
  var name: String = "NONAME"
  @BeanProperty
  var node: Node = null
  @BeanProperty
  var blocks: java.util.List[Block] = null
  @BeanProperty
  var dataBlocksMap: java.util.Map[String, Address] = null


  def setNodeInfo(node:Node) = {
    this.node = node
    this.name = node.getNodeName
  }

  def cpu(speed: Double): NodeConfiguration = {
    this.cpuConfiguration = new CpuConfiguration(speed)
    this
  }

  def memoryGB(size: Int): NodeConfiguration = {
    this.memory = size
    this
  }

  def bandwidthMB(bandwidth: Double): NodeConfiguration = {
    this.bandwidthConfiguration = new BandwidthConfiguration(bandwidth)
    this
  }

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("{")
    sb.append(cpuConfiguration).append(",")
    sb.append("memory: ").append(memory).append(",")
    sb.append(bandwidthConfiguration).append(",")
    sb.append("simDownloads: ").append(simultaneousDownloads)
    sb.append("}")
    sb.toString
  }
}