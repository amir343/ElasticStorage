package econtroller.controller

import se.sics.kompics.Event

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class StartSense(val senseTimeout:Int) extends Event {
  def getSenseTimeout = senseTimeout
}