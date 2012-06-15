package instance.common

import se.sics.kompics.Event

abstract class AbstractOperation extends Event with Operation

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */

trait Operation {
  def getDuration(cpuSpeed: Long): Long

  def getNumberOfOperations(): Int
}