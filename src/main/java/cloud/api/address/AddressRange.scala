package cloud.api.address

import scala.reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-15
 *
 */
class AddressRange(@BeanProperty var ip:String,
                   @BeanProperty var startPort:Int,
                   @BeanProperty var endPort:Int) {
}