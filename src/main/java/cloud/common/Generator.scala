package cloud.common

import se.sics.kompics.PortType
import cloud.requestengine.{DownloadStarted, RequestGeneratorInit}
import cloud.elb.BlocksActivated
import instance.common.Request

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class Generator extends PortType {

  negative(classOf[RequestGeneratorInit])
  negative(classOf[DownloadStarted])
  negative(classOf[DownloadRejected])
  negative(classOf[SendRawData])
  negative(classOf[BlocksActivated])
  positive(classOf[SendRawData])
  positive(classOf[Request])

}