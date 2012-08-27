package instance.common

//TODO: Event should be removed. This needs to be refactored after migration.

abstract class AbstractOperation extends Operation

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */

trait Operation {
  def getDuration(cpuSpeed: Long): Long

  def getNumberOfOperations(): Int
}