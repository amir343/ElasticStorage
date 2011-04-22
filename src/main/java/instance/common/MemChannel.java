package instance.common;

import instance.mem.StartMemoryUnit;
import instance.os.RestartSignal;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class MemChannel extends PortType {
	{
		positive(Ready.class);
		positive(AckBlock.class);
		positive(NAckBlock.class);
		negative(RequestBlock.class);
		negative(WriteBlockIntoMemory.class);
		negative(RestartSignal.class);
		negative(StartMemoryUnit.class);
	}
}
