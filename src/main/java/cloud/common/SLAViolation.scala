package cloud.common

import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-08-12
 */

trait SLAViolation {

  @BeanProperty
  var cpuLoadViolation:Double = _

  @BeanProperty
  var responseTimeViolation:Double = _

  @BeanProperty
  var bandwidthViolation:Double = _

}