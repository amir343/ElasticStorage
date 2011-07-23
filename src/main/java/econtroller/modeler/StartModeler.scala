package econtroller.modeler

import se.sics.kompics.Event
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-24
 *
 */
class StartModeler(val cloudProvider:Address) extends Event {
  def getCloudProviderAddress = cloudProvider
}