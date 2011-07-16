package instance.os

import se.sics.kompics.Event
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class CPULoad(@BeanProperty var cpuLoad:Double) extends Event