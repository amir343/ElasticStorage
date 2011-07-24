package cloud.common

import se.sics.kompics.Event
import instance.common.Request

/**
 * @author Amir Moulavi
 * @date 2011-07-24
 *
 */
class DownloadRejected(val request:Request) extends Event {
  def getRequest = request
}