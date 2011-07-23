package econtroller.modeler

import se.sics.kompics.Init
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 *
 */
class ModelerInit(val self:Address) extends Init {
  def getSelf = self
}