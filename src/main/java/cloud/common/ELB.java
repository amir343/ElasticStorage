package cloud.common;

import cloud.api.RebalanceDataBlocks;
import cloud.api.RebalanceResponseMap;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class ELB extends PortType {
	{
		negative(ELBInit.class);
		negative(GetReplicas.class);
		negative(SuspectNode.class);
		negative(RestoreNode.class);
		negative(RemoveReplica.class);
		negative(RebalanceDataBlocks.class);
		positive(Replicas.class);
		positive(RebalanceResponseMap.class);
	}
}
