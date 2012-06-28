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
package cloud.common

import se.sics.kompics.PortType
import cloud.api.{ RebalanceResponseMap, RebalanceDataBlocks }
import cloud.elb.NodesToRemove

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class ELB extends PortType {

  negative(classOf[ELBInit])
  negative(classOf[GetReplicas])
  negative(classOf[SuspectNode])
  negative(classOf[RestoreNode])
  negative(classOf[RemoveReplica])
  negative(classOf[RebalanceDataBlocks])
  negative(classOf[SendRawData])
  negative(classOf[SelectNodesToRemove])
  positive(classOf[Replicas])
  positive(classOf[RebalanceResponseMap])
  positive(classOf[NodesToRemove])

}