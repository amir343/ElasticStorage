package cloud.requestengine

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class DownloadStarted(val source:Address,
                      val destination:Address,
                      val requestID:String)
  extends Message(source, destination)