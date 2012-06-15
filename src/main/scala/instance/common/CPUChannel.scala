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
package instance.common

import instance.os.{RestartSignal, SnapshotRequest, CPULoad}
import se.sics.kompics.PortType

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class CPUChannel extends PortType {

  positive(classOf[Ready])
  positive(classOf[CPULoad])
  positive(classOf[SnapshotRequest])
  negative(classOf[StartProcess])
  negative(classOf[EndProcess])
  negative(classOf[AbstractOperation])
  negative(classOf[RestartSignal])
  negative(classOf[SnapshotRequest])

}