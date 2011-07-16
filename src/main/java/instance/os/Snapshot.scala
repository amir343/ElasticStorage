package instance.os

import scala.reflect.BeanProperty
import java.util.{Calendar, Date => JDate}

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class Snapshot(@BeanProperty var id:Int) {

  @BeanProperty var date:JDate = Calendar.getInstance().getTime

}