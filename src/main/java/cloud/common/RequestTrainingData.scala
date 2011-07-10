package cloud.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class RequestTrainingData(source:Address, destination:Address) extends Message(source, destination)