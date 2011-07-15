package cloud.elb

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import cloud.common.StateVariables

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class SenseData(source:Address, destination:Address) extends Message(source, destination) with StateVariables {

  override def toString: String = getStringPresentation

}