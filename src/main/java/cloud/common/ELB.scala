package cloud.common

import se.sics.kompics.PortType
import cloud.api.{RebalanceResponseMap, RebalanceDataBlocks}

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
  positive(classOf[Replicas])
  positive(classOf[RebalanceResponseMap])

}