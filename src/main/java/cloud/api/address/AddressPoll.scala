package cloud.api.address
import java.util.{List => JList}
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class AddressPoll {
  @BeanProperty var addresses:JList[AddressRange] = _
}