package econtroller.controller

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import java.util.{List => JList}

/**
 * @author Amir Moulavi
 *
 */
class ConnectionEstablished(source:Address,
                            destination:Address,
                            nodes:JList[Address])
  extends Message(source, destination) {

  def getNodes = nodes

}