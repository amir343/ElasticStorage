package instance.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-24
 *
 */
class Rejected(val source:Address,
               val destination:Address,
               val request:Request)
  extends Message(source, destination) {

  def getRequest = request

}