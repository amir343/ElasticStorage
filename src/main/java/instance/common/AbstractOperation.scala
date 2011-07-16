package instance.common

import se.sics.kompics.Event

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */

trait Operation {
  def getDuration(cpuSpeed:Long):Long
  def getNumberOfOperations():Int
}

abstract class AbstractOperation extends Event with Operation