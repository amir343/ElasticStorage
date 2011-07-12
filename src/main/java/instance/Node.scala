package instance

import se.sics.kompics.address.Address
import java.net.{UnknownHostException, InetAddress}
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class Node(private val nodeName:String,
           private val address:String,
           private val port:Int) extends Serializable {

  def getNodeName: String = nodeName
  def getIP: String = address
  def getPort: Int = port

  def getAddressStringWithoutName: String = address + ":" + port

  override def toString: String = {
      val sb: StringBuilder = new StringBuilder
      sb.append(nodeName).append("@").append(address).append(":").append(port)
      sb.toString
  }

  def getAddress: Address = {
    var self: Address = null
    try {
      self = new Address(InetAddress.getByName(address), port, 1)
    }
    catch {
      case e: UnknownHostException => {
        e.printStackTrace()
      }
    }
    self
  }

  override def equals(another: Any) = another match {
    case another:Node => another.getIP == this.getIP && another.getNodeName == this.getNodeName && another.getPort == this.getPort
    case _ => false
  }

  override def hashCode: Int = {
    val builder: HashCodeBuilder = new HashCodeBuilder
    builder.append(this.port)
    builder.append(this.address)
    builder.append(this.nodeName)
    builder.toHashCode
  }
}

object Node {

  def fromString(nodeString: String): Node = {
    val tokens: Array[String] = nodeString.split("@")
    val add: Array[String] = tokens(1).split(":")
    new Node(tokens(0), add(0), Integer.parseInt(add(1)))
  }

  def getAddressFromString(string: String): Address = {
    var self: Address = null
    var tokens: Array[String] = string.split(":")
    try {
      self = new Address(InetAddress.getByName(tokens(0)), Integer.parseInt(tokens(1)), 1)
    }
    catch {
      case e: UnknownHostException => {
        e.printStackTrace()
      }
    }
    self
  }


}