package instance.os

import se.sics.kompics.Event
import reflect.BeanProperty
import org.jfree.chart.JFreeChart

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class SnapshotRequest extends Event {

  @BeanProperty var cpuLoadChart:JFreeChart = _

}