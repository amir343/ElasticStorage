package econtroller.sensor

import se.sics.kompics.PortType
import econtroller.controller.{StartSense, Sense}
import cloud.elb.SenseData

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class SensorChannel extends PortType {

  negative(classOf[Sense])
  negative(classOf[StartSense])
  negative(classOf[StopSense])
  positive(classOf[SenseData])

}