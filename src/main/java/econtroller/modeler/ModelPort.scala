package econtroller.modeler

import se.sics.kompics.PortType
import cloud.elb.SenseData

/**
 * @author Amir Moulavi
 * @date 2011-07-24
 *
 */
class ModelPort extends PortType {

  negative(classOf[StartModeler])
  negative(classOf[SenseData])

}