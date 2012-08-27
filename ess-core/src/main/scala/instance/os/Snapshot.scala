/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package instance.os

import scala.reflect.BeanProperty
import java.util.{ Calendar, Date ⇒ JDate }
import org.jfree.chart.JFreeChart

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class Snapshot(val id: Int, @BeanProperty val date: JDate = Calendar.getInstance().getTime)

class CloudSnapshot(override val id: Int) extends Snapshot(id) {
  @BeanProperty
  var chart: JFreeChart = _
  @BeanProperty
  var logText: String = _

  def getId: Int = id

}

class InstanceSnapshot(override val id: Int) extends Snapshot(id) {
  @BeanProperty
  var cpuChart: JFreeChart = _
  @BeanProperty
  var bandwidthChart: JFreeChart = _
  @BeanProperty
  var log: String = _

  def getId: Int = id

}