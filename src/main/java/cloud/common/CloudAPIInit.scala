package cloud.common

import se.sics.kompics.Init
import scenarios.manager.CloudConfiguration
import scala.reflect.BeanProperty
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class CloudAPIInit(@BeanProperty var cloudConfiguration:CloudConfiguration, @BeanProperty var self:Address) extends Init {

  @BeanProperty
  var period : Long = _
  @BeanProperty
  var delta : Long = _

}