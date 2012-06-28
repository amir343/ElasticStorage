package instance.common

import se.sics.kompics.Event

//TODO: Event should be removed. This needs to be refactored after migration.

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