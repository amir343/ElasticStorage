package cloud.requestengine

import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class RequestStatistic {

  @BeanProperty
  var start: Long = _
  @BeanProperty
  var end: Long = _

  def getResponseTime:Long = end-start

}